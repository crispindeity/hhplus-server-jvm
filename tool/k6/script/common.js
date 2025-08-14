import http from 'k6/http';
import {check, sleep} from 'k6';

export const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
export const CONCERT_IDS = ['1'];
export const MAX_RESERVATION_ATTEMPTS = Number(__ENV.MAX_RESERVATION_ATTEMPTS || 20);

function getHeaderValueCaseInsensitive(response, headerName) {
  const matchedKey = Object.keys(response.headers).find(
      (key) => key.toLowerCase() === headerName.toLowerCase());
  return matchedKey ? response.headers[matchedKey] : null;
}

function parseJsonSafely(response) {
  try {
    return response.json();
  } catch {
    return null;
  }
}

function requestWithLog(method, url, body, params, checkName, checkFn, suppressOn) {
  const res = http.request(method, url, body, params);
  const ok = check(res, {[checkName]: checkFn});
  if (!ok) {
    if (!(typeof suppressOn === 'function' && suppressOn(res))) {
      console.log(`[FAIL] ${method} ${url} status=${res.status} body=${res.body}`);
    }
  }
  return res;
}

function fetchAvailableDates(concertId, authHeaders) {
  while (true) {
    const res = requestWithLog(
        'GET',
        `${BASE_URL}/api/concerts/${concertId}/reservations/available-dates`,
        null,
        {...authHeaders, tags: {step: 'dates'}},
        '날짜 조회 HTTP 200',
        (r) => r.status === 200
    );

    const body = parseJsonSafely(res);

    if (body?.code === 400 && body?.message === 'queue not yet allowed.') {
      sleep(0.1);
      continue;
    }

    const dates = body?.result?.dates ?? [];

    check(dates, {'예약 가능 날짜 확인': (arr) => Array.isArray(arr)})

    return dates;
  }
}

function fetchAvailableSeats(concertId, dateString, authHeaders) {
  const res = requestWithLog(
      'GET',
      `${BASE_URL}/api/concerts/${concertId}/reservations/available-seats?date=${encodeURIComponent(
          dateString)}`,
      null,
      {...authHeaders, tags: {step: 'seats'}},
      '좌석 조회 HTTP 200',
      (r) => r.status === 200
  );
  const body = parseJsonSafely(res);
  return body?.result?.seats ?? [];
}

function tryCreateReservationWithRetries({concertId, dateString, authHeaders, maxAttempts}) {
  let attempts = 0;
  let lastBody = ""
  while (attempts < maxAttempts) {
    attempts += 1;
    const seats = fetchAvailableSeats(concertId, dateString, authHeaders);
    if (!Array.isArray(seats) || seats.length === 0) {
      check(null, {'예약 실패: 좌석 소진': () => true});
      return false;
    }
    const randomSeat = seats[Math.floor(Math.random() * seats.length)];
    const seatId = randomSeat.id;
    const res = requestWithLog(
        'POST',
        `${BASE_URL}/api/reservations`,
        JSON.stringify({date: dateString, seat: seatId}),
        {
          headers: {...authHeaders.headers, 'Content-Type': 'application/json'},
          tags: {step: 'reserve'}
        },
        '예약 생성 응답 수신',
        (r) => r.status >= 200 && r.status < 600,
        (r) => {
          const b = parseJsonSafely(r);
          const code = b?.code;
          const msg = String(b?.message || '');
          return r.status === 409 || (code === 400 && msg === 'already reserved.');
        }
    );
    const body = parseJsonSafely(res);
    const code = body?.code;
    const msg = String(body?.message || '');
    if (res.status === 409 || (code === 400 && msg === 'already reserved.')) {
      sleep(0.05);
      continue;
    }
    const success = code === 200 && msg === 'success';
    if (success) {
      check(res, {'예약 생성 성공': () => true});
      return true;
    } else {
      sleep(0.05);
    }
    lastBody = body
  }
  console.log(`[FAIL] reservation unexpected resp=${JSON.stringify(lastBody)}`);
  return false;
}

