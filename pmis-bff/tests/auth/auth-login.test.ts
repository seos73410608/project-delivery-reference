import { afterEach, beforeEach, describe, expect, it, vi, type Mock } from "vitest";
import type { AuthService } from "../../src/auth/services/auth.service.js";
import { AuthServiceError } from "../../src/auth/services/auth.service.js";
import { MemorySessionStore } from "../../src/auth/session/memory-session-store.js";
import type { BffSession } from "../../src/auth/session/session.types.js";
import { createTestApp } from "../helpers/create-test-app.js";

describe("POST /api/auth/login", () => {
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

  it("정상 로그인 시 사용자 정보와 세션 쿠키를 반환한다", async () => {
    await sessionStore.set(session);

    login.mockResolvedValue({
      status: "SUCCESS",
      sessionId: session.sessionId,
      user: session.user,
    });

    const response = await app.inject({
      method: "POST",
      url: "/api/auth/login",
      payload: {
        username: "user",
        password: "user1234",
      },
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
    expect(setCookie).not.toContain("Secure");

    expect(login).toHaveBeenCalledWith({
      username: "user",
      password: "user1234",
    });
  });

  it("필수 입력값이 없으면 400을 반환한다", async () => {
    const response = await app.inject({
      method: "POST",
      url: "/api/auth/login",
      payload: {
        username: "user",
      },
    });

    expect(response.statusCode).toBe(400);
    expect(login).not.toHaveBeenCalled();
  });

  it("허용되지 않은 추가 필드가 있으면 400을 반환한다", async () => {
    const response = await app.inject({
      method: "POST",
      url: "/api/auth/login",
      payload: {
        username: "user",
        password: "user1234",
        extra: "not-allowed",
      },
    });
    expect(response.statusCode).toBe(400);
    expect(login).not.toHaveBeenCalled();
  });

  it("인증 서비스 오류를 상태 코드와 함께 전달한다", async () => {
    login.mockRejectedValue(
      new AuthServiceError(
        "아이디 또는 비밀번호가 올바르지 않습니다.",
        "INVALID_CREDENTIALS",
        401,
      ),
    );

    const response = await app.inject({
      method: "POST",
      url: "/api/auth/login",
      payload: {
        username: "user",
        password: "wrong-password",
      },
    });

    expect(response.statusCode).toBe(401);
    expect(response.json()).toMatchObject({
      success: false,
      code: "INVALID_CREDENTIALS",
    });
  });

  it("로그인 성공 결과에 해당하는 세션이 없으면 500을 반환한다", async () => {
    login.mockResolvedValue({
      status: "SUCCESS",
      sessionId: session.sessionId,
      user: session.user,
    });

    const response = await app.inject({
      method: "POST",
      url: "/api/auth/login",
      payload: {
        username: "user",
        password: "user1234",
      },
    });

    expect(response.statusCode).toBe(500);
    expect(response.json()).toMatchObject({
      success: false,
      code: "SESSION_CREATION_FAILED",
    });
    expect(response.headers["set-cookie"]).toBeUndefined();
  });
});





