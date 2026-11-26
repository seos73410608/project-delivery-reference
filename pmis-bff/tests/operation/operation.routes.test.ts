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

describe("Operation routes", () => {
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

  const operationResponse = {
    id: 10,
    operationCode: "OPS-001",
    operationName: "통합 운영 서비스",
    operationType: "APPLICATION_SERVICE",
    status: "ACTIVE",
    managerUserId: 1,
    managerName: "운영 관리자",
    description: "통합 운영 서비스 설명",
    effectiveFrom: "2026-01-01",
    effectiveTo: null,
    createdAt: "2026-01-01T09:00:00",
    updatedAt: "2026-01-02T09:00:00",
  };

  function mockBackendResponse(
    data: unknown,
    status = 200,
  ): void {
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

  it("Operation 목록을 조회하고 Backend Bearer 토큰을 전달한다", async () => {
    mockBackendResponse([operationResponse]);

    const response = await app.inject({
      method: "GET",
      url: "/api/operations",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json()).toMatchObject({
      success: true,
      code: "SUCCESS",
      data: [operationResponse],
    });

    expect(fetchMock).toHaveBeenCalledOnce();

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe("/api/operations");
    expect(options).toMatchObject({
      method: "GET",
      headers: {
        Authorization: "Bearer test-access-token",
      },
    });
  });

  it("Operation 상세 정보를 조회한다", async () => {
    mockBackendResponse(operationResponse);

    const response = await app.inject({
      method: "GET",
      url: "/api/operations/10",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json().data).toEqual(operationResponse);

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe("/api/operations/10");
    expect(options.method).toBe("GET");
  });

  it("Operation 코드로 상세 정보를 조회한다", async () => {
    mockBackendResponse(operationResponse);

    const response = await app.inject({
      method: "GET",
      url: "/api/operations/by-code/OPS-001",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json().data).toEqual(operationResponse);

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe(
      "/api/operations/by-code/OPS-001",
    );
    expect(options.method).toBe("GET");
  });

  it("Operation을 등록하고 201을 반환한다", async () => {
    mockBackendResponse(operationResponse);

    const requestBody = {
      operationCode: "OPS-001",
      operationName: "통합 운영 서비스",
      operationType: "APPLICATION_SERVICE",
      managerUserId: 1,
      description: "통합 운영 서비스 설명",
      effectiveFrom: "2026-01-01",
    };

    const response = await app.inject({
      method: "POST",
      url: "/api/operations",
      headers: {
        cookie: cookieHeader,
      },
      payload: requestBody,
    });

    expect(response.statusCode).toBe(201);
    expect(response.json().data).toEqual(operationResponse);

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe("/api/operations");
    expect(options.method).toBe("POST");
    expect(JSON.parse(options.body)).toEqual(requestBody);
  });

  it("Operation 정보를 수정한다", async () => {
    mockBackendResponse(operationResponse);

    const requestBody = {
      operationName: "변경된 운영 서비스",
      operationType: "APPLICATION_SERVICE",
      managerUserId: 1,
      description: "변경된 설명",
      effectiveFrom: "2026-01-01",
    };

    const response = await app.inject({
      method: "PUT",
      url: "/api/operations/10",
      headers: {
        cookie: cookieHeader,
      },
      payload: requestBody,
    });

    expect(response.statusCode).toBe(200);
    expect(response.json().data).toEqual(operationResponse);

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe("/api/operations/10");
    expect(options.method).toBe("PUT");
    expect(JSON.parse(options.body)).toEqual(requestBody);
  });

  it("Operation 상태를 변경한다", async () => {
    const updatedOperation = {
      ...operationResponse,
      status: "SUSPENDED",
    };

    mockBackendResponse(updatedOperation);

    const requestBody = {
      status: "SUSPENDED",
    };

    const response = await app.inject({
      method: "PATCH",
      url: "/api/operations/10/status",
      headers: {
        cookie: cookieHeader,
      },
      payload: requestBody,
    });

    expect(response.statusCode).toBe(200);
    expect(response.json().data).toEqual(updatedOperation);

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe(
      "/api/operations/10/status",
    );
    expect(options.method).toBe("PATCH");
    expect(JSON.parse(options.body)).toEqual(requestBody);
  });

  it("세션 쿠키가 없으면 Backend를 호출하지 않고 401을 반환한다", async () => {
    const response = await app.inject({
      method: "GET",
      url: "/api/operations",
    });

    expect(response.statusCode).toBe(401);
    expect(response.json()).toMatchObject({
      success: false,
      code: "UNAUTHORIZED",
    });
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("잘못된 Operation ID는 Backend 호출 전에 거부한다", async () => {
    const response = await app.inject({
      method: "GET",
      url: "/api/operations/invalid",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(400);
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("필수 등록 항목이 누락되면 Backend 호출 전에 거부한다", async () => {
    const response = await app.inject({
      method: "POST",
      url: "/api/operations",
      headers: {
        cookie: cookieHeader,
      },
      payload: {
        operationCode: "OPS-001",
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
          code: "OPERATION_NOT_FOUND",
          message: "운영 정보를 찾을 수 없습니다.",
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
      url: "/api/operations/999",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(404);
    expect(response.json()).toMatchObject({
      success: false,
      code: "OPERATION_NOT_FOUND",
      message: "운영 정보를 찾을 수 없습니다.",
    });
  });
});