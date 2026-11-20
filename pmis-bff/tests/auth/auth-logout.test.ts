
import {
  afterEach,
  beforeEach,
  describe,
  expect,
  it,
  vi,
  type Mock,
} from "vitest";
import type { AuthService } from "../../src/auth/services/auth.service.js";
import { BackendApiError } from "../../src/auth/services/backend-auth.service.js";
import { MemorySessionStore } from "../../src/auth/session/memory-session-store.js";
import type { BffSession } from "../../src/auth/session/session.types.js";
import { createTestApp } from "../helpers/create-test-app.js";

describe("POST /api/auth/logout", () => {
  let app: Awaited<ReturnType<typeof createTestApp>>;
  let sessionStore: MemorySessionStore;
  let login: Mock<AuthService["login"]>;
  let completeSessionSelection: Mock<AuthService["completeSessionSelection"]>;
  let logout: Mock<(sessionId: string) => Promise<void>>;

  const session: BffSession = {
    sessionId: "123e4567-e89b-42d3-a456-426614174000",
    accessToken: "test-access-token",
    refreshToken: "test-refresh-token",
    user: {
      userId: 1,
      username: "user",
      name: "테스트 사용자",
      role: "USER_ROLE",
    },
    expiresAt: Date.now() + 5 * 60 * 1000,
    createdAt: Date.now(),
    updatedAt: Date.now(),
  };

  beforeEach(async () => {
    sessionStore = new MemorySessionStore();
    login = vi.fn<AuthService["login"]>();
    completeSessionSelection =
      vi.fn<AuthService["completeSessionSelection"]>();
    logout = vi.fn<(sessionId: string) => Promise<void>>();

    app = await createTestApp({
      sessionStore,
      authService: {
        login,
        completeSessionSelection,
        logout,
      },
    });
  });

  afterEach(async () => {
    await app.close();
  });

  it("세션 쿠키가 없으면 401을 반환한다", async () => {
    const response = await app.inject({
      method: "POST",
      url: "/api/auth/logout",
    });

    expect(response.statusCode).toBe(401);
    expect(response.json()).toMatchObject({
      success: false,
      code: "UNAUTHORIZED",
    });
    expect(logout).not.toHaveBeenCalled();
  });

  it("정상 로그아웃 시 쿠키를 만료시키고 BFF 세션을 삭제한다", async () => {
    await sessionStore.set(session);
    logout.mockImplementation(async (sessionId) => {
      await sessionStore.delete(sessionId);
    });

    const response = await app.inject({
      method: "POST",
      url: "/api/auth/logout",
      headers: {
        cookie: `pmis_session=${session.sessionId}`,
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json()).toMatchObject({
      success: true,
      code: "SUCCESS",
    });
    expect(response.headers["set-cookie"]).toContain("pmis_session=");
    expect(response.headers["set-cookie"]).toContain("Max-Age=0");
    expect(await sessionStore.get(session.sessionId)).toBeNull();
    expect(logout).toHaveBeenCalledWith(session.sessionId);
  });

  it("Backend 인증 실패 시 BFF 세션과 쿠키를 유지한다", async () => {
    await sessionStore.set(session);
    logout.mockRejectedValue(
      new BackendApiError(
        "인증 정보가 유효하지 않습니다.",
        "AUTH_003",
        401,
      ),
    );

    const response = await app.inject({
      method: "POST",
      url: "/api/auth/logout",
      headers: {
        cookie: `pmis_session=${session.sessionId}`,
      },
    });

    expect(response.statusCode).toBe(401);
    expect(response.json()).toMatchObject({
      success: false,
      code: "AUTH_003",
    });
    expect(response.headers["set-cookie"]).toBeUndefined();
    expect(await sessionStore.get(session.sessionId)).toEqual(session);
  });

  it("Backend 연결 장애 시 BFF 세션과 쿠키를 유지한다", async () => {
    await sessionStore.set(session);
    logout.mockRejectedValue(
      new BackendApiError(
        "Backend 서버에 연결할 수 없습니다.",
        "BACKEND_UNAVAILABLE",
        503,
      ),
    );

    const response = await app.inject({
      method: "POST",
      url: "/api/auth/logout",
      headers: {
        cookie: `pmis_session=${session.sessionId}`,
      },
    });

    expect(response.statusCode).toBe(503);
    expect(response.json()).toMatchObject({
      success: false,
      code: "BACKEND_UNAVAILABLE",
    });
    expect(response.headers["set-cookie"]).toBeUndefined();
    expect(await sessionStore.get(session.sessionId)).toEqual(session);
  });
});
