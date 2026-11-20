import { afterEach, beforeEach, describe, expect, it, vi, type Mock } from "vitest";
import type { AuthService } from "../../src/auth/services/auth.service.js";
import { MemorySessionStore } from "../../src/auth/session/memory-session-store.js";
import type { BffSession } from "../../src/auth/session/session.types.js";
import { createTestApp } from "../helpers/create-test-app.js";

describe("GET /api/auth/me", () => {
  let app: Awaited<ReturnType<typeof createTestApp>>;
  let sessionStore: MemorySessionStore;
  let login: Mock<AuthService["login"]>;
  let completeSessionSelection: Mock<AuthService["completeSessionSelection"]>;

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

    app = await createTestApp({
      sessionStore,
      authService: {
        login,
        completeSessionSelection,
      },
    });
  });

  afterEach(async () => {
    await app.close();
  });

  it("세션 쿠키가 없으면 401을 반환한다", async () => {
    const response = await app.inject({
      method: "GET",
      url: "/api/auth/me",
    });

    expect(response.statusCode).toBe(401);
    expect(response.json()).toMatchObject({
      success: false,
      code: "UNAUTHORIZED",
    });
  });

  it("유효하지 않은 세션 ID이면 401을 반환한다", async () => {
    const response = await app.inject({
      method: "GET",
      url: "/api/auth/me",
      headers: {
        cookie: "pmis_session=123e4567-e89b-42d3-a456-426614174001",
      },
    });

    expect(response.statusCode).toBe(401);
    expect(response.json()).toMatchObject({
      success: false,
      code: "UNAUTHORIZED",
    });
  });

  it("유효한 세션이면 사용자 정보를 반환한다", async () => {
    await sessionStore.set(session);

    const response = await app.inject({
      method: "GET",
      url: "/api/auth/me",
      headers: {
        cookie: `pmis_session=${session.sessionId}`,
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json()).toMatchObject({
      success: true,
      data: session.user,
    });
  });

  it("만료된 세션이면 401을 반환한다", async () => {
    const expiredSession: BffSession = {
      ...session,
      expiresAt: Date.now() - 1000,
    };

    await sessionStore.set(expiredSession);

    const response = await app.inject({
      method: "GET",
      url: "/api/auth/me",
      headers: {
        cookie: `pmis_session=${expiredSession.sessionId}`,
      },
    });

    expect(response.statusCode).toBe(401);
    expect(response.json()).toMatchObject({
      success: false,
      code: "UNAUTHORIZED",
    });
  });

  it("세션 저장소 조회 중 오류가 발생하면 500을 반환한다", async () => {
    vi.spyOn(sessionStore, "get").mockRejectedValue(
      new Error("session store unavailable"),
    );

    const response = await app.inject({
      method: "GET",
      url: "/api/auth/me",
      headers: {
        cookie: `pmis_session=${session.sessionId}`,
      },
    });

    expect(response.statusCode).toBe(500);
    expect(response.json()).toMatchObject({
      success: false,
      code: "INTERNAL_SERVER_ERROR",
    });
  });
});
