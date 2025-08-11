import exec from 'k6/execution';
import {USER_IDS} from './user_ids.js';
import {runFlowWithUserId} from './common.js';

export const options = {
  scenarios: {
    one_user_per_vu: {
      executor: 'per-vu-iterations',
      vus: USER_IDS.length,
      iterations: 1,
      maxDuration: '5m',
    },
  },
  thresholds: {
    checks: ['rate>0.99'],
    'http_req_duration{step:token}': ['p(95)<500'],
    'http_req_duration{step:dates}': ['p(95)<500'],
    'http_req_duration{step:seats}': ['p(95)<500'],
    'http_req_duration{step:reserve}': ['p(95)<800'],
    'http_req_duration{step:payment}': ['p(95)<800'],
    'http_req_duration{step:charge}': ['p(95)<800'],
  },
};

export default function () {
  const index = exec.vu.idInTest - 1;
  if (index < 0 || index >= USER_IDS.length) {
    return;
  }
  const userId = USER_IDS[index];
  runFlowWithUserId(userId);
}
