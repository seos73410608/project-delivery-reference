
import {
  afterEach,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from "vitest";
import {
  BackendApiClient,
} from "../../../src/common/backend/backend-api.client.js";

describe("BackendApiClient", () => {
  let backendApiClient: BackendApiClient;
  let fetchMock: ReturnType<typeof vi.fn>;

  const accessToken = "test-access-token";
  const refreshedAccessToken = "refreshed-access-token";

  beforeEach(() => {
    backendApiClient = new BackendApiClient();
    fetchMock = vi.fn();

    vi.stubGlobal("fetch", fetchMock);
  });

  afterEach(() => {
    vi.unstubAllGlobals();
  });

  it("GET 요청에 Bearer 토큰과 Query Parameter를 전달한다", async () => {
    fetchMock.mockResolvedValue(
      new Response(
        JSON.stringify({
          success: true,
          code: "SUCCESS",
          data: [{ projectId: 1 }],
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
      backendApiClient.request({
        method: "GET",
        path: "/api/projects",
        accessToken,
        query: {
          page: 1,
          size: 20,
          keyword: "PMIS",
          active: true,
          sort: ["projectName,asc", "createdAt,desc"],
          ignored: null,
        },
      }),
    ).resolves.toEqual([{ projectId: 1 }]);

    expect(fetchMock).toHaveBeenCalledOnce();

    const [url, options] = fetchMock.mock.calls[0];
    const requestUrl = new URL(url);

    expect(requestUrl.pathname).toBe("/api/projects");
    expect(requestUrl.searchParams.get("page")).toBe("1");
    expect(requestUrl.searchParams.get("size")).toBe("20");
    expect(requestUrl.searchParams.get("keyword")).toBe("PMIS");
    expect(requestUrl.searchParams.get("active")).toBe("true");
    expect(requestUrl.searchParams.getAll("sort")).toEqual([
      "projectName,asc",
      "createdAt,desc",
    ]);
    expect(requestUrl.searchParams.has("ignored")).toBe(false);

    expect(options).toMatchObject({
      method: "GET",
      headers: {
        Authorization: `Bearer ${accessToken}`,
        Accept: "application/json",
      },
    });

    expect(options).not.toHaveProperty("body");
  });

  it("POST 요청에 JSON Body를 전달한다", async () => {
    const requestBody = {
      projectName: "PMIS 구축",
      description: "프로젝트 API 연동",
    };

    fetchMock.mockResolvedValue(
      new Response(
        JSON.stringify({
          success: true,
          code: "SUCCESS",
          data: { projectId: 1 },
        }),
        {
          status: 201,
          headers: {
            "Content-Type": "application/json",
          },
        },
      ),
    );

    await expect(
      backendApiClient.request({
        method: "POST",
        path: "/api/projects",
        accessToken,
        body: requestBody,
      }),
    ).resolves.toEqual({ projectId: 1 });

    const [, options] = fetchMock.mock.calls[0];

    expect(options).toMatchObject({
      method: "POST",
      headers: {
        Authorization: `Bearer ${accessToken}`,
        Accept: "application/json",
        "Content-Type": "application/json",
      },
      body: JSON.stringify(requestBody),
    });
  });

  it("HTTP 204 응답이면 null을 반환한다", async () => {
    fetchMock.mockResolvedValue(
      new Response(null, {
        status: 204,
      }),
    );

    await expect(
      backendApiClient.request({
        method: "DELETE",
        path: "/api/projects/1",
        accessToken,
      }),
    ).resolves.toBeNull();
  });

  it("Backend 오류 응답의 코드와 상태를 유지한다", async () => {
    fetchMock.mockResolvedValue(
      new Response(
        JSON.stringify({
          success: false,
          code: "PROJECT_001",
          message: "프로젝트를 찾을 수 없습니다.",
          data: null,
        }),
        {
          status: 404,
          headers: {
            "Content-Type": "application/json",
          },
        },
      ),
    );

    await expect(
      backendApiClient.request({
        method: "GET",
        path: "/api/projects/999",
        accessToken,
      }),
    ).rejects.toMatchObject({
      code: "PROJECT_001",
      statusCode: 404,
      message: "프로젝트를 찾을 수 없습니다.",
    });
  });

  it("Backend 연결 실패 시 BACKEND_UNAVAILABLE을 반환한다", async () => {
    fetchMock.mockRejectedValue(
      new Error("Connection refused"),
    );

    await expect(
      backendApiClient.request({
        method: "GET",
        path: "/api/projects",
        accessToken,
      }),
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
      backendApiClient.request({
        method: "GET",
        path: "/api/projects",
        accessToken,
      }),
    ).rejects.toMatchObject({
      code: "INVALID_BACKEND_RESPONSE",
      statusCode: 502,
    });
  });

  it("객체가 아닌 JSON 응답이면 INVALID_BACKEND_RESPONSE를 반환한다", async () => {
    fetchMock.mockResolvedValue(
      new Response(
        JSON.stringify("Unexpected response"),
        {
          status: 200,
          headers: {
            "Content-Type": "application/json",
          },
        },
      ),
    );

    await expect(
      backendApiClient.request({
        method: "GET",
        path: "/api/projects",
        accessToken,
      }),
    ).rejects.toMatchObject({
      code: "INVALID_BACKEND_RESPONSE",
      statusCode: 502,
    });
  });

  it("401 응답이면 Access Token 갱신 후 원래 요청을 재시도한다", async () => {
    const refreshAccessToken = vi.fn()
      .mockResolvedValue(refreshedAccessToken);

    fetchMock
      .mockResolvedValueOnce(
        new Response(
          JSON.stringify({
            success: false,
            code: "AUTH_001",
            message: "인증이 만료되었습니다.",
            data: null,
          }),
          {
            status: 401,
            headers: {
              "Content-Type": "application/json",
            },
          },
        ),
      )
      .mockResolvedValueOnce(
        new Response(
          JSON.stringify({
            success: true,
            code: "SUCCESS",
            data: [{ projectId: 1 }],
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
      backendApiClient.request({
        method: "GET",
        path: "/api/projects",
        accessToken,
        authContext: {
          refreshAccessToken,
        },
      }),
    ).resolves.toEqual([{ projectId: 1 }]);

    expect(fetchMock).toHaveBeenCalledTimes(2);
    expect(refreshAccessToken).toHaveBeenCalledOnce();
    expect(refreshAccessToken).toHaveBeenCalledWith(accessToken);

    const [, firstOptions] = fetchMock.mock.calls[0];
    const [, retryOptions] = fetchMock.mock.calls[1];

    expect(firstOptions.headers.Authorization)
      .toBe(`Bearer ${accessToken}`);

    expect(retryOptions.headers.Authorization)
      .toBe(`Bearer ${refreshedAccessToken}`);
  });

  it("Access Token 갱신 실패 시 갱신 오류를 전달한다", async () => {
    const refreshError = new Error("Token refresh failed");

    const refreshAccessToken = vi.fn()
      .mockRejectedValue(refreshError);

    fetchMock.mockResolvedValue(
      new Response(
        JSON.stringify({
          success: false,
          code: "AUTH_001",
          message: "인증이 만료되었습니다.",
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
      backendApiClient.request({
        method: "GET",
        path: "/api/projects",
        accessToken,
        authContext: {
          refreshAccessToken,
        },
      }),
    ).rejects.toBe(refreshError);

    expect(fetchMock).toHaveBeenCalledOnce();
    expect(refreshAccessToken).toHaveBeenCalledOnce();
  });

  it("재시도 요청에서도 401이면 추가 갱신 없이 종료한다", async () => {
    const refreshAccessToken = vi.fn()
      .mockResolvedValue(refreshedAccessToken);

    const unauthorizedResponse = () =>
      new Response(
        JSON.stringify({
          success: false,
          code: "AUTH_001",
          message: "인증이 만료되었습니다.",
          data: null,
        }),
        {
          status: 401,
          headers: {
            "Content-Type": "application/json",
          },
        },
      );

    fetchMock
      .mockResolvedValueOnce(unauthorizedResponse())
      .mockResolvedValueOnce(unauthorizedResponse());

    await expect(
      backendApiClient.request({
        method: "GET",
        path: "/api/projects",
        accessToken,
        authContext: {
          refreshAccessToken,
        },
      }),
    ).rejects.toMatchObject({
      code: "AUTH_001",
      statusCode: 401,
    });

    expect(fetchMock).toHaveBeenCalledTimes(2);
    expect(refreshAccessToken).toHaveBeenCalledOnce();
  });

  it("403 응답이면 Access Token 갱신을 시도하지 않는다", async () => {
    const refreshAccessToken = vi.fn()
      .mockResolvedValue(refreshedAccessToken);

    fetchMock.mockResolvedValue(
      new Response(
        JSON.stringify({
          success: false,
          code: "AUTH_002",
          message: "접근 권한이 없습니다.",
          data: null,
        }),
        {
          status: 403,
          headers: {
            "Content-Type": "application/json",
          },
        },
      ),
    );

    await expect(
      backendApiClient.request({
        method: "DELETE",
        path: "/api/projects/1",
        accessToken,
        authContext: {
          refreshAccessToken,
        },
      }),
    ).rejects.toMatchObject({
      code: "AUTH_002",
      statusCode: 403,
    });

    expect(fetchMock).toHaveBeenCalledOnce();
    expect(refreshAccessToken).not.toHaveBeenCalled();
  });
});
