import {SharedArray} from 'k6/data';
import exec from 'k6/execution';
import papaparse from 'https://jslib.k6.io/papaparse/5.1.1/index.js';
import {runFlowWithUserId} from './common.js';

const VUS = Number(__ENV.VUS || 50);
const CSV_PATH = __ENV.USERS_CSV || '../../../data/csv/users.csv';

const allUsers = new SharedArray('users', () => {
  const text = open(CSV_PATH);
  const rows = papaparse.parse(text, {header: false}).data;
  return rows.map(r => r && r[1]).filter(Boolean);
});

export const options = {
  stages: [
    {duration: '30s', target: 30},
    {duration: '1m', target: 50},
    {duration: '3m', target: 40},
    {duration: '4m', target: 25},
    {duration: '1m', target: 35},
    {duration: '30s', target: 0},
  ],
  thresholds: {
    checks: ['rate>0.99'],
    'http_req_duration{step:token}': ['p(95)<500'],
    'http_req_duration{step:dates}': ['p(95)<500'],
    'http_req_duration{step:seats}': ['p(95)<500'],
    'http_req_duration{step:reserve}': ['p(95)<800'],
    'http_req_duration{step:payment}': ['p(95)<800'],
    'http_req_duration{step:charge}': ['p(95)<800'],
    'http_req_duration': ['p(99)<2000'],
    'http_req_failed': ['rate<0.05'],
  },
};

let myUsers = [];
let myUserIndex = 0;
let initialized = false;

function initializeMyUsers() {
  if (initialized) {
    return;
  }
  const vuId = exec.vu.idInTest;
  const totalUsers = allUsers.length;
  const usersPerVU = Math.floor(totalUsers / VUS);
  const startIndex = (vuId - 1) * usersPerVU;
  const endIndex = vuId === VUS ? totalUsers : vuId * usersPerVU;
  myUsers = allUsers.slice(startIndex, endIndex);
  myUserIndex = 0;
  initialized = true;
  console.log(`VU ${vuId}: ${myUsers.length} users assigned (${startIndex} to ${endIndex - 1})`);
}

function getNextUserId() {
  initializeMyUsers();
  if (myUsers.length === 0) {
    console.warn(`VU ${exec.vu.idInTest}: No users assigned`);
    return null;
  }
  const userId = myUsers[myUserIndex];
  myUserIndex = (myUserIndex + 1) % myUsers.length;
  return userId;
}

export default function () {
  const userId = getNextUserId();
  const concertIds = Array.from({ length: 100 }, (_, i) => String(i + 1));
  if (!userId) {
    return;
  }
  runFlowWithUserId(userId, concertIds);
}
