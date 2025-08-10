import http from 'k6/http';
import { check } from 'k6';
import { USER_IDS } from './user_ids.js';

const BASE_URL = 'http://localhost:8080';

export let options = {
  vus: 10,
  iterations: USER_IDS.length,
};

export default function () {
  const userId = USER_IDS[__ITER];
  const tokenRes = http.post(
      `${BASE_URL}/api/queue/entry-token`,
      JSON.stringify({ userId }),
      { headers: { 'Content-Type': 'application/json', 'accept': 'application/json' } }
  );
  const tokenHeaderKey = Object.keys(tokenRes.headers).find(k => k.toLowerCase() === 'entryqueuetoken');
  check(tokenRes, {
    '토큰 발급 성공': r => r.status === 200,
    '토큰 헤더 존재': r => !!tokenHeaderKey && Array.isArray(r.headers[tokenHeaderKey]) && r.headers[tokenHeaderKey].length > 0,
  });
  const entryQueueToken = tokenHeaderKey ? tokenRes.headers[tokenHeaderKey][0] : null;
  if (entryQueueToken) {
    const dateRes = http.get(
        `${BASE_URL}/api/concerts/1/reservations/available-dates`,
        { headers: { 'accept': 'application/json', 'EntryQueueToken': entryQueueToken } }
    );
    check(dateRes, { '날짜 조회 성공': r => r.status === 200 });
  }
}
