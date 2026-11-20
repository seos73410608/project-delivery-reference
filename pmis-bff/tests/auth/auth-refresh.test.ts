import { afterEach, beforeEach, describe, expect, it, vi, type Mock } from "vitest";
import type { AuthService } from "../../src/auth/services/auth.service.js";
import { AuthServiceError } from "../../src/auth/services/auth.service.js";
import { BackendApiError } from "../../src/auth/services/backend-auth.service.js";
import { MemorySessionStore } from "../../src/auth/session/memory-session-store.js";
import type { BffSession } from "../../src/auth/session/session.types.js";
import { createTestApp } from "../helpers/create-test-app.js";

describe("POST /api/auth/refresh", () => {
  let app: Awaited<ReturnType<typeof createTestApp>>;
  let sessionStore: MemorySessionStore;
  let login: Mock<AuthService["login"]>;
  let completeSessionSelection: Mock<AuthService["completeSessionSelection"]>;
  let refresh: Mock<(sessionId: string) => Promise<BffSession>>;

  const session: BffSession = {
    sessionId: "123e4567-e89b-42d3-a456-426614174000",
    accessToken: "old-access-token",
    refreshToken: "old-refresh-token",
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
    refresh = vi.fn<(sessionId: string) => Promise<BffSession>>();

    app = await createTestApp({
      sessionStore,
      authService: {
        login,
        completeSessionSelection,
        refresh,
      },
    });
  });

  afterEach(async () => {
    await app.close();
  });

  it("세션 쿠키가 없으면 401을 반환한다", async () => {
    const response = await app.inject({
      method: "POST",
      url: "/api/auth/refresh",
    });

    expect(response.statusCode).toBe(401);
    expect(response.json()).toMatchObject({
      success: false,
      code: "UNAUTHORIZED",
    });
    expect(refresh).not.toHaveBeenCalled();
  });

  it("유효하지 않은 BFF 세션이면 401을 반환한다", async () => {
    refresh.mockRejectedValue(
      new AuthServiceError(
        "세션이 유효하지 않거나 만료되었습니다.",
        "UNAUTHORIZED",
        401,
      ),
    );

    const response = await app.inject({
      method: "POST",
      url: "/api/auth/refresh",
      headers: {
        cookie: `pmis_session=${session.sessionId}`,
      },
    });

    expect(response.statusCode).toBe(401);
    expect(response.json()).toMatchObject({
      success: false,
      code: "UNAUTHORIZED",
    });
    expect(refresh).toHaveBeenCalledWith(session.sessionId);
  });

  it("정상 갱신 시 쿠키를 재발급하고 토큰을 응답에 노출하지 않는다", async () => {
    const refreshedSession: BffSession = {
      ...session,
      accessToken: "new-access-token",
      refreshToken: "new-refresh-token",
      expiresAt: Date.now() + 10 * 60 * 1000,
      updatedAt: Date.now() + 1000,
    };

    await sessionStore.set(refreshedSession);
    refresh.mockResolvedValue(refreshedSession);

    const response = await app.inject({
      method: "POST",
      url: "/api/auth/refresh",
      headers: {
        cookie: `pmis_session=${session.sessionId}`,
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json()).toMatchObject({
      success: true,
      code: "SUCCESS",
    });
    expect(response.json()).not.toHaveProperty("data");
    expect(response.body).not.toContain("new-access-token");
    expect(response.body).not.toContain("new-refresh-token");

    const setCookie = response.headers["set-cookie"];
    expect(setCookie).toContain("pmis_session=");
    expect(setCookie).toContain("HttpOnly");
    expect(setCookie).toContain("SameSite=Lax");
    expect(setCookie).toContain("Path=/");

    expect(refresh).toHaveBeenCalledWith(session.sessionId);
  });

  it("Backend에서 Refresh Token이 만료되었다고 응답하면 세션을 정리한다", async () => {
    await sessionStore.set(session);
    refresh.mockRejectedValue(
      new BackendApiError(
        "JWT 토큰이 만료되었습니다.",
        "AUTH_004",
        401,
      ),
    );

    const response = await app.inject({
      method: "POST",
      url: "/api/auth/refresh",
      headers: {
        cookie: `pmis_session=${session.sessionId}`,
      },
    });

    expect(response.statusCode).toBe(401);
    expect(response.headers["set-cookie"]).toContain("pmis_session=");
    expect(response.headers["set-cookie"]).toContain("Max-Age=0");
    expect(await sessionStore.get(session.sessionId)).toBeNull();
  });

  it("Backend 장애가 발생하면 기존 세션을 유지한다", async () => {
    await sessionStore.set(session);
    refresh.mockRejectedValue(
      new BackendApiError(
        "Backend 서버에 연결할 수 없습니다.",
        "BACKEND_UNAVAILABLE",
        503,
      ),
    );

    const response = await app.inject({
      method: "POST",
      url: "/api/auth/refresh",
      headers: {
        cookie: `pmis_session=${session.sessionId}`,
      },
    });

    expect(response.statusCode).toBe(503);
    expect(response.headers["set-cookie"]).toBeUndefined();
    expect(await sessionStore.get(session.sessionId)).toEqual(session);
  });
});
