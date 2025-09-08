// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** getConsecutiveSignDaysFromRedis POST /api/getConsecutiveSignDays */
export async function getConsecutiveSignDaysFromRedisUsingPost(options?: { [key: string]: any }) {
  return request<API.BaseResponseint>('/api/getConsecutiveSignDays', {
    method: 'POST',
    ...(options || {}),
  });
}

/** monthlySign POST /api/sign */
export async function monthlySignUsingPost(options?: { [key: string]: any }) {
  return request<API.BaseResponseboolean>('/api/sign', {
    method: 'POST',
    ...(options || {}),
  });
}

/** monthlySignTotal POST /api/signTotal */
export async function monthlySignTotalUsingPost(options?: { [key: string]: any }) {
  return request<API.BaseResponseint>('/api/signTotal', {
    method: 'POST',
    ...(options || {}),
  });
}
