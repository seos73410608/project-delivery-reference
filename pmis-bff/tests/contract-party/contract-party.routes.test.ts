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

describe("Contract Party routes", () => {
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

  const contractPartyResponse = {
    id: 1,
    contractId: 10,
    contractCode: "CONT-001",
    contractName: "테스트 계약",
    companyId: 20,
    companyCode: "COMP-001",
    companyName: "테스트 회사",
    organizationId: 30,
    organizationCode: "ORG-001",
    organizationName: "테스트 조직",
    partyRole: "CONTRACTOR",
    primary: true,
    effectiveFrom: "2026-01-01",
    effectiveTo: null,
    description: "계약 참여 설명",
    createdAt: "2026-01-01T09:00:00",
    updatedAt: "2026-01-01T09:00:00",
  };

  const createRequest = {
    companyId: 20,
    organizationId: 30,
    partyRole: "CONTRACTOR",
    primary: true,
    effectiveFrom: "2026-01-01",
    description: "계약 참여 설명",
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

  it("계약별 참여 회사 목록을 조회한다", async () => {
    fetchMock.mockResolvedValue(
      new Response(
        JSON.stringify({
          success: true,
          code: "SUCCESS",
          data: [contractPartyResponse],
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
      url: "/api/contracts/10/parties",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json()).toMatchObject({
      success: true,
      code: "SUCCESS",
      data: [contractPartyResponse],
    });

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe(
      "/api/contracts/10/parties",
    );
    expect(options.method).toBe("GET");
    expect(options.headers.Authorization).toBe(
      "Bearer test-access-token",
    );
  });

  it("회사별 계약 참여 이력을 조회한다", async () => {
    fetchMock.mockResolvedValue(
      new Response(
        JSON.stringify({
          success: true,
          code: "SUCCESS",
          data: [contractPartyResponse],
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
      url: "/api/companies/20/contract-parties",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json()).toMatchObject({
      success: true,
      data: [contractPartyResponse],
    });

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe(
      "/api/companies/20/contract-parties",
    );
    expect(options.method).toBe("GET");
  });

  it("계약에 참여 회사를 등록하고 Backend Bearer 토큰을 전달한다", async () => {
    fetchMock.mockResolvedValue(
      new Response(
        JSON.stringify({
          success: true,
          code: "SUCCESS",
          data: contractPartyResponse,
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
      url: "/api/contracts/10/parties",
      headers: {
        cookie: cookieHeader,
      },
      payload: createRequest,
    });

    expect(response.statusCode).toBe(201);
    expect(response.json()).toMatchObject({
      success: true,
      code: "SUCCESS",
      data: contractPartyResponse,
    });

    expect(fetchMock).toHaveBeenCalledOnce();

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe(
      "/api/contracts/10/parties",
    );
    expect(options.method).toBe("POST");
    expect(options.headers.Authorization).toBe(
      "Bearer test-access-token",
    );

    expect(JSON.parse(options.body)).toMatchObject(createRequest);
  });

  it("필수 필드만으로 참여 회사를 등록할 수 있다", async () => {
    fetchMock.mockResolvedValue(
      new Response(
        JSON.stringify({
          success: true,
          code: "SUCCESS",
          data: contractPartyResponse,
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
      url: "/api/contracts/10/parties",
      headers: {
        cookie: cookieHeader,
      },
      payload: {
        companyId: 20,
        partyRole: "CLIENT",
        effectiveFrom: "2026-01-01",
      },
    });

    expect(response.statusCode).toBe(201);

    const [, options] = fetchMock.mock.calls[0];

    expect(JSON.parse(options.body)).toEqual({
      companyId: 20,
      partyRole: "CLIENT",
      effectiveFrom: "2026-01-01",
    });
  });

  it("계약 참여를 종료 처리하고 effectiveTo를 전달한다", async () => {
    fetchMock.mockResolvedValue(
      new Response(
        JSON.stringify({
          success: true,
          code: "SUCCESS",
          data: {
            ...contractPartyResponse,
            effectiveTo: "2026-06-30",
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
      url: "/api/contracts/10/parties/1/close?effectiveTo=2026-06-30",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json().data.effectiveTo).toBe("2026-06-30");

    const [url, options] = fetchMock.mock.calls[0];

    const requestUrl = new URL(url);

    expect(requestUrl.pathname).toBe(
      "/api/contracts/10/parties/1/close",
    );
    expect(requestUrl.searchParams.get("effectiveTo")).toBe(
      "2026-06-30",
    );
    expect(options.method).toBe("PATCH");
  });

  it("대표 참여 회사를 변경한다", async () => {
    fetchMock.mockResolvedValue(
      new Response(
        JSON.stringify({
          success: true,
          code: "SUCCESS",
          data: {
            ...contractPartyResponse,
            primary: false,
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
      url: "/api/contracts/10/parties/1/primary?primary=false",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json().data.primary).toBe(false);

    const [url, options] = fetchMock.mock.calls[0];

    const requestUrl = new URL(url);

    expect(requestUrl.pathname).toBe(
      "/api/contracts/10/parties/1/primary",
    );
    expect(requestUrl.searchParams.get("primary")).toBe("false");
    expect(options.method).toBe("PATCH");
  });

  it("세션 쿠키가 없으면 Backend를 호출하지 않고 401을 반환한다", async () => {
    const response = await app.inject({
      method: "GET",
      url: "/api/contracts/10/parties",
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
      url: "/api/contracts/invalid/parties",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(400);
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("잘못된 회사 ID는 Backend 호출 전에 거부한다", async () => {
    const response = await app.inject({
      method: "GET",
      url: "/api/companies/invalid/contract-parties",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(400);
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("필수 참여 회사 등록 필드가 누락되면 400을 반환한다", async () => {
    const response = await app.inject({
      method: "POST",
      url: "/api/contracts/10/parties",
      headers: {
        cookie: cookieHeader,
      },
      payload: {
        companyId: 20,
        partyRole: "CONTRACTOR",
      },
    });

    expect(response.statusCode).toBe(400);
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("잘못된 참여 역할은 Backend 호출 전에 거부한다", async () => {
    const response = await app.inject({
      method: "POST",
      url: "/api/contracts/10/parties",
      headers: {
        cookie: cookieHeader,
      },
      payload: {
        companyId: 20,
        partyRole: "INVALID",
        effectiveFrom: "2026-01-01",
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
          code: "CONTRACT_PARTY_001",
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
      url: "/api/contracts/999/parties",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(404);
    expect(response.json()).toMatchObject({
      success: false,
      code: "CONTRACT_PARTY_001",
      message: "계약을 찾을 수 없습니다.",
    });
  });
});