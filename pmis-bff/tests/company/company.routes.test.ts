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

describe("Company routes", () => {
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

  const companyResponse = {
    id: 1,
    companyCode: "COMP-001",
    companyName: "테스트 회사",
    legalName: "테스트 회사 주식회사",
    businessRegistrationNumber: "123-45-67890",
    countryCode: "KOR",
    website: "https://example.com",
    status: "ACTIVE",
    description: "회사 설명",
    createdAt: "2026-01-01T09:00:00",
    updatedAt: "2026-01-01T09:00:00",
  };

  const roleAssignmentResponse = {
    id: 1,
    companyId: 1,
    companyCode: "COMP-001",
    companyName: "테스트 회사",
    companyRoleId: 1,
    roleCode: "CUSTOMER",
    roleName: "고객사",
    effectiveFrom: "2026-01-01",
    effectiveTo: null,
    description: "고객사 역할",
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

  it("회사를 생성하고 Backend Bearer 토큰을 전달한다", async () => {
    fetchMock.mockResolvedValue(
      new Response(
        JSON.stringify({
          success: true,
          code: "SUCCESS",
          data: companyResponse,
        }),
        {
          status: 201,
          headers: {
            "Content-Type": "application/json",
          },
        },
      ),
    );

    const response = await app.inject({
      method: "POST",
      url: "/api/companies/",
      headers: {
        cookie: cookieHeader,
      },
      payload: {
        companyCode: "COMP-001",
        companyName: "테스트 회사",
        legalName: "테스트 회사 주식회사",
        businessRegistrationNumber: "123-45-67890",
        countryCode: "KOR",
        website: "https://example.com",
        description: "회사 설명",
      },
    });

    expect(response.statusCode).toBe(201);
    expect(response.json()).toMatchObject({
      success: true,
      code: "SUCCESS",
      data: companyResponse,
    });

    expect(fetchMock).toHaveBeenCalledOnce();

    const [, options] = fetchMock.mock.calls[0];

    expect(options).toMatchObject({
      method: "POST",
      headers: {
        Authorization: "Bearer test-access-token",
      },
    });
  });

  it("회사 목록을 검색하고 Query Parameter를 전달한다", async () => {
    const pageResponse = {
      totalElements: 1,
      totalPages: 1,
      size: 20,
      content: [companyResponse],
      number: 0,
      sort: {
        empty: false,
        sorted: true,
        unsorted: false,
      },
      pageable: {
        offset: 0,
        sort: {
          empty: false,
          sorted: true,
          unsorted: false,
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

    fetchMock.mockResolvedValue(
      new Response(
        JSON.stringify({
          success: true,
          code: "SUCCESS",
          data: pageResponse,
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
      url: "/api/companies/?companyName=테스트&page=0&size=20&sort=companyName,asc",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json()).toMatchObject({
      success: true,
      data: pageResponse,
    });

    const [url, options] = fetchMock.mock.calls[0];
    const requestUrl = new URL(url);

    expect(requestUrl.pathname).toBe("/api/companies");
    expect(requestUrl.searchParams.get("companyName")).toBe("테스트");
    expect(requestUrl.searchParams.get("page")).toBe("0");
    expect(requestUrl.searchParams.get("size")).toBe("20");
    expect(requestUrl.searchParams.get("sort")).toBe("companyName,asc");
    expect(options.headers.Authorization).toBe(
      "Bearer test-access-token",
    );
  });

  it("회사 상세 정보를 조회한다", async () => {
    fetchMock.mockResolvedValue(
      new Response(
        JSON.stringify({
          success: true,
          code: "SUCCESS",
          data: companyResponse,
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
      url: "/api/companies/1",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json()).toMatchObject({
      success: true,
      data: companyResponse,
    });

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe("/api/companies/1");
    expect(options.method).toBe("GET");
  });

  it("회사를 수정한다", async () => {
    fetchMock.mockResolvedValue(
      new Response(
        JSON.stringify({
          success: true,
          code: "SUCCESS",
          data: {
            ...companyResponse,
            companyName: "수정된 회사",
          },
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
      method: "PUT",
      url: "/api/companies/1",
      headers: {
        cookie: cookieHeader,
      },
      payload: {
        companyName: "수정된 회사",
        legalName: "수정된 회사 주식회사",
        businessRegistrationNumber: "123-45-67890",
        countryCode: "KOR",
        website: "https://example.com",
        description: "수정된 회사 설명",
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json().data.companyName).toBe("수정된 회사");

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe("/api/companies/1");
    expect(options.method).toBe("PUT");
    expect(JSON.parse(options.body)).toMatchObject({
      companyName: "수정된 회사",
      countryCode: "KOR",
    });
  });

  it("회사 상태를 변경한다", async () => {
    fetchMock.mockResolvedValue(
      new Response(
        JSON.stringify({
          success: true,
          code: "SUCCESS",
          data: {
            ...companyResponse,
            status: "INACTIVE",
          },
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
      method: "PATCH",
      url: "/api/companies/1/status?status=INACTIVE",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json().data.status).toBe("INACTIVE");

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe("/api/companies/1/status");
    expect(new URL(url).searchParams.get("status")).toBe("INACTIVE");
    expect(options.method).toBe("PATCH");
  });

  it("회사의 역할 할당 목록을 조회한다", async () => {
    fetchMock.mockResolvedValue(
      new Response(
        JSON.stringify({
          success: true,
          code: "SUCCESS",
          data: [roleAssignmentResponse],
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
      url: "/api/companies/1/roles",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json()).toMatchObject({
      success: true,
      data: [roleAssignmentResponse],
    });

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe("/api/companies/1/roles");
    expect(options.method).toBe("GET");
  });

  it("회사에 역할을 할당한다", async () => {
    fetchMock.mockResolvedValue(
      new Response(
        JSON.stringify({
          success: true,
          code: "SUCCESS",
          data: roleAssignmentResponse,
        }),
        {
          status: 201,
          headers: {
            "Content-Type": "application/json",
          },
        },
      ),
    );

    const response = await app.inject({
      method: "POST",
      url: "/api/companies/1/roles",
      headers: {
        cookie: cookieHeader,
      },
      payload: {
        roleCode: "CUSTOMER",
        effectiveFrom: "2026-01-01",
        description: "고객사 역할",
      },
    });

    expect(response.statusCode).toBe(201);
    expect(response.json()).toMatchObject({
      success: true,
      data: roleAssignmentResponse,
    });

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe("/api/companies/1/roles");
    expect(options.method).toBe("POST");
    expect(JSON.parse(options.body)).toMatchObject({
      roleCode: "CUSTOMER",
      effectiveFrom: "2026-01-01",
    });
  });

  it("회사 역할 할당을 종료한다", async () => {
    fetchMock.mockResolvedValue(
      new Response(
        JSON.stringify({
          success: true,
          code: "SUCCESS",
          data: {
            ...roleAssignmentResponse,
            effectiveTo: "2026-12-31",
          },
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
      method: "PATCH",
      url: "/api/companies/1/roles/1/close",
      headers: {
        cookie: cookieHeader,
      },
      payload: {
        effectiveTo: "2026-12-31",
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json().data.effectiveTo).toBe("2026-12-31");

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe(
      "/api/companies/1/roles/1/close",
    );
    expect(options.method).toBe("PATCH");
    expect(JSON.parse(options.body)).toMatchObject({
      effectiveTo: "2026-12-31",
    });
  });

  it("세션 쿠키가 없으면 Backend를 호출하지 않고 401을 반환한다", async () => {
    const response = await app.inject({
      method: "GET",
      url: "/api/companies/",
    });

    expect(response.statusCode).toBe(401);
    expect(response.json()).toMatchObject({
      success: false,
      code: "UNAUTHORIZED",
    });
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("잘못된 회사 ID는 Backend 호출 전에 거부한다", async () => {
    const response = await app.inject({
      method: "GET",
      url: "/api/companies/invalid",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(400);
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("필수 회사 생성 필드가 누락되면 400을 반환한다", async () => {
    const response = await app.inject({
      method: "POST",
      url: "/api/companies/",
      headers: {
        cookie: cookieHeader,
      },
      payload: {
        companyName: "테스트 회사",
      },
    });

    expect(response.statusCode).toBe(400);
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("잘못된 회사 역할 할당 ID는 Backend 호출 전에 거부한다", async () => {
    const response = await app.inject({
      method: "PATCH",
      url: "/api/companies/1/roles/invalid/close",
      headers: {
        cookie: cookieHeader,
      },
      payload: {
        effectiveTo: "2026-12-31",
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
          code: "COMPANY_001",
          message: "회사를 찾을 수 없습니다.",
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
      url: "/api/companies/999",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(404);
    expect(response.json()).toMatchObject({
      success: false,
      code: "COMPANY_001",
      message: "회사를 찾을 수 없습니다.",
    });
  });
});