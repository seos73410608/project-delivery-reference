import type {
  FastifyReply,
  FastifyRequest,
  preHandlerHookHandler,
} from "fastify";
import type { BackendApiAuthContext } from "../backend/backend-api.types.js";
import { SESSION_COOKIE_NAME } from "../../auth/session/session-cookie.js";

declare module "fastify" {
  interface FastifyRequest {
    backendAccessToken: string | null;
    backendSessionId: string | null;
    backendAuthContext: BackendApiAuthContext | null;
  }
}

export const requireSession: preHandlerHookHandler = async (
  request: FastifyRequest,
  reply: FastifyReply,
): Promise<void> => {
  const sessionId = request.cookies[SESSION_COOKIE_NAME];

  if (!sessionId) {
    reply.status(401).send({
      success: false,
      code: "UNAUTHORIZED",
      message: "인증이 필요합니다.",
    });
    return;
  }

  try {
    const session = await request.server.sessionStore.get(sessionId);

    if (!session) {
      reply.status(401).send({
        success: false,
        code: "UNAUTHORIZED",
        message: "세션이 유효하지 않거나 만료되었습니다.",
      });
      return;
    }

    request.backendAccessToken = session.accessToken;
    request.backendSessionId = sessionId;
    request.backendAuthContext = {
      refreshAccessToken: async (rejectedAccessToken: string) => {
        const refreshedSession =
          await request.server.authService.refreshAccessToken(
            sessionId,
            rejectedAccessToken,
          );

        return refreshedSession.accessToken;
      },
    };
  } catch {
    reply.status(500).send({
      success: false,
      code: "INTERNAL_SERVER_ERROR",
      message: "인증 상태 조회 중 오류가 발생했습니다.",
    });
  }
};