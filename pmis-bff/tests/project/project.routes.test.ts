
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

describe("Project routes", () => {
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

  const projectResponse = {
    id: 1,
    projectCode: "PRJ-001",
    projectName: "PMIS 구축",
    customerName: "테스트 고객사",
    projectManager: "홍길동",
    description: "프로젝트 설명",
    startDate: "2026-01-01",
    endDate: "2026-12-31",
    status: "PLANNING",
    priority: "HIGH",
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

  it("프로젝트를 생성하고 Backend Bearer 토큰을 전달한다", async () => {
    fetchMock.mockResolvedValue(
      new Response(
        JSON.stringify({
          success: true,
          code: "SUCCESS",
          data: projectResponse,
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
      url: "/api/projects/",
      headers: {
        cookie: cookieHeader,
      },
      payload: {
        projectCode: "PRJ-001",
        projectName: "PMIS 구축",
        customerName: "테스트 고객사",
        projectManager: "홍길동",
        description: "프로젝트 설명",
        startDate: "2026-01-01",
        endDate: "2026-12-31",
        priority: "HIGH",
      },
    });

    expect(response.statusCode).toBe(201);
    expect(response.json()).toMatchObject({
      success: true,
      code: "SUCCESS",
      data: projectResponse,
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

  it("프로젝트 목록을 검색하고 Query Parameter를 전달한다", async () => {
    const pageResponse = {
      totalElements: 1,
      totalPages: 1,
      size: 20,
      content: [projectResponse],
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
      url: "/api/projects/?projectName=PMIS&page=0&size=20&sort=projectName,asc",
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

    expect(requestUrl.pathname).toBe("/api/projects");
    expect(requestUrl.searchParams.get("projectName")).toBe("PMIS");
    expect(requestUrl.searchParams.get("page")).toBe("0");
    expect(requestUrl.searchParams.get("size")).toBe("20");
    expect(requestUrl.searchParams.get("sort")).toBe("projectName,asc");
    expect(options.headers.Authorization).toBe(
      "Bearer test-access-token",
    );
  });

  it("프로젝트를 수정한다", async () => {
    fetchMock.mockResolvedValue(
      new Response(
        JSON.stringify({
          success: true,
          code: "SUCCESS",
          data: {
            ...projectResponse,
            projectName: "PMIS 고도화",
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
      url: "/api/projects/1",
      headers: {
        cookie: cookieHeader,
      },
      payload: {
        projectName: "PMIS 고도화",
        customerName: "테스트 고객사",
        projectManager: "홍길동",
        description: "수정된 설명",
        startDate: "2026-01-01",
        endDate: "2026-12-31",
        status: "IN_PROGRESS",
        priority: "HIGH",
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json().data.projectName).toBe("PMIS 고도화");

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe("/api/projects/1");
    expect(options.method).toBe("PUT");
    expect(JSON.parse(options.body)).toMatchObject({
      projectName: "PMIS 고도화",
      status: "IN_PROGRESS",
    });
  });

  it("프로젝트를 삭제한다", async () => {
    fetchMock.mockResolvedValue(
      new Response(
        JSON.stringify({
          success: true,
          code: "SUCCESS",
          data: "프로젝트가 삭제되었습니다.",
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
      method: "DELETE",
      url: "/api/projects/1",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json()).toMatchObject({
      success: true,
      data: "프로젝트가 삭제되었습니다.",
    });

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe("/api/projects/1");
    expect(options.method).toBe("DELETE");
  });

  it("프로젝트 상세 정보를 조회한다", async () => {
    const detailResponse = {
      ...projectResponse,
      progressRate: 50,
      totalPlannedManDays: 100,
      weightedCompletedManDays: 50,
      missingEffortTaskCount: 2,
    };

    fetchMock.mockResolvedValue(
      new Response(
        JSON.stringify({
          success: true,
          code: "SUCCESS",
          data: detailResponse,
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
      url: "/api/projects/1/detail",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json()).toMatchObject({
      success: true,
      data: detailResponse,
    });

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe("/api/projects/1/detail");
    expect(options.method).toBe("GET");
  });

  it("세션 쿠키가 없으면 Backend를 호출하지 않고 401을 반환한다", async () => {
    const response = await app.inject({
      method: "GET",
      url: "/api/projects/",
    });

    expect(response.statusCode).toBe(401);
    expect(response.json()).toMatchObject({
      success: false,
      code: "UNAUTHORIZED",
    });
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("잘못된 프로젝트 ID는 Backend 호출 전에 거부한다", async () => {
    const response = await app.inject({
      method: "GET",
      url: "/api/projects/invalid/detail",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(400);
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("필수 프로젝트 생성 필드가 누락되면 400을 반환한다", async () => {
    const response = await app.inject({
      method: "POST",
      url: "/api/projects/",
      headers: {
        cookie: cookieHeader,
      },
      payload: {
        projectName: "PMIS 구축",
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

    const response = await app.inject({
      method: "GET",
      url: "/api/projects/999/detail",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(404);
    expect(response.json()).toMatchObject({
      success: false,
      code: "PROJECT_001",
      message: "프로젝트를 찾을 수 없습니다.",
    });
  });
});
