import {USER_IDS} from './user_ids.js';
import {runFlowWithUserId} from './common.js';

export const options = {
  vus: Number(__ENV.VUS || 1),
  iterations: USER_IDS.length,
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
  const userId = USER_IDS[__ITER];
  runFlowWithUserId(userId);
}
