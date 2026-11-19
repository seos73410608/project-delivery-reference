
import type { FastifyPluginAsync, FastifyReply } from "fastify";
import { AuthServiceError } from "../services/auth.service.js";
import { BackendApiError } from "../services/backend-auth.service.js";
import {
  getSessionCookieOptions,
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
