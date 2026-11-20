
import {
  afterEach,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from "vitest";
import {
  BackendAuthService,
  BackendApiError,
} from "../../src/auth/services/backend-auth.service.js";

describe("BackendAuthService.logout", () => {
  let backendAuthService: BackendAuthService;
  let fetchMock: ReturnType<typeof vi.fn>;

  const accessToken = "test-access-token";

  beforeEach(() => {
    backendAuthService = new BackendAuthService();
    fetchMock = vi.fn();

    vi.stubGlobal("fetch", fetchMock);
  });

  afterEach(() => {
    vi.unstubAllGlobals();
  });

  it("POST 방식으로 Bearer 토큰을 전달하고 로그아웃한다", async () => {
    fetchMock.mockResolvedValue(
      new Response(
        JSON.stringify({
          success: true,
          code: "SUCCESS",
          message: "로그아웃되었습니다.",
          data: null,
        }),
        {
          status: 200,
          headers: {
            "Content-Type": "application/json",
          },
        },
      ),
    );

    await expect(
      backendAuthService.logout(accessToken),
    ).resolves.toBeUndefined();

    expect(fetchMock).toHaveBeenCalledOnce();

    const [url, options] = fetchMock.mock.calls[0];

    expect(url).toContain("/api/auth/logout");
    expect(options).toMatchObject({
      method: "POST",
      headers: {
        Authorization: `Bearer ${accessToken}`,
        Accept: "application/json",
      },
    });

    expect(options).not.toHaveProperty("body");
  });

  it("Backend 인증 실패 시 예외를 발생시킨다", async () => {
    fetchMock.mockResolvedValue(
      new Response(
        JSON.stringify({
          success: false,
          code: "AUTH_003",
          message: "인증 정보가 유효하지 않습니다.",
          data: null,
        }),
        {
          status: 401,
          headers: {
            "Content-Type": "application/json",
          },
        },
      ),
    );

    await expect(
      backendAuthService.logout(accessToken),
    ).rejects.toMatchObject({
      code: "AUTH_003",
      statusCode: 401,
    });
  });

  it("Backend 서버 오류 시 예외를 발생시킨다", async () => {
    fetchMock.mockResolvedValue(
      new Response(
        JSON.stringify({
          success: false,
          code: "INTERNAL_SERVER_ERROR",
          message: "서버 오류가 발생했습니다.",
          data: null,
        }),
        {
          status: 500,
          headers: {
            "Content-Type": "application/json",
          },
        },
      ),
    );

    await expect(
      backendAuthService.logout(accessToken),
    ).rejects.toMatchObject({
      code: "INTERNAL_SERVER_ERROR",
      statusCode: 500,
    });
  });

  it("Backend 연결 실패 시 BACKEND_UNAVAILABLE을 반환한다", async () => {
    fetchMock.mockRejectedValue(new Error("Connection refused"));

    await expect(
      backendAuthService.logout(accessToken),
    ).rejects.toMatchObject({
      code: "BACKEND_UNAVAILABLE",
      statusCode: 503,
    });
  });

  it("잘못된 JSON 응답이면 INVALID_BACKEND_RESPONSE를 반환한다", async () => {
    fetchMock.mockResolvedValue(
      new Response("Invalid JSON", {
        status: 200,
        headers: {
          "Content-Type": "application/json",
        },
      }),
    );

    await expect(
      backendAuthService.logout(accessToken),
    ).rejects.toMatchObject({
      code: "INVALID_BACKEND_RESPONSE",
      statusCode: 502,
    });
  });
});
