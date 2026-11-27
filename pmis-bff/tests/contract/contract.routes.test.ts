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

describe("Contract routes", () => {
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

  const contractResponse = {
    id: 1,
    contractCode: "CONT-001",
    contractName: "테스트 계약",
    contractType: "MAINTENANCE",
    status: "DRAFT",
    signedDate: "2026-01-01",
    startDate: "2026-01-01",
    endDate: "2026-12-31",
    currencyCode: "KRW",
    contractAmount: 100000000,
    description: "계약 설명",
    createdAt: "2026-01-01T09:00:00",
    updatedAt: "2026-01-01T09:00:00",
  };

  const pageResponse = {
    totalElements: 1,
    totalPages: 1,
    size: 20,
    content: [contractResponse],
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

  it("계약을 생성하고 Backend Bearer 토큰을 전달한다", async () => {
    fetchMock.mockResolvedValue(
      new Response(
        JSON.stringify({
          success: true,
          code: "SUCCESS",
          data: contractResponse,
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
      url: "/api/contracts/",
      headers: {
        cookie: cookieHeader,
      },
      payload: {
        contractCode: "CONT-001",
        contractName: "테스트 계약",
        contractType: "MAINTENANCE",
        signedDate: "2026-01-01",
        startDate: "2026-01-01",
        endDate: "2026-12-31",
        currencyCode: "KRW",
        contractAmount: 100000000,
        description: "계약 설명",
      },
    });

    expect(response.statusCode).toBe(201);
    expect(response.json()).toMatchObject({
      success: true,
      code: "SUCCESS",
      data: contractResponse,
    });

    expect(fetchMock).toHaveBeenCalledOnce();

    const [, options] = fetchMock.mock.calls[0];

    expect(options).toMatchObject({
      method: "POST",
      headers: {
        Authorization: "Bearer test-access-token",
      },
    });

    expect(JSON.parse(options.body)).toMatchObject({
      contractCode: "CONT-001",
      contractName: "테스트 계약",
      contractType: "MAINTENANCE",
      currencyCode: "KRW",
    });
  });

  it("계약 목록을 검색하고 Query Parameter를 전달한다", async () => {
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
      url: "/api/contracts/?contractName=테스트&contractType=MAINTENANCE&status=DRAFT&page=0&size=20&sort=contractName,asc",
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

    expect(requestUrl.pathname).toBe("/api/contracts");
    expect(requestUrl.searchParams.get("contractName")).toBe("테스트");
    expect(requestUrl.searchParams.get("contractType")).toBe(
      "MAINTENANCE",
    );
    expect(requestUrl.searchParams.get("status")).toBe("DRAFT");
    expect(requestUrl.searchParams.get("page")).toBe("0");
    expect(requestUrl.searchParams.get("size")).toBe("20");
    expect(requestUrl.searchParams.get("sort")).toBe(
      "contractName,asc",
    );
    expect(options.headers.Authorization).toBe(
      "Bearer test-access-token",
    );
  });

  it("계약 상세 정보를 조회한다", async () => {
    fetchMock.mockResolvedValue(
      new Response(
        JSON.stringify({
          success: true,
          code: "SUCCESS",
          data: contractResponse,
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
      url: "/api/contracts/1",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json()).toMatchObject({
      success: true,
      data: contractResponse,
    });

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe("/api/contracts/1");
    expect(options.method).toBe("GET");
  });

  it("계약을 수정한다", async () => {
    fetchMock.mockResolvedValue(
      new Response(
        JSON.stringify({
          success: true,
          code: "SUCCESS",
          data: {
            ...contractResponse,
            contractName: "수정된 계약",
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
      url: "/api/contracts/1",
      headers: {
        cookie: cookieHeader,
      },
      payload: {
        contractName: "수정된 계약",
        contractType: "SERVICE",
        signedDate: "2026-01-01",
        startDate: "2026-02-01",
        endDate: "2026-12-31",
        currencyCode: "KRW",
        contractAmount: 120000000,
        description: "수정된 계약 설명",
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json().data.contractName).toBe("수정된 계약");

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe("/api/contracts/1");
    expect(options.method).toBe("PUT");
    expect(JSON.parse(options.body)).toMatchObject({
      contractName: "수정된 계약",
      contractType: "SERVICE",
      currencyCode: "KRW",
    });
  });

  it("계약 상태를 변경한다", async () => {
    fetchMock.mockResolvedValue(
      new Response(
        JSON.stringify({
          success: true,
          code: "SUCCESS",
          data: {
            ...contractResponse,
            status: "ACTIVE",
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
      url: "/api/contracts/1/status?status=ACTIVE",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json().data.status).toBe("ACTIVE");

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe("/api/contracts/1/status");
    expect(new URL(url).searchParams.get("status")).toBe("ACTIVE");
    expect(options.method).toBe("PATCH");
  });

  it("세션 쿠키가 없으면 Backend를 호출하지 않고 401을 반환한다", async () => {
    const response = await app.inject({
      method: "GET",
      url: "/api/contracts/",
    });

    expect(response.statusCode).toBe(401);
    expect(response.json()).toMatchObject({
      success: false,
      code: "UNAUTHORIZED",
    });
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("잘못된 계약 ID는 Backend 호출 전에 거부한다", async () => {
    const response = await app.inject({
      method: "GET",
      url: "/api/contracts/invalid",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(400);
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("필수 계약 생성 필드가 누락되면 400을 반환한다", async () => {
    const response = await app.inject({
      method: "POST",
      url: "/api/contracts/",
      headers: {
        cookie: cookieHeader,
      },
      payload: {
        contractName: "테스트 계약",
      },
    });

    expect(response.statusCode).toBe(400);
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("잘못된 계약 유형은 Backend 호출 전에 거부한다", async () => {
    const response = await app.inject({
      method: "GET",
      url: "/api/contracts/?contractType=INVALID",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(400);
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("잘못된 계약 상태는 Backend 호출 전에 거부한다", async () => {
    const response = await app.inject({
      method: "PATCH",
      url: "/api/contracts/1/status?status=INVALID",
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
          code: "CONTRACT_001",
          message: "계약을 찾을 수 없습니다.",
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
      url: "/api/contracts/999",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(404);
    expect(response.json()).toMatchObject({
      success: false,
      code: "CONTRACT_001",
      message: "계약을 찾을 수 없습니다.",
    });
  });
});