
import type { FastifyPluginAsync, FastifyReply } from "fastify";
import { AuthServiceError } from "../services/auth.service.js";
import { BackendApiError } from "../services/backend-auth.service.js";
import {
  getSessionCookieOptions,
  SESSION_COOKIE_CLEAR_OPTIONS,
  SESSION_COOKIE_NAME,
} from "../session/session-cookie.js";

interface LoginBody {
  username: string;
  password: string;
}

interface SessionSelectionBody {
  loginAttemptId: string;
  sessionIds: string[];
}

function sendAuthError(
  error: unknown,
  reply: FastifyReply,
) {
  if (error instanceof AuthServiceError || error instanceof BackendApiError) {
    return reply.status(error.statusCode).send({
      success: false,
      code: error.code,
      message: error.message,
    });
  }

  return reply.status(500).send({
    success: false,
    code: "INTERNAL_SERVER_ERROR",
    message: "인증 처리 중 오류가 발생했습니다.",
  });
}

const authRoutes: FastifyPluginAsync = async (app) => {
  // 일반 로그인
  app.post<{ Body: LoginBody }>(
    "/login",
    {
      schema: {
        body: {
          type: "object",
          required: ["username", "password"],
          additionalProperties: false,
          properties: {
            username: { type: "string", minLength: 1 },
            password: { type: "string", minLength: 1 },
          },
        },
      },
    },
    async (request, reply) => {
      try {
        const result = await app.authService.login(request.body);

        if (result.status === "SESSION_SELECTION_REQUIRED") {
          return reply.send({
            success: true,
            code: "SESSION_SELECTION_REQUIRED",
            message: "로그인할 세션을 선택해야 합니다.",
            data: {
              status: result.status,
              loginAttemptId: result.loginAttemptId,
              requiredTerminationCount: result.requiredTerminationCount,
              activeSessions: result.activeSessions,
            },
          });
        }

        const session = await app.sessionStore.get(result.sessionId);

        if (!session) {
          return reply.status(500).send({
            success: false,
            code: "SESSION_CREATION_FAILED",
            message: "로그인 세션을 생성하지 못했습니다.",
          });
        }

        return reply
          .setCookie(
            SESSION_COOKIE_NAME,
            result.sessionId,
            getSessionCookieOptions(session.expiresAt),
          )
          .send({
            success: true,
            code: "SUCCESS",
            message: "로그인에 성공했습니다.",
            data: {
              user: result.user,
            },
          });
      } catch (error) {
        return sendAuthError(error, reply);
      }
    },
  );

  // Refresh Token 갱신
  app.post("/refresh", async (request, reply) => {
    const sessionId = request.cookies[SESSION_COOKIE_NAME];

    if (!sessionId) {
      return reply.status(401).send({
        success: false,
        code: "UNAUTHORIZED",
        message: "인증이 필요합니다.",
      });
    }

    try {
      const session = await app.authService.refresh(sessionId);

      return reply
        .setCookie(
          SESSION_COOKIE_NAME,
          session.sessionId,
          getSessionCookieOptions(session.expiresAt),
        )
        .send({
          success: true,
          code: "SUCCESS",
          message: "토큰 갱신에 성공했습니다.",
        });
    } catch (error) {
      const shouldInvalidateSession =
        (error instanceof AuthServiceError &&
          error.code === "UNAUTHORIZED") ||
        (error instanceof BackendApiError &&
          ["AUTH_003", "AUTH_004", "AUTH_009"].includes(error.code));

      if (shouldInvalidateSession) {
        try {
          await app.sessionStore.delete(sessionId);
        } catch {
          // 세션 저장소 정리 실패와 관계없이 브라우저 쿠키는 만료시킨다.
        }

        reply.clearCookie(
          SESSION_COOKIE_NAME,
          SESSION_COOKIE_CLEAR_OPTIONS,
        );
      }

      return sendAuthError(error, reply);
    }
  });

  // 현재 로그인 사용자 조회
  app.get("/me", async (request, reply) => {
    const sessionId = request.cookies[SESSION_COOKIE_NAME];

    if (!sessionId) {
      return reply.status(401).send({
        success: false,
        code: "UNAUTHORIZED",
        message: "인증이 필요합니다.",
      });
    }

    try {
      const session = await app.sessionStore.get(sessionId);

      if (!session) {
        return reply.status(401).send({
          success: false,
          code: "UNAUTHORIZED",
          message: "세션이 유효하지 않거나 만료되었습니다.",
        });
      }

      return reply.send({
        success: true,
        code: "SUCCESS",
        message: "사용자 정보 조회에 성공했습니다.",
        data: session.user,
      });
    } catch {
      return reply.status(500).send({
        success: false,
        code: "INTERNAL_SERVER_ERROR",
        message: "인증 상태 조회 중 오류가 발생했습니다.",
      });
    }
  });

  // 다중 세션 선택 로그인
  app.post<{ Body: SessionSelectionBody }>(
    "/login/session-selection",
    {
      schema: {
        body: {
          type: "object",
          required: ["loginAttemptId", "sessionIds"],
          additionalProperties: false,
          properties: {
            loginAttemptId: { type: "string", minLength: 1 },
            sessionIds: {
              type: "array",
              minItems: 1,
              uniqueItems: true,
              items: {
                type: "string",
                pattern:
                  "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
              },
            },
          },
        },
      },
    },
    async (request, reply) => {
      try {
        const result =
          await app.authService.completeSessionSelection(request.body);

        if (result.status === "SESSION_SELECTION_REQUIRED") {
          return reply.send({
            success: true,
            code: "SESSION_SELECTION_REQUIRED",
            message: "추가로 종료할 세션을 선택해야 합니다.",
            data: {
              status: result.status,
              loginAttemptId: result.loginAttemptId,
              requiredTerminationCount: result.requiredTerminationCount,
              activeSessions: result.activeSessions,
            },
          });
        }

        const session = await app.sessionStore.get(result.sessionId);

        if (!session) {
          return reply.status(500).send({
            success: false,
            code: "SESSION_CREATION_FAILED",
            message: "로그인 세션을 생성하지 못했습니다.",
          });
        }

        return reply
          .setCookie(
            SESSION_COOKIE_NAME,
            result.sessionId,
            getSessionCookieOptions(session.expiresAt),
          )
          .send({
            success: true,
            code: "SUCCESS",
            message: "세션 선택 및 로그인이 완료되었습니다.",
            data: {
              user: result.user,
            },
          });
      } catch (error) {
        return sendAuthError(error, reply);
      }
    },
  );
};

export default authRoutes;
