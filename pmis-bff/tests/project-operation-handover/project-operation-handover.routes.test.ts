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

describe("ProjectOperationHandover routes", () => {
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

  const handoverResponse = {
    id: 100,
    handoverType: "INITIAL",
    status: "DRAFT",
    sequenceNo: 1,
    projectOperation: {
      id: 10,
      relationshipType: "BUILD_RESULT",
      status: "ACTIVE",
    },
    project: {
      id: 20,
      projectCode: "PRJ-001",
      projectName: "PMIS 구축",
      customerName: "테스트 고객사",
      projectManager: "홍길동",
    },
    operation: {
      id: 30,
      operationCode: "OPS-001",
      operationName: "PMIS 운영",
      operationType: "APPLICATION_SERVICE",
      status: "ACTIVE",
    },
    previousHandoverId: null,
    previousSequenceNo: null,
    previousHandoverType: null,
    fromParticipant: null,
    toParticipant: {
      id: 40,
      role: "OPERATOR",
      primary: true,
      company: {
        id: 50,
        companyCode: "COMP-001",
        companyName: "테스트 회사",
      },
      organization: {
        id: 60,
        organizationCode: "ORG-001",
        organizationName: "운영팀",
      },
    },
    handedOverByUserId: null,
    handedOverByName: null,
    acceptedByUserId: null,
    acceptedByName: null,
    plannedHandoverDate: "2026-12-01",
    requestedAt: null,
    acceptedAt: null,
    completedAt: null,
    notes: "인수인계 테스트",
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

  it("최초 인수인계를 생성하고 Backend Bearer 토큰을 전달한다", async () => {
    mockBackendResponse(handoverResponse, 201);

    const payload = {
      projectOperationId: 10,
      toParticipantId: 40,
      plannedHandoverDate: "2026-12-01",
      notes: "최초 인수인계",
    };

    const response = await app.inject({
      method: "POST",
      url: "/api/project-operation-handovers/initial",
      headers: {
        cookie: cookieHeader,
      },
      payload,
    });

    expect(response.statusCode).toBe(201);
    expect(response.json()).toMatchObject({
      success: true,
      code: "SUCCESS",
      data: handoverResponse,
    });

    expect(fetchMock).toHaveBeenCalledOnce();

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe(
      "/api/project-operation-handovers/initial",
    );
    expect(options).toMatchObject({
      method: "POST",
      headers: {
        Authorization: "Bearer test-access-token",
      },
    });
    expect(JSON.parse(options.body)).toEqual(payload);
  });

  it("운영자 변경 인수인계를 생성한다", async () => {
    mockBackendResponse({
      ...handoverResponse,
      handoverType: "OPERATOR_CHANGE",
      fromParticipant: {
        ...handoverResponse.toParticipant,
        id: 41,
      },
      toParticipant: {
        ...handoverResponse.toParticipant,
        id: 42,
      },
    }, 201);

    const payload = {
      projectOperationId: 10,
      fromParticipantId: 41,
      toParticipantId: 42,
      plannedHandoverDate: "2026-12-01",
      notes: "운영자 변경",
    };

    const response = await app.inject({
      method: "POST",
      url: "/api/project-operation-handovers/operator-change",
      headers: {
        cookie: cookieHeader,
      },
      payload,
    });

    expect(response.statusCode).toBe(201);
    expect(response.json().data.handoverType).toBe("OPERATOR_CHANGE");

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe(
      "/api/project-operation-handovers/operator-change",
    );
    expect(options.method).toBe("POST");
    expect(JSON.parse(options.body)).toEqual(payload);
  });

  it("동일 운영 관계의 재인수인계를 생성한다", async () => {
    mockBackendResponse({
      ...handoverResponse,
      handoverType: "RE_HANDOVER",
      sequenceNo: 2,
      previousHandoverId: 99,
      previousSequenceNo: 1,
      previousHandoverType: "INITIAL",
    }, 201);

    const payload = {
      projectOperationId: 10,
      fromParticipantId: 41,
      toParticipantId: 40,
      plannedHandoverDate: "2026-12-01",
      notes: "동일 운영자 재인수인계",
    };

    const response = await app.inject({
      method: "POST",
      url: "/api/project-operation-handovers/re-handover",
      headers: {
        cookie: cookieHeader,
      },
      payload,
    });

    expect(response.statusCode).toBe(201);
    expect(response.json().data).toMatchObject({
      handoverType: "RE_HANDOVER",
      sequenceNo: 2,
      previousHandoverId: 99,
    });

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe(
      "/api/project-operation-handovers/re-handover",
    );
    expect(options.method).toBe("POST");
    expect(JSON.parse(options.body)).toEqual(payload);
  });

  it("인수인계 단건 정보를 조회한다", async () => {
    mockBackendResponse(handoverResponse);

    const response = await app.inject({
      method: "GET",
      url: "/api/project-operation-handovers/100",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json()).toMatchObject({
      success: true,
      data: handoverResponse,
    });

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe(
      "/api/project-operation-handovers/100",
    );
    expect(options.method).toBe("GET");
    expect(options.headers.Authorization).toBe(
      "Bearer test-access-token",
    );
  });

  it("프로젝트 운영 관계 기준으로 인수인계 이력을 조회한다", async () => {
    mockBackendResponse([handoverResponse]);

    const response = await app.inject({
      method: "GET",
      url: "/api/project-operation-handovers/by-project-operation/10",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json().data).toEqual([handoverResponse]);

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe(
      "/api/project-operation-handovers/by-project-operation/10",
    );
    expect(options.method).toBe("GET");
  });

  it.each([
    ["request", "REQUESTED"],
    ["start", "IN_PROGRESS"],
    ["submit-for-review", "REVIEW"],
    ["accept", "ACCEPTED"],
    ["complete", "COMPLETED"],
    ["reject", "REJECTED"],
    ["cancel", "CANCELLED"],
  ])("%s 상태 전이 API를 호출한다", async (action, status) => {
    mockBackendResponse({
      ...handoverResponse,
      status,
    });

    const response = await app.inject({
      method: "PATCH",
      url: `/api/project-operation-handovers/100/${action}`,
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json().data.status).toBe(status);

    expect(fetchMock).toHaveBeenCalledOnce();

    const [url, options] = fetchMock.mock.calls[0];

    expect(new URL(url).pathname).toBe(
      `/api/project-operation-handovers/100/${action}`,
    );
    expect(options.method).toBe("PATCH");
    expect(options.headers.Authorization).toBe(
      "Bearer test-access-token",
    );
    expect(options.body).toBeUndefined();
  });

  it("세션 쿠키가 없으면 Backend를 호출하지 않고 401을 반환한다", async () => {
    const response = await app.inject({
      method: "GET",
      url: "/api/project-operation-handovers/100",
    });

    expect(response.statusCode).toBe(401);
    expect(response.json()).toMatchObject({
      success: false,
      code: "UNAUTHORIZED",
    });
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("잘못된 인수인계 ID는 Backend 호출 전에 거부한다", async () => {
    const response = await app.inject({
      method: "GET",
      url: "/api/project-operation-handovers/invalid",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(400);
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("필수 최초 인수인계 생성 필드가 누락되면 400을 반환한다", async () => {
    const response = await app.inject({
      method: "POST",
      url: "/api/project-operation-handovers/initial",
      headers: {
        cookie: cookieHeader,
      },
      payload: {
        projectOperationId: 10,
      },
    });

    expect(response.statusCode).toBe(400);
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("운영자 변경 생성 시 필수 참여자 필드가 누락되면 400을 반환한다", async () => {
    const response = await app.inject({
      method: "POST",
      url: "/api/project-operation-handovers/operator-change",
      headers: {
        cookie: cookieHeader,
      },
      payload: {
        projectOperationId: 10,
        toParticipantId: 42,
      },
    });

    expect(response.statusCode).toBe(400);
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("잘못된 날짜 형식은 Backend 호출 전에 거부한다", async () => {
    const response = await app.inject({
      method: "POST",
      url: "/api/project-operation-handovers/initial",
      headers: {
        cookie: cookieHeader,
      },
      payload: {
        projectOperationId: 10,
        toParticipantId: 40,
        plannedHandoverDate: "2026-13-45",
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
          code: "PROJECT_OPERATION_HANDOVER_001",
          message: "인수인계 정보를 찾을 수 없습니다.",
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
      url: "/api/project-operation-handovers/999",
      headers: {
        cookie: cookieHeader,
      },
    });

    expect(response.statusCode).toBe(404);
    expect(response.json()).toMatchObject({
      success: false,
      code: "PROJECT_OPERATION_HANDOVER_001",
      message: "인수인계 정보를 찾을 수 없습니다.",
    });
  });
});