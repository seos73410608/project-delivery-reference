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

describe("OperationParticipant routes", () => {
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

  const participantResponse = {
    id: 100,
    role: "OPERATOR",
    primary: true,
    company: {
      id: 10,
      companyCode: "COMP-001",
      companyName: "운영 회사",
    },
    organization: {
      id: 20,
      organizationCode: "ORG-001",
      organizationName: "운영 조직",
    },
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

  it("인계 대상 운영 참여자 목록을 조회하고 Backend Bearer 토큰을 전달한다", async () => {
    mockBackendResponse([participantResponse]);

    const response = await app.inject({
      method: "GET",
      url: "/api/operations/10/participants",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json()).toMatchObject({
      success: true,
      code: "SUCCESS",
      data: [participantResponse],
    });

    expect(fetchMock).toHaveBeenCalledOnce();

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe(
      "/api/operations/10/participants",
    );
    expect(new URL(url).search).toBe("");
    expect(options).toMatchObject({
      method: "GET",
      headers: {
        Authorization: "Bearer test-access-token",
      },
    });
  });

  it("인계 예정일을 Backend에 쿼리 파라미터로 전달한다", async () => {
    mockBackendResponse([participantResponse]);

    const response = await app.inject({
      method: "GET",
      url: "/api/operations/10/participants?plannedHandoverDate=2026-12-01",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json()).toMatchObject({
      success: true,
      code: "SUCCESS",
      data: [participantResponse],
    });

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe(
      "/api/operations/10/participants",
    );
    expect(new URL(url).searchParams.get("plannedHandoverDate")).toBe(
      "2026-12-01",
    );
    expect(options.method).toBe("GET");
  });

  it("조직 정보가 없는 참여자도 정상적으로 조회한다", async () => {
    const participantWithoutOrganization = {
      ...participantResponse,
      organization: null,
    };

    mockBackendResponse([participantWithoutOrganization]);

    const response = await app.inject({
      method: "GET",
      url: "/api/operations/10/participants",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json().data).toEqual([
      participantWithoutOrganization,
    ]);
  });

  it("세션 쿠키가 없으면 Backend를 호출하지 않고 401을 반환한다", async () => {
    const response = await app.inject({
      method: "GET",
      url: "/api/operations/10/participants",
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
      url: "/api/operations/invalid/participants",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(400);
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("인계 예정일 형식이 잘못되면 Backend 호출 전에 거부한다", async () => {
    const response = await app.inject({
      method: "GET",
      url: "/api/operations/10/participants?plannedHandoverDate=2026-13-45",
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
      url: "/api/operations/999/participants",
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