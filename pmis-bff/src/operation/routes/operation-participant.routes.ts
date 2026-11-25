import type {
  FastifyInstance,
  FastifyReply,
  FastifyRequest,
} from "fastify";
import { BackendApiError } from "../../common/backend/backend-api.client.js";
import { requireSession } from "../../common/auth/require-session.js";
import { operationParticipantService } from "../services/operation-participant.service.js";
import {
  operationParticipantOperationIdParamsSchema,
  operationParticipantsQuerySchema,
} from "../schemas/operation-participant.schema.js";
import type {
  OperationParticipantsQuery,
} from "../types/operation.types.js";

interface OperationParticipantOperationIdParams {
  operationId: string;
}

function getAccessToken(request: FastifyRequest): string | null {
  return request.backendAccessToken;
}

function sendBackendError(
  reply: FastifyReply,
  error: unknown,
): FastifyReply {
  if (error instanceof BackendApiError) {
    return reply.status(error.statusCode).send({
      success: false,
      code: error.code,
      message: error.message,
    });
  }

  reply.log.error(error);

  return reply.status(500).send({
    success: false,
    code: "INTERNAL_SERVER_ERROR",
    message: "요청 처리 중 오류가 발생했습니다.",
  });
}

export default async function operationParticipantRoutes(
  app: FastifyInstance,
): Promise<void> {
  app.get<{
    Params: OperationParticipantOperationIdParams;
    Querystring: OperationParticipantsQuery;
  }>(
    "/:operationId/participants",
    {
      preHandler: requireSession,
      schema: {
        params: operationParticipantOperationIdParamsSchema,
        querystring: operationParticipantsQuerySchema,
      },
    },
    async (request, reply) => {
      const accessToken = getAccessToken(request);

      if (!accessToken) {
        return reply.status(401).send({
          success: false,
          code: "UNAUTHORIZED",
          message: "인증이 필요합니다.",
        });
      }

      try {
        const data =
          await operationParticipantService.findHandoverEligibleParticipants(
            accessToken,
            request.params.operationId,
            request.query,
            request.backendAuthContext ?? undefined,
          );

        return reply.send({
          success: true,
          code: "SUCCESS",
          data,
        });
      } catch (error) {
        return sendBackendError(reply, error);
      }
    },
  );
}