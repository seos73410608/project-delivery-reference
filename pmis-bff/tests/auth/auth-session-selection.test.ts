import { afterEach, beforeEach, describe, expect, it, vi, type Mock } from "vitest";
import type { AuthService } from "../../src/auth/services/auth.service.js";
import { AuthServiceError } from "../../src/auth/services/auth.service.js";
import { MemorySessionStore } from "../../src/auth/session/memory-session-store.js";
import type { BffSession } from "../../src/auth/session/session.types.js";
import { createTestApp } from "../helpers/create-test-app.js";

describe("POST /api/auth/login/session-selection", () => {
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

  const requestBody = {
    loginAttemptId: "test-login-attempt",
    sessionIds: ["123e4567-e89b-42d3-a456-426614174001"],
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

  it("정상 세션 선택 시 사용자 정보와 세션 쿠키를 반환한다", async () => {
    await sessionStore.set(session);

    completeSessionSelection.mockResolvedValue({
      status: "SUCCESS",
      sessionId: session.sessionId,
      user: session.user,
    });

    const response = await app.inject({
      method: "POST",
      url: "/api/auth/login/session-selection",
      payload: requestBody,
    });

    expect(response.statusCode).toBe(200);
    expect(response.json()).toMatchObject({
      success: true,
      code: "SUCCESS",
      data: {
        user: session.user,
      },
    });

    const setCookie = response.headers["set-cookie"];

    expect(setCookie).toBeDefined();
    expect(setCookie).toContain("pmis_session=");
    expect(setCookie).toContain("HttpOnly");
    expect(setCookie).toContain("SameSite=Lax");
    expect(setCookie).toContain("Path=/");

    expect(completeSessionSelection).toHaveBeenCalledWith(requestBody);
  });

  it("추가 세션 선택이 필요하면 선택 정보를 반환하고 쿠키를 발급하지 않는다", async () => {
    completeSessionSelection.mockResolvedValue({
      status: "SESSION_SELECTION_REQUIRED",
      loginAttemptId: requestBody.loginAttemptId,
      requiredTerminationCount: 1,
      activeSessions: [],
    });

    const response = await app.inject({
      method: "POST",
      url: "/api/auth/login/session-selection",
      payload: requestBody,
    });

    expect(response.statusCode).toBe(200);
    expect(response.json()).toMatchObject({
      success: true,
      code: "SESSION_SELECTION_REQUIRED",
      data: {
        status: "SESSION_SELECTION_REQUIRED",
        loginAttemptId: requestBody.loginAttemptId,
        requiredTerminationCount: 1,
        activeSessions: [],
      },
    });

    expect(response.headers["set-cookie"]).toBeUndefined();
  });

  it("잘못된 요청값이면 400을 반환한다", async () => {
    const response = await app.inject({
      method: "POST",
      url: "/api/auth/login/session-selection",
      payload: {
        loginAttemptId: requestBody.loginAttemptId,
        sessionIds: ["invalid-uuid"],
      },
    });

    expect(response.statusCode).toBe(400);
    expect(completeSessionSelection).not.toHaveBeenCalled();
  });

  it("인증 서비스 오류를 해당 HTTP 상태로 반환한다", async () => {
    completeSessionSelection.mockRejectedValue(
      new AuthServiceError(
        "세션 선택 요청이 유효하지 않습니다.",
        "INVALID_SESSION_SELECTION",
        401,
      ),
    );

    const response = await app.inject({
      method: "POST",
      url: "/api/auth/login/session-selection",
      payload: requestBody,
    });

    expect(response.statusCode).toBe(401);
    expect(response.json()).toMatchObject({
      success: false,
      code: "INVALID_SESSION_SELECTION",
    });
  });

  it("로그인 성공 결과에 해당하는 세션이 없으면 500을 반환한다", async () => {
    completeSessionSelection.mockResolvedValue({
      status: "SUCCESS",
      sessionId: session.sessionId,
      user: session.user,
    });

    const response = await app.inject({
      method: "POST",
      url: "/api/auth/login/session-selection",
      payload: requestBody,
    });

    expect(response.statusCode).toBe(500);
    expect(response.json()).toMatchObject({
      success: false,
      code: "SESSION_CREATION_FAILED",
    });
  });
});
