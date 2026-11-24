
import {
  afterEach,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from "vitest";
import { MemorySessionStore } from "../../src/auth/session/memory-session-store.js";
import type { BffSession } from "../../src/auth/session/session.types.js";
import { createTestApp } from "../helpers/create-test-app.js";

describe("OperationalScopeCI routes", () => {
  let app: Awaited<ReturnType<typeof createTestApp>>;
  let sessionStore: MemorySessionStore;
  let fetchMock: ReturnType<typeof vi.fn>;

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

  const cookieHeader = `pmis_session=${session.sessionId}`;

  const operationalScopeCiResponse = {
    id: 300,
    scopeId: 100,
    ciId: 200,
    ciCode: "CI-001",
    ciName: "운영 WAS 서버",
    ciTypeId: 10,
    ciTypeCode: "SERVER",
    ciTypeName: "서버",
    status: "ACTIVE",
    environment: "PROD",
    createdAt: "2026-11-24T09:00:00",
    updatedAt: "2026-11-24T09:00:00",
  };

  function mockBackendResponse(
    data: unknown,
    status = 200,
  ) {
    fetchMock.mockResolvedValue(
      new Response(
        JSON.stringify({
          success: true,
          code: "SUCCESS",
          data,
        }),
        {
          status,
          headers: {
            "Content-Type": "application/json",
          },
        },
      ),
    );
  }

  beforeEach(async () => {
    sessionStore = new MemorySessionStore();
    await sessionStore.set(session);

    fetchMock = vi.fn();
    vi.stubGlobal("fetch", fetchMock);

    app = await createTestApp({
      sessionStore,
      authService: {
        login: vi.fn(),
        completeSessionSelection: vi.fn(),
      },
    });
  });

  afterEach(async () => {
    await app.close();
    vi.unstubAllGlobals();
  });

  it("Operational Scope에 CI를 연결하고 Backend Bearer 토큰을 전달한다", async () => {
    mockBackendResponse(operationalScopeCiResponse, 201);

    const response = await app.inject({
      method: "POST",
      url: "/api/operational-scopes/100/cis/200",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(201);
    expect(response.json()).toMatchObject({
      success: true,
      code: "SUCCESS",
      data: operationalScopeCiResponse,
    });

    expect(fetchMock).toHaveBeenCalledOnce();

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe(
      "/api/operational-scopes/100/cis/200",
    );
    expect(options).toMatchObject({
      method: "POST",
      headers: {
        Authorization: "Bearer test-access-token",
      },
    });
    expect(options.body).toBeUndefined();
  });

  it("Operational Scope에 연결된 CI 목록을 조회한다", async () => {
    mockBackendResponse([operationalScopeCiResponse]);

    const response = await app.inject({
      method: "GET",
      url: "/api/operational-scopes/100/cis",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json()).toMatchObject({
      success: true,
      code: "SUCCESS",
      data: [operationalScopeCiResponse],
    });

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe(
      "/api/operational-scopes/100/cis",
    );
    expect(options.method).toBe("GET");
    expect(options.headers.Authorization).toBe(
      "Bearer test-access-token",
    );
  });

  it("Operational Scope와 CI의 연결 상세 정보를 조회한다", async () => {
    mockBackendResponse(operationalScopeCiResponse);

    const response = await app.inject({
      method: "GET",
      url: "/api/operational-scopes/100/cis/200",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json()).toMatchObject({
      success: true,
      code: "SUCCESS",
      data: operationalScopeCiResponse,
    });

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe(
      "/api/operational-scopes/100/cis/200",
    );
    expect(options.method).toBe("GET");
    expect(options.headers.Authorization).toBe(
      "Bearer test-access-token",
    );
  });

  it("Operational Scope와 CI의 연결을 해제한다", async () => {
    mockBackendResponse(null);

    const response = await app.inject({
      method: "DELETE",
      url: "/api/operational-scopes/100/cis/200",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json()).toMatchObject({
      success: true,
      code: "SUCCESS",
      message: null,
    });

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe(
      "/api/operational-scopes/100/cis/200",
    );
    expect(options).toMatchObject({
      method: "DELETE",
      headers: {
        Authorization: "Bearer test-access-token",
      },
    });
    expect(options.body).toBeUndefined();
  });

  it("세션 쿠키가 없으면 Backend를 호출하지 않고 401을 반환한다", async () => {
    const response = await app.inject({
      method: "GET",
      url: "/api/operational-scopes/100/cis",
    });

    expect(response.statusCode).toBe(401);
    expect(response.json()).toMatchObject({
      success: false,
      code: "UNAUTHORIZED",
    });
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("잘못된 scopeId는 Backend 호출 전에 거부한다", async () => {
    const response = await app.inject({
      method: "GET",
      url: "/api/operational-scopes/invalid/cis",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(400);
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("잘못된 ciId는 Backend 호출 전에 거부한다", async () => {
    const response = await app.inject({
      method: "GET",
      url: "/api/operational-scopes/100/cis/invalid",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(400);
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("0인 scopeId는 Backend 호출 전에 거부한다", async () => {
    const response = await app.inject({
      method: "GET",
      url: "/api/operational-scopes/0/cis",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(400);
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("Backend 오류의 상태 코드와 오류 코드를 유지한다", async () => {
    fetchMock.mockResolvedValue(
      new Response(
        JSON.stringify({
          success: false,
          code: "OPERATIONAL_SCOPE_CI_001",
          message: "Operational Scope와 CI 연결 정보를 찾을 수 없습니다.",
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

    const response = await app.inject({
      method: "GET",
      url: "/api/operational-scopes/100/cis/999",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(404);
    expect(response.json()).toMatchObject({
      success: false,
      code: "OPERATIONAL_SCOPE_CI_001",
      message: "Operational Scope와 CI 연결 정보를 찾을 수 없습니다.",
    });
  });
});
