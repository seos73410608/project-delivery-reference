
import type {
  FastifyInstance,
  FastifyReply,
  FastifyRequest,
} from "fastify";
import { BackendApiError } from "../../common/backend/backend-api.client.js";
import { requireSession } from "../../common/auth/require-session.js";
import { operationalScopeCiService } from "../services/operational-scope-ci.service.js";
import {
  operationalScopeCiParamsSchema,
  operationalScopeCiScopeParamsSchema,
} from "../schemas/operational-scope-ci.schema.js";

interface OperationalScopeCiScopeParams {
  scopeId: string;
}

interface OperationalScopeCiParams {
  scopeId: string;
  ciId: string;
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

export default async function operationalScopeCiRoutes(
  app: FastifyInstance,
): Promise<void> {
  app.post<{ Params: OperationalScopeCiParams }>(
    "/:scopeId/cis/:ciId",
    {
      preHandler: requireSession,
      schema: {
        params: operationalScopeCiParamsSchema,
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
        const data = await operationalScopeCiService.connect(
          accessToken,
          request.params.scopeId,
          request.params.ciId,
          request.backendAuthContext ?? undefined,
        );

        return reply.status(201).send({
          success: true,
          code: "SUCCESS",
          data,
        });
      } catch (error) {
        return sendBackendError(reply, error);
      }
    },
  );

  app.get<{ Params: OperationalScopeCiScopeParams }>(
    "/:scopeId/cis",
    {
      preHandler: requireSession,
      schema: {
        params: operationalScopeCiScopeParamsSchema,
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
        const data = await operationalScopeCiService.getAll(
          accessToken,
          request.params.scopeId,
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

  app.get<{ Params: OperationalScopeCiParams }>(
    "/:scopeId/cis/:ciId",
    {
      preHandler: requireSession,
      schema: {
        params: operationalScopeCiParamsSchema,
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
        const data = await operationalScopeCiService.getDetail(
          accessToken,
          request.params.scopeId,
          request.params.ciId,
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

  app.delete<{ Params: OperationalScopeCiParams }>(
    "/:scopeId/cis/:ciId",
    {
      preHandler: requireSession,
      schema: {
        params: operationalScopeCiParamsSchema,
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
        const message = await operationalScopeCiService.disconnect(
          accessToken,
          request.params.scopeId,
          request.params.ciId,
          request.backendAuthContext ?? undefined,
        );

        return reply.send({
          success: true,
          code: "SUCCESS",
          message,
        });
      } catch (error) {
        return sendBackendError(reply, error);
      }
    },
  );
}
