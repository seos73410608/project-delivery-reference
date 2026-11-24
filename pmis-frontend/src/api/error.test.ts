import { describe, expect, it } from 'vitest';
import type { AxiosError } from 'axios';

import { getApiErrorMessage } from './error';

describe('getApiErrorMessage', () => {
  const fallbackMessage = '요청 처리에 실패했습니다.';

  it('Backend 응답에 message가 있으면 해당 메시지를 반환한다', () => {
    const error = {
      isAxiosError: true,
      response: {
        data: {
          code: 'AUTH_002',
          message: '접근 권한이 없습니다.',
        },
      },
    } as unknown as AxiosError<{ message?: unknown }>;

    expect(
      getApiErrorMessage(error, fallbackMessage),
    ).toBe('접근 권한이 없습니다.');
  });

  it('Backend 응답에 message가 없으면 기본 메시지를 반환한다', () => {
    const error = {
      isAxiosError: true,
      response: {
        data: {
          code: 'AUTH_002',
        },
      },
    } as unknown as AxiosError<{ message?: unknown }>;

    expect(
      getApiErrorMessage(error, fallbackMessage),
    ).toBe(fallbackMessage);
  });

  it('Backend message가 공백이면 기본 메시지를 반환한다', () => {
    const error = {
      isAxiosError: true,
      response: {
        data: {
          message: '   ',
        },
      },
    } as unknown as AxiosError<{ message?: unknown }>;

    expect(
      getApiErrorMessage(error, fallbackMessage),
    ).toBe(fallbackMessage);
  });

  it('Axios 오류가 아니면 기본 메시지를 반환한다', () => {
    const error = new Error('일반 오류');

    expect(
      getApiErrorMessage(error, fallbackMessage),
    ).toBe(fallbackMessage);
  });
});