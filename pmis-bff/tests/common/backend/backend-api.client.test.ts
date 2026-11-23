
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
});
