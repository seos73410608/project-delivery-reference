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

describe("OperationScope routes", () => {
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

  const operationScopeResponse = {
    id: 100,
    status: "ACTIVE",
    operationId: 10,
    operationCode: "OPS-001",
    operationName: "PMIS 운영",
    operationType: "APPLICATION_SERVICE",
    operationStatus: "ACTIVE",
    operationalScopeId: 20,
    scopeCode: "SCOPE-001",
    scopeName: "운영 시스템",
    scopeType: "SYSTEM",
    operationalScopeStatus: "ACTIVE",
    effectiveFrom: "2026-12-01",
    effectiveTo: null,
    description: "운영 범위 테스트",
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

  it("운영 범위를 생성하고 Backend Bearer 토큰을 전달한다", async () => {
    mockBackendResponse(operationScopeResponse, 201);

    const payload = {
      operationId: 10,
      operationalScopeId: 20,
      effectiveFrom: "2026-12-01",
      effectiveTo: null,
      description: "운영 범위 생성",
    };

    const response = await app.inject({
      method: "POST",
      url: "/api/operation-scopes",
      headers: {
        cookie: cookieHeader,
      },
      payload,
    });

    expect(response.statusCode).toBe(201);
    expect(response.json()).toMatchObject({
      success: true,
      code: "SUCCESS",
      data: operationScopeResponse,
    });

    expect(fetchMock).toHaveBeenCalledOnce();

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe("/api/operation-scopes");
    expect(options).toMatchObject({
      method: "POST",
      headers: {
        Authorization: "Bearer test-access-token",
      },
    });
    expect(JSON.parse(options.body)).toEqual(payload);
  });

  it("운영 범위 단건 정보를 조회한다", async () => {
    mockBackendResponse(operationScopeResponse);

    const response = await app.inject({
      method: "GET",
      url: "/api/operation-scopes/100",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json()).toMatchObject({
      success: true,
      code: "SUCCESS",
      data: operationScopeResponse,
    });

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe("/api/operation-scopes/100");
    expect(options.method).toBe("GET");
    expect(options.headers.Authorization).toBe(
      "Bearer test-access-token",
    );
  });

  it("운영 관계 기준으로 운영 범위 목록을 조회한다", async () => {
    mockBackendResponse([operationScopeResponse]);

    const response = await app.inject({
      method: "GET",
      url: "/api/operation-scopes/by-operation/10",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json()).toMatchObject({
      success: true,
      code: "SUCCESS",
      data: [operationScopeResponse],
    });

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe(
      "/api/operation-scopes/by-operation/10",
    );
    expect(options.method).toBe("GET");
    expect(options.headers.Authorization).toBe(
      "Bearer test-access-token",
    );
  });

  it("운영 대상 범위 기준으로 운영 범위 목록을 조회한다", async () => {
    mockBackendResponse([operationScopeResponse]);

    const response = await app.inject({
      method: "GET",
      url: "/api/operation-scopes/by-operational-scope/20",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json()).toMatchObject({
      success: true,
      code: "SUCCESS",
      data: [operationScopeResponse],
    });

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe(
      "/api/operation-scopes/by-operational-scope/20",
    );
    expect(options.method).toBe("GET");
    expect(options.headers.Authorization).toBe(
      "Bearer test-access-token",
    );
  });

  it("운영 범위 정보를 수정한다", async () => {
    const updatedResponse = {
      ...operationScopeResponse,
      effectiveFrom: "2027-01-01",
      effectiveTo: "2027-12-31",
      description: "운영 범위 수정",
    };

    mockBackendResponse(updatedResponse);

    const payload = {
      effectiveFrom: "2027-01-01",
      effectiveTo: "2027-12-31",
      description: "운영 범위 수정",
    };

    const response = await app.inject({
      method: "PUT",
      url: "/api/operation-scopes/100",
      headers: {
        cookie: cookieHeader,
      },
      payload,
    });

    expect(response.statusCode).toBe(200);
    expect(response.json()).toMatchObject({
      success: true,
      code: "SUCCESS",
      data: updatedResponse,
    });

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe("/api/operation-scopes/100");
    expect(options).toMatchObject({
      method: "PUT",
      headers: {
        Authorization: "Bearer test-access-token",
      },
    });
    expect(JSON.parse(options.body)).toEqual(payload);
  });

  it("운영 범위 상태를 변경한다", async () => {
    const changedResponse = {
      ...operationScopeResponse,
      status: "COMPLETED",
    };

    mockBackendResponse(changedResponse);

    const payload = {
      status: "COMPLETED",
    };

    const response = await app.inject({
      method: "PATCH",
      url: "/api/operation-scopes/100/status",
      headers: {
        cookie: cookieHeader,
      },
      payload,
    });

    expect(response.statusCode).toBe(200);
    expect(response.json()).toMatchObject({
      success: true,
      code: "SUCCESS",
      data: changedResponse,
    });

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe(
      "/api/operation-scopes/100/status",
    );
    expect(options).toMatchObject({
      method: "PATCH",
      headers: {
        Authorization: "Bearer test-access-token",
      },
    });
    expect(JSON.parse(options.body)).toEqual(payload);
  });

  it("세션 쿠키가 없으면 Backend를 호출하지 않고 401을 반환한다", async () => {
    const response = await app.inject({
      method: "GET",
      url: "/api/operation-scopes/100",
    });

    expect(response.statusCode).toBe(401);
    expect(response.json()).toMatchObject({
      success: false,
      code: "UNAUTHORIZED",
    });
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("잘못된 운영 범위 ID는 Backend 호출 전에 거부한다", async () => {
    const response = await app.inject({
      method: "GET",
      url: "/api/operation-scopes/invalid",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(400);
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("잘못된 운영 관계 ID는 Backend 호출 전에 거부한다", async () => {
    const response = await app.inject({
      method: "GET",
      url: "/api/operation-scopes/by-operation/invalid",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(400);
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("잘못된 운영 대상 범위 ID는 Backend 호출 전에 거부한다", async () => {
    const response = await app.inject({
      method: "GET",
      url: "/api/operation-scopes/by-operational-scope/invalid",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(400);
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("운영 범위 생성 시 필수 필드가 누락되면 400을 반환한다", async () => {
    const response = await app.inject({
      method: "POST",
      url: "/api/operation-scopes",
      headers: {
        cookie: cookieHeader,
      },
      payload: {
        operationId: 10,
      },
    });

    expect(response.statusCode).toBe(400);
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("운영 범위 생성 시 잘못된 날짜 형식은 Backend 호출 전에 거부한다", async () => {
    const response = await app.inject({
      method: "POST",
      url: "/api/operation-scopes",
      headers: {
        cookie: cookieHeader,
      },
      payload: {
        operationId: 10,
        operationalScopeId: 20,
        effectiveFrom: "2026-13-45",
      },
    });

    expect(response.statusCode).toBe(400);
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("잘못된 운영 범위 상태 값은 Backend 호출 전에 거부한다", async () => {
    const response = await app.inject({
      method: "PATCH",
      url: "/api/operation-scopes/100/status",
      headers: {
        cookie: cookieHeader,
      },
      payload: {
        status: "INVALID",
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
          code: "OPERATION_SCOPE_001",
          message: "운영 범위 정보를 찾을 수 없습니다.",
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
      url: "/api/operation-scopes/999",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(404);
    expect(response.json()).toMatchObject({
      success: false,
      code: "OPERATION_SCOPE_001",
      message: "운영 범위 정보를 찾을 수 없습니다.",
    });
  });
});