
import type {
  FastifyInstance,
  FastifyReply,
  FastifyRequest,
} from "fastify";
import { BackendApiError } from "../../common/backend/backend-api.client.js";
import { requireSession } from "../../common/auth/require-session.js";
import { operationalScopeService } from "../services/operational-scope.service.js";
import {
  createOperationalScopeBodySchema,
  operationalScopeIdParamsSchema,
  searchOperationalScopesQuerySchema,
  updateOperationalScopeBodySchema,
} from "../schemas/operational-scope.schema.js";
import type {
  CreateOperationalScopeRequest,
  SearchOperationalScopesQuery,
  UpdateOperationalScopeRequest,
} from "../types/operational-scope.types.js";

interface OperationalScopeIdParams {
  id: string;
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

export default async function operationalScopeRoutes(
  app: FastifyInstance,
): Promise<void> {
  app.post<{ Body: CreateOperationalScopeRequest }>(
    "/",
    {
      preHandler: requireSession,
      schema: {
        body: createOperationalScopeBodySchema,
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
        const data = await operationalScopeService.createOperationalScope(
          accessToken,
          request.body,
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

  app.get<{ Querystring: SearchOperationalScopesQuery }>(
    "/",
    {
      preHandler: requireSession,
      schema: {
        querystring: searchOperationalScopesQuerySchema,
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
        const data = await operationalScopeService.searchOperationalScopes(
          accessToken,
          request.query,
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

  app.get<{ Params: OperationalScopeIdParams }>(
    "/:id",
    {
      preHandler: requireSession,
      schema: {
        params: operationalScopeIdParamsSchema,
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
        const data = await operationalScopeService.getOperationalScope(
          accessToken,
          request.params.id,
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

  app.put<{
    Params: OperationalScopeIdParams;
    Body: UpdateOperationalScopeRequest;
  }>(
    "/:id",
    {
      preHandler: requireSession,
      schema: {
        params: operationalScopeIdParamsSchema,
        body: updateOperationalScopeBodySchema,
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
        const data = await operationalScopeService.updateOperationalScope(
          accessToken,
          request.params.id,
          request.body,
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