function is2xxCode(val) {
  if (typeof val === 'number') {
    return val >= 200 && val < 300;
  }
  if (typeof val === 'string') {
    const n = parseInt(val, 10);
    return !isNaN(n) && n >= 200 && n < 300;
  }
  return false;
}

function attemptPaymentWithTopUp({userId, authHeaders}) {
  let res = requestWithLog(
      'POST',
      `${BASE_URL}/api/payments`,
      null,
      {...authHeaders, tags: {step: 'payment'}},
      '결제 HTTP 200',
      (r) => r.status === 200
  );
  let body = parseJsonSafely(res);
  let code = body?.code;
  if (body && !is2xxCode(code)) {
    const msg = String(body?.message || '');
    const notEnough = msg.startsWith('not enough point to complete the operation');
    if (notEnough) {
      const m = msg.match(/amount:\s*(\d+),\s*balance:\s*(\d+)/);
      if (m) {
        const requiredAmount = parseInt(m[1], 10);
        const currentBalance = parseInt(m[2], 10);
        const requiredTopUp = Math.max(requiredAmount - currentBalance, 0);
        const chargeRes = requestWithLog(
            'POST',
            `${BASE_URL}/api/users/${userId}/points/charge`,
            JSON.stringify({amount: requiredTopUp}),
            {
              headers: {...authHeaders.headers, 'Content-Type': 'application/json'},
              tags: {step: 'charge'}
            },
            '포인트 충전 HTTP 200/201',
            (r) => r.status === 200 || r.status === 201
        );
        const chargeBody = parseJsonSafely(chargeRes);
        if (!(chargeRes.status === 200 || chargeRes.status === 201)) {
          console.log(`[FAIL] charge resp=${JSON.stringify(chargeBody)}`);
        }
        res = requestWithLog(
            'POST',
            `${BASE_URL}/api/payments`,
            null,
            {...authHeaders, tags: {step: 'payment'}},
            '재결제 HTTP 200',
            (r) => r.status === 200
        );
        body = parseJsonSafely(res);
        code = body?.code;
      }
    }
  }
  const finalMsg = String(body?.message || '');
  const alreadyPaid = /already\s*paid/i.test(finalMsg);
  const success = is2xxCode(code) || alreadyPaid;
  if (!success) {
    console.log(`[FAIL] payment userId=${userId} resp=${JSON.stringify(body)}`);
  }
  check(body, {'결제 응답 바디 존재': (b) => !!b});
  check({success}, {'결제 성공 처리(2xx/이미결제)': (v) => v.success === true});
  return success ? 200 : code;
}

export function runFlowWithUserId(userId, { concertIds = CONCERT_IDS } = {}) {
  const tokenRes = requestWithLog(
      'POST',
      `${BASE_URL}/api/queue/entry-token`,
      JSON.stringify({userId}),
      {
        headers: {'Content-Type': 'application/json', accept: 'application/json'},
        tags: {step: 'token'}
      },
      '토큰 발급 HTTP 200',
      (r) => r.status === 200
  );
  const entryQueueToken = getHeaderValueCaseInsensitive(tokenRes, 'EntryQueueToken');
  check(tokenRes,
      {'토큰 헤더 존재': () => typeof entryQueueToken === 'string' && entryQueueToken.length > 0})
  || console.log(`[FAIL] missing token header body=${tokenRes.body}`);
  if (!entryQueueToken) {
    return;
  }
  const authHeaders = {headers: {accept: 'application/json', EntryQueueToken: entryQueueToken}};
  const chosenConcertId = concertIds[Math.floor(Math.random() * concertIds.length)];
  const availableDates = fetchAvailableDates(chosenConcertId, authHeaders);
  if (!Array.isArray(availableDates) || availableDates.length === 0) {
    return;
  }
  const selectedDate = availableDates[Math.floor(Math.random() * availableDates.length)];
  const reservationCreated = tryCreateReservationWithRetries({
    concertId: chosenConcertId,
    dateString: selectedDate,
    authHeaders,
    maxAttempts: MAX_RESERVATION_ATTEMPTS
  });
  if (!reservationCreated) {
    return;
  }
  attemptPaymentWithTopUp({userId, authHeaders});
}
