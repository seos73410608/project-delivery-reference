
import {
  afterEach,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from "vitest";
import type { FastifyInstance } from "fastify";
import { createTestApp } from "../helpers/create-test-app.js";
import { MemorySessionStore } from "../../src/auth/session/memory-session-store.js";
import type { BffSession } from "../../src/auth/session/session.types.js";

describe("OperationalScope routes", () => {
  let app: Awaited<ReturnType<typeof createTestApp>>;
  let sessionStore: MemorySessionStore;
  let fetchMock: ReturnType<typeof vi.fn>;

  const session: BffSession = {
    sessionId: "123e4567-e89b-42d3-a456-426614174000",
    accessToken: "test-access-token",
    refreshToken: "test-refresh-token",
    user: {
      userId: 1,
      username: "admin",
      name: "테스트 관리자",
      role: "ADMIN",
    },
    expiresAt: Date.now() + 5 * 60 * 1000,
    createdAt: Date.now(),
    updatedAt: Date.now(),
  };

  const cookieHeader = `pmis_session=${session.sessionId}`;

  const operationalScopeResponse = {
    id: 1,
    scopeCode: "OPS-0001",
    scopeName: "통합 운영 범위",
    scopeType: "SERVICE",
    status: "ACTIVE",
    parentScopeId: null,
    parentScopeCode: null,
    parentScopeName: null,
    ownerId: 10,
    ownerUsername: "operator",
    ownerName: "운영 담당자",
    description: "통합 운영 범위 설명",
    effectiveFrom: "2026-01-01",
    effectiveTo: null,
    createdAt: "2026-01-01T09:00:00",
    updatedAt: "2026-01-02T09:00:00",
    version: 0,
  };

  const pageOperationalScopeResponse = {
    totalElements: 1,
    totalPages: 1,
    size: 20,
    content: [operationalScopeResponse],
    number: 0,
    sort: {
      empty: true,
      sorted: false,
      unsorted: true,
    },
    pageable: {
      offset: 0,
      sort: {
        empty: true,
        sorted: false,
        unsorted: true,
      },
      paged: true,
      pageNumber: 0,
      pageSize: 20,
      unpaged: false,
    },
    first: true,
    last: true,
    numberOfElements: 1,
    empty: false,
  };

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

  it("운영 범위를 생성한다", async () => {
    fetchMock.mockResolvedValue(
      new Response(
        JSON.stringify({
          success: true,
          code: "SUCCESS",
          data: operationalScopeResponse,
        }),
        {
          status: 201,
          headers: {
            "Content-Type": "application/json",
          },
        },
      ),
    );

    const requestBody = {
      scopeName: "통합 운영 범위",
      scopeType: "SERVICE",
      ownerId: 10,
      description: "통합 운영 범위 설명",
      effectiveFrom: "2026-01-01",
    };

    const response = await app.inject({
      method: "POST",
      url: "/api/operational-scopes/",
      headers: {
        cookie: cookieHeader,
      },
      payload: requestBody,
    });

    expect(response.statusCode).toBe(201);
    expect(response.json()).toEqual({
      success: true,
      code: "SUCCESS",
      data: operationalScopeResponse,
    });

    expect(fetchMock).toHaveBeenCalledOnce();

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe("/api/operational-scopes");
    expect(options).toMatchObject({
      method: "POST",
      headers: {
        Authorization: "Bearer test-access-token",
      },
    });
    expect(JSON.parse(options.body)).toEqual(requestBody);
  });

  it("검색 조건과 페이지 정보를 전달하여 운영 범위를 조회한다", async () => {
    fetchMock.mockResolvedValue(
      new Response(
        JSON.stringify({
          success: true,
          code: "SUCCESS",
          data: pageOperationalScopeResponse,
        }),
        {
          status: 200,
          headers: {
            "Content-Type": "application/json",
          },
        },
      ),
    );

    const response = await app.inject({
      method: "GET",
      url:
        "/api/operational-scopes/" +
        "?scopeCode=OPS-0001" +
        "&scopeName=%ED%86%B5%ED%95%A9" +
        "&scopeType=SERVICE" +
        "&status=ACTIVE" +
        "&parentScopeId=2" +
        "&ownerId=10" +
        "&ownerKeyword=operator" +
        "&page=0" +
        "&size=20" +
        "&sort=createdAt,desc",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json()).toMatchObject({
      success: true,
      data: pageOperationalScopeResponse,
    });

    expect(fetchMock).toHaveBeenCalledOnce();

    const [url, options] = fetchMock.mock.calls[0];
    const backendUrl = new URL(url);

    expect(backendUrl.pathname).toBe("/api/operational-scopes");
    expect(backendUrl.searchParams.get("scopeCode")).toBe("OPS-0001");
    expect(backendUrl.searchParams.get("scopeName")).toBe("통합");
    expect(backendUrl.searchParams.get("scopeType")).toBe("SERVICE");
    expect(backendUrl.searchParams.get("status")).toBe("ACTIVE");
    expect(backendUrl.searchParams.get("parentScopeId")).toBe("2");
    expect(backendUrl.searchParams.get("ownerId")).toBe("10");
    expect(backendUrl.searchParams.get("ownerKeyword")).toBe("operator");
    expect(backendUrl.searchParams.get("page")).toBe("0");
    expect(backendUrl.searchParams.get("size")).toBe("20");
    expect(backendUrl.searchParams.get("sort")).toBe("createdAt,desc");

    expect(options).toMatchObject({
      method: "GET",
      headers: {
        Authorization: "Bearer test-access-token",
      },
    });
  });

  it("운영 범위 상세 정보를 조회한다", async () => {
    fetchMock.mockResolvedValue(
      new Response(
        JSON.stringify({
          success: true,
          code: "SUCCESS",
          data: operationalScopeResponse,
        }),
        {
          status: 200,
          headers: {
            "Content-Type": "application/json",
          },
        },
      ),
    );

    const response = await app.inject({
      method: "GET",
      url: "/api/operational-scopes/1",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json()).toMatchObject({
      success: true,
      data: operationalScopeResponse,
    });

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe("/api/operational-scopes/1");
    expect(options.method).toBe("GET");
  });

  it("버전 정보를 포함하여 운영 범위를 수정한다", async () => {
    const updatedResponse = {
      ...operationalScopeResponse,
      scopeName: "수정된 운영 범위",
      version: 1,
    };

    fetchMock.mockResolvedValue(
      new Response(
        JSON.stringify({
          success: true,
          code: "SUCCESS",
          data: updatedResponse,
        }),
        {
          status: 200,
          headers: {
            "Content-Type": "application/json",
          },
        },
      ),
    );

    const requestBody = {
      version: 0,
      scopeName: "수정된 운영 범위",
      scopeType: "SYSTEM",
      ownerId: 10,
      description: "수정된 설명",
      effectiveFrom: "2026-02-01",
    };

    const response = await app.inject({
      method: "PUT",
      url: "/api/operational-scopes/1",
      headers: {
        cookie: cookieHeader,
      },
      payload: requestBody,
    });

    expect(response.statusCode).toBe(200);
    expect(response.json()).toMatchObject({
      success: true,
      data: updatedResponse,
    });

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe("/api/operational-scopes/1");
    expect(options.method).toBe("PUT");
    expect(JSON.parse(options.body)).toEqual(requestBody);
  });

  it("세션이 없으면 401을 반환한다", async () => {
    const response = await app.inject({
      method: "GET",
      url: "/api/operational-scopes/",
    });

    expect(response.statusCode).toBe(401);
    expect(response.json()).toMatchObject({
      success: false,
      code: "UNAUTHORIZED",
    });
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("잘못된 운영 범위 ID를 거부한다", async () => {
    const response = await app.inject({
      method: "GET",
      url: "/api/operational-scopes/invalid",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(400);
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("생성 요청의 필수 항목이 누락되면 거부한다", async () => {
    const response = await app.inject({
      method: "POST",
      url: "/api/operational-scopes/",
      headers: {
        cookie: cookieHeader,
      },
      payload: {
        scopeType: "SERVICE",
      },
    });

    expect(response.statusCode).toBe(400);
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("수정 요청의 version이 누락되면 거부한다", async () => {
    const response = await app.inject({
      method: "PUT",
      url: "/api/operational-scopes/1",
      headers: {
        cookie: cookieHeader,
      },
      payload: {
        scopeName: "수정된 운영 범위",
        scopeType: "SERVICE",
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
          code: "OPERATIONAL_SCOPE_NOT_FOUND",
          message: "운영 범위를 찾을 수 없습니다.",
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
      url: "/api/operational-scopes/999",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(404);
    expect(response.json()).toMatchObject({
      success: false,
      code: "OPERATIONAL_SCOPE_NOT_FOUND",
      message: "운영 범위를 찾을 수 없습니다.",
    });
  });
});
