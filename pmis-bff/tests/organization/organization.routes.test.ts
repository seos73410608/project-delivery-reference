
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

describe("Organization routes", () => {
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

  const organizationResponse = {
    id: 1,
    companyId: 1,
    companyCode: "COMP-001",
    companyName: "테스트 회사",
    organizationCode: "ORG-001",
    organizationName: "개발본부",
    organizationType: "DIVISION",
    parentOrganizationId: null,
    parentOrganizationName: null,
    managerUserId: 1,
    managerName: "테스트 사용자",
    status: "ACTIVE",
    effectiveFrom: "2026-01-01",
    effectiveTo: null,
    description: "개발 조직",
    createdAt: "2026-01-01T09:00:00",
    updatedAt: "2026-01-01T09:00:00",
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

  it("회사별 조직 목록을 조회한다", async () => {
    fetchMock.mockResolvedValue(
      new Response(
        JSON.stringify({
          success: true,
          code: "SUCCESS",
          data: [organizationResponse],
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
      url: "/api/companies/1/organizations",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json()).toMatchObject({
      success: true,
      code: "SUCCESS",
      data: [organizationResponse],
    });

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe(
      "/api/companies/1/organizations",
    );
    expect(options.method).toBe("GET");
    expect(options.headers.Authorization).toBe(
      "Bearer test-access-token",
    );
  });

  it("조직 상세 정보를 조회한다", async () => {
    fetchMock.mockResolvedValue(
      new Response(
        JSON.stringify({
          success: true,
          code: "SUCCESS",
          data: organizationResponse,
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
      url: "/api/companies/1/organizations/1",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json()).toMatchObject({
      success: true,
      data: organizationResponse,
    });

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe(
      "/api/companies/1/organizations/1",
    );
    expect(options.method).toBe("GET");
  });

  it("회사의 조직을 생성한다", async () => {
    fetchMock.mockResolvedValue(
      new Response(
        JSON.stringify({
          success: true,
          code: "SUCCESS",
          data: organizationResponse,
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
      url: "/api/companies/1/organizations",
      headers: {
        cookie: cookieHeader,
      },
      payload: {
        organizationCode: "ORG-001",
        organizationName: "개발본부",
        organizationType: "DIVISION",
        managerUserId: 1,
        effectiveFrom: "2026-01-01",
        description: "개발 조직",
      },
    });

    expect(response.statusCode).toBe(201);
    expect(response.json()).toMatchObject({
      success: true,
      data: organizationResponse,
    });

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe(
      "/api/companies/1/organizations",
    );
    expect(options.method).toBe("POST");
    expect(options.headers.Authorization).toBe(
      "Bearer test-access-token",
    );
    expect(JSON.parse(options.body)).toMatchObject({
      organizationCode: "ORG-001",
      organizationName: "개발본부",
      organizationType: "DIVISION",
    });
  });

  it("조직 정보를 수정한다", async () => {
    fetchMock.mockResolvedValue(
      new Response(
        JSON.stringify({
          success: true,
          code: "SUCCESS",
          data: {
            ...organizationResponse,
            organizationName: "플랫폼개발본부",
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
      url: "/api/companies/1/organizations/1",
      headers: {
        cookie: cookieHeader,
      },
      payload: {
        organizationName: "플랫폼개발본부",
        organizationType: "DIVISION",
        managerUserId: 1,
        effectiveFrom: "2026-01-01",
        description: "플랫폼 개발 조직",
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json().data.organizationName).toBe(
      "플랫폼개발본부",
    );

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe(
      "/api/companies/1/organizations/1",
    );
    expect(options.method).toBe("PUT");
    expect(JSON.parse(options.body)).toMatchObject({
      organizationName: "플랫폼개발본부",
      organizationType: "DIVISION",
    });
  });

  it("조직 상태를 변경한다", async () => {
    fetchMock.mockResolvedValue(
      new Response(
        JSON.stringify({
          success: true,
          code: "SUCCESS",
          data: {
            ...organizationResponse,
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
      url: "/api/companies/1/organizations/1/status?status=INACTIVE",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json().data.status).toBe("INACTIVE");

    const [url, options] = fetchMock.mock.calls[0];

    const requestUrl = new URL(url);

    expect(requestUrl.pathname).toBe(
      "/api/companies/1/organizations/1/status",
    );
    expect(requestUrl.searchParams.get("status")).toBe("INACTIVE");
    expect(options.method).toBe("PATCH");
  });

  it("세션 쿠키가 없으면 Backend를 호출하지 않고 401을 반환한다", async () => {
    const response = await app.inject({
      method: "GET",
      url: "/api/companies/1/organizations",
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
      url: "/api/companies/invalid/organizations",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(400);
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("잘못된 조직 ID는 Backend 호출 전에 거부한다", async () => {
    const response = await app.inject({
      method: "GET",
      url: "/api/companies/1/organizations/invalid",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(400);
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("필수 조직 생성 필드가 누락되면 400을 반환한다", async () => {
    const response = await app.inject({
      method: "POST",
      url: "/api/companies/1/organizations",
      headers: {
        cookie: cookieHeader,
      },
      payload: {
        organizationName: "개발본부",
      },
    });

    expect(response.statusCode).toBe(400);
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("잘못된 조직 유형은 Backend 호출 전에 거부한다", async () => {
    const response = await app.inject({
      method: "POST",
      url: "/api/companies/1/organizations",
      headers: {
        cookie: cookieHeader,
      },
      payload: {
        organizationCode: "ORG-001",
        organizationName: "개발본부",
        organizationType: "INVALID",
      },
    });

    expect(response.statusCode).toBe(400);
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("잘못된 조직 상태는 Backend 호출 전에 거부한다", async () => {
    const response = await app.inject({
      method: "PATCH",
      url: "/api/companies/1/organizations/1/status?status=INVALID",
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
          code: "ORGANIZATION_001",
          message: "조직을 찾을 수 없습니다.",
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
      url: "/api/companies/1/organizations/999",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(404);
    expect(response.json()).toMatchObject({
      success: false,
      code: "ORGANIZATION_001",
      message: "조직을 찾을 수 없습니다.",
    });
  });
});
