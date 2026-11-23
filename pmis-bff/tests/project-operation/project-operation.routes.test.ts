
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

describe("ProjectOperation routes", () => {
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

  const projectOperationResponse = {
    id: 1,
    relationshipType: "BUILD_RESULT",
    status: "ACTIVE",
    projectId: 10,
    projectCode: "PRJ-001",
    projectName: "PMIS 구축",
    customerName: "테스트 고객사",
    projectManager: "홍길동",
    operationId: 20,
    operationCode: "OPS-001",
    operationName: "PMIS 운영",
    operationType: "APPLICATION_SERVICE",
    operationStatus: "ACTIVE",
    effectiveFrom: "2026-01-01",
    effectiveTo: null,
    description: "프로젝트와 운영의 관계",
    createdAt: "2026-01-01T09:00:00",
    updatedAt: "2026-01-01T09:00:00",
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

  it("프로젝트 운영 관계를 생성하고 Backend Bearer 토큰을 전달한다", async () => {
    mockBackendResponse(projectOperationResponse, 201);

    const response = await app.inject({
      method: "POST",
      url: "/api/project-operations/",
      headers: {
        cookie: cookieHeader,
      },
      payload: {
        projectId: 10,
        operationId: 20,
        relationshipType: "BUILD_RESULT",
        effectiveFrom: "2026-01-01",
        effectiveTo: null,
        description: "프로젝트와 운영의 관계",
      },
    });

    expect(response.statusCode).toBe(201);
    expect(response.json()).toMatchObject({
      success: true,
      code: "SUCCESS",
      data: projectOperationResponse,
    });

    expect(fetchMock).toHaveBeenCalledOnce();

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe("/api/project-operations");
    expect(options).toMatchObject({
      method: "POST",
      headers: {
        Authorization: "Bearer test-access-token",
      },
    });

    expect(JSON.parse(options.body)).toMatchObject({
      projectId: 10,
      operationId: 20,
      relationshipType: "BUILD_RESULT",
      effectiveTo: null,
    });
  });

  it("프로젝트 운영 관계의 단건 정보를 조회한다", async () => {
    mockBackendResponse(projectOperationResponse);

    const response = await app.inject({
      method: "GET",
      url: "/api/project-operations/1",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json()).toMatchObject({
      success: true,
      data: projectOperationResponse,
    });

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe("/api/project-operations/1");
    expect(options.method).toBe("GET");
    expect(options.headers.Authorization).toBe(
      "Bearer test-access-token",
    );
  });

  it("프로젝트 운영 관계를 수정하고 nullable 필드를 전달한다", async () => {
    mockBackendResponse({
      ...projectOperationResponse,
      relationshipType: "ENHANCEMENT",
      effectiveFrom: null,
      effectiveTo: null,
      description: null,
    });

    const response = await app.inject({
      method: "PUT",
      url: "/api/project-operations/1",
      headers: {
        cookie: cookieHeader,
      },
      payload: {
        relationshipType: "ENHANCEMENT",
        effectiveFrom: null,
        effectiveTo: null,
        description: null,
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json().data.relationshipType).toBe("ENHANCEMENT");

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe("/api/project-operations/1");
    expect(options.method).toBe("PUT");
    expect(JSON.parse(options.body)).toMatchObject({
      relationshipType: "ENHANCEMENT",
      effectiveFrom: null,
      effectiveTo: null,
      description: null,
    });
  });

  it("프로젝트 운영 관계의 상태를 변경한다", async () => {
    mockBackendResponse({
      ...projectOperationResponse,
      status: "COMPLETED",
    });

    const response = await app.inject({
      method: "PATCH",
      url: "/api/project-operations/1/status",
      headers: {
        cookie: cookieHeader,
      },
      payload: {
        status: "COMPLETED",
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json().data.status).toBe("COMPLETED");

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe(
      "/api/project-operations/1/status",
    );
    expect(options.method).toBe("PATCH");
    expect(JSON.parse(options.body)).toMatchObject({
      status: "COMPLETED",
    });
  });

  it("프로젝트 ID 기준으로 운영 관계 목록을 조회한다", async () => {
    mockBackendResponse([projectOperationResponse]);

    const response = await app.inject({
      method: "GET",
      url: "/api/project-operations/by-project/10",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json().data).toEqual([projectOperationResponse]);

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe(
      "/api/project-operations/by-project/10",
    );
    expect(options.method).toBe("GET");
  });

  it("운영 ID 기준으로 프로젝트 운영 관계 목록을 조회한다", async () => {
    mockBackendResponse([projectOperationResponse]);

    const response = await app.inject({
      method: "GET",
      url: "/api/project-operations/by-operation/20",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json().data).toEqual([projectOperationResponse]);

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe(
      "/api/project-operations/by-operation/20",
    );
    expect(options.method).toBe("GET");
  });

  it("세션 쿠키가 없으면 Backend를 호출하지 않고 401을 반환한다", async () => {
    const response = await app.inject({
      method: "GET",
      url: "/api/project-operations/1",
    });

    expect(response.statusCode).toBe(401);
    expect(response.json()).toMatchObject({
      success: false,
      code: "UNAUTHORIZED",
    });
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("잘못된 프로젝트 운영 관계 ID는 Backend 호출 전에 거부한다", async () => {
    const response = await app.inject({
      method: "GET",
      url: "/api/project-operations/invalid",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(400);
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("필수 생성 필드가 누락되면 400을 반환한다", async () => {
    const response = await app.inject({
      method: "POST",
      url: "/api/project-operations/",
      headers: {
        cookie: cookieHeader,
      },
      payload: {
        projectId: 10,
        relationshipType: "BUILD_RESULT",
      },
    });

    expect(response.statusCode).toBe(400);
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("잘못된 관계 유형은 Backend 호출 전에 거부한다", async () => {
    const response = await app.inject({
      method: "POST",
      url: "/api/project-operations/",
      headers: {
        cookie: cookieHeader,
      },
      payload: {
        projectId: 10,
        operationId: 20,
        relationshipType: "INVALID_TYPE",
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
          code: "PROJECT_OPERATION_001",
          message: "프로젝트 운영 관계를 찾을 수 없습니다.",
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
      url: "/api/project-operations/999",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(404);
    expect(response.json()).toMatchObject({
      success: false,
      code: "PROJECT_OPERATION_001",
      message: "프로젝트 운영 관계를 찾을 수 없습니다.",
    });
  });
});
