import Fastify from "fastify";
import cookie from "@fastify/cookie";
import {
  afterEach,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from "vitest";
import { MemorySessionStore } from "../../../src/auth/session/memory-session-store.js";
import type { BffSession } from "../../../src/auth/session/session.types.js";
import { requireSession } from "../../../src/common/auth/require-session.js";

describe("requireSession", () => {
  let app: Fastify.FastifyInstance;
  let sessionStore: MemorySessionStore;

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

  beforeEach(async () => {
    sessionStore = new MemorySessionStore();

    app = Fastify({
      logger: false,
    });

    app.decorateRequest("backendAccessToken", null);
    app.decorate("sessionStore", sessionStore);

    await app.register(cookie);

    app.get(
      "/protected",
      {
        preHandler: requireSession,
      },
      async (request) => ({
        accessToken: request.backendAccessToken,
      }),
    );

    await app.ready();
  });

  afterEach(async () => {
    await app.close();
  });

  it("세션 쿠키가 없으면 401을 반환한다", async () => {
    const response = await app.inject({
      method: "GET",
      url: "/protected",
    });

    expect(response.statusCode).toBe(401);
    expect(response.json()).toMatchObject({
      success: false,
      code: "UNAUTHORIZED",
    });
  });

  it("유효하지 않은 세션이면 401을 반환한다", async () => {
    const response = await app.inject({
      method: "GET",
      url: "/protected",
      headers: {
        cookie:
          "pmis_session=123e4567-e89b-42d3-a456-426614174001",
      },
    });

    expect(response.statusCode).toBe(401);
    expect(response.json()).toMatchObject({
      success: false,
      code: "UNAUTHORIZED",
    });
  });

  it("유효한 세션이면 Backend Access Token을 요청에 전달한다", async () => {
    await sessionStore.set(session);

    const response = await app.inject({
      method: "GET",
      url: "/protected",
      headers: {
        cookie: `pmis_session=${session.sessionId}`,
      },
    });

    expect(response.statusCode).toBe(200);
    expect(response.json()).toEqual({
      accessToken: session.accessToken,
    });
  });

  it("만료된 세션이면 401을 반환한다", async () => {
    const expiredSession: BffSession = {
      ...session,
      expiresAt: Date.now() - 1000,
    };

    await sessionStore.set(expiredSession);

    const response = await app.inject({
      method: "GET",
      url: "/protected",
      headers: {
        cookie: `pmis_session=${expiredSession.sessionId}`,
      },
    });

    expect(response.statusCode).toBe(401);
    expect(response.json()).toMatchObject({
      success: false,
      code: "UNAUTHORIZED",
    });
  });

  it("세션 저장소 오류가 발생하면 500을 반환한다", async () => {
    vi.spyOn(sessionStore, "get").mockRejectedValue(
      new Error("session store unavailable"),
    );

    const response = await app.inject({
      method: "GET",
      url: "/protected",
      headers: {
        cookie: `pmis_session=${session.sessionId}`,
      },
    });

    expect(response.statusCode).toBe(500);
    expect(response.json()).toMatchObject({
      success: false,
      code: "INTERNAL_SERVER_ERROR",
    });
  });
});