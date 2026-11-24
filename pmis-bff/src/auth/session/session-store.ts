import type { BffSession } from "./session.types.js";

export interface SessionStore {
  get(sessionId: string): Promise<BffSession | null>;
  set(session: BffSession): Promise<void>;
  delete(sessionId: string): Promise<void>;

  /**
   * 기존 세션이 존재하고 Access Token이 예상값과 일치할 때만
   * 새 세션으로 원자적으로 교체한다.
   *
   * 로그아웃으로 삭제된 세션이 자동 갱신 과정에서
   * 다시 생성되는 것을 방지한다.
   */
  compareAndSet(
    sessionId: string,
    expectedAccessToken: string,
    session: BffSession,
  ): Promise<boolean>;
}