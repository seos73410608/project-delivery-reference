
import {
  afterEach,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from "vitest";
import {
  AuthService,
  AuthServiceError,
} from "../../src/auth/services/auth.service.js";
import {
  backendAuthService,
  BackendApiError,
} from "../../src/auth/services/backend-auth.service.js";
import { MemorySessionStore } from "../../src/auth/session/memory-session-store.js";
import type { BffSession } from "../../src/auth/session/session.types.js";

describe("AuthService.logout", () => {
  let sessionStore: MemorySessionStore;
  let authService: AuthService;
  let backendLogoutSpy: ReturnType<
    typeof vi.spyOn<typeof backendAuthService, "logout">
  >;

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

  beforeEach(() => {
    sessionStore = new MemorySessionStore();
    authService = new AuthService(sessionStore);

    backendLogoutSpy = vi
      .spyOn(backendAuthService, "logout")
      .mockResolvedValue();
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it("유효하지 않은 세션이면 Backend 로그아웃을 호출하지 않는다", async () => {
    await expect(
      authService.logout(session.sessionId),
    ).rejects.toBeInstanceOf(AuthServiceError);

    await expect(
      authService.logout(session.sessionId),
    ).rejects.toMatchObject({
      code: "UNAUTHORIZED",
      statusCode: 401,
    });

    expect(backendLogoutSpy).not.toHaveBeenCalled();
  });

  it("Backend 로그아웃 성공 후 BFF 세션을 삭제한다", async () => {
    await sessionStore.set(session);

    backendLogoutSpy.mockImplementation(async () => {
      // Backend 로그아웃 처리 중에는 BFF 세션이 유지되어야 한다.
      expect(await sessionStore.get(session.sessionId)).toEqual(session);
    });

    await authService.logout(session.sessionId);

    expect(backendLogoutSpy).toHaveBeenCalledOnce();
    expect(backendLogoutSpy).toHaveBeenCalledWith(
      session.accessToken,
    );
    expect(await sessionStore.get(session.sessionId)).toBeNull();
  });

  it("Backend 로그아웃 실패 시 BFF 세션을 유지한다", async () => {
    await sessionStore.set(session);

    const backendError = new BackendApiError(
      "Backend 서버에 연결할 수 없습니다.",
      "BACKEND_UNAVAILABLE",
      503,
    );

    backendLogoutSpy.mockRejectedValue(backendError);

    await expect(
      authService.logout(session.sessionId),
    ).rejects.toBe(backendError);

    expect(backendLogoutSpy).toHaveBeenCalledWith(
      session.accessToken,
    );
    expect(await sessionStore.get(session.sessionId)).toEqual(session);
  });
});
