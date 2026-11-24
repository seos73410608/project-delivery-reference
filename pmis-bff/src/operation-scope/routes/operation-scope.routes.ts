
import type {
  FastifyInstance,
  FastifyReply,
  FastifyRequest,
} from "fastify";
import { BackendApiError } from "../../common/backend/backend-api.client.js";
import { requireSession } from "../../common/auth/require-session.js";
import { operationScopeService } from "../services/operation-scope.service.js";
import {
  changeOperationScopeStatusBodySchema,
  createOperationScopeBodySchema,
  operationScopeIdParamsSchema,
  operationScopeOperationIdParamsSchema,
  operationScopeOperationalScopeIdParamsSchema,
  updateOperationScopeBodySchema,
} from "../schemas/operation-scope.schema.js";
import type {
  ChangeOperationScopeStatusRequest,
  CreateOperationScopeRequest,
  UpdateOperationScopeRequest,
} from "../types/operation-scope.types.js";

interface OperationScopeIdParams {
  id: string;
}

interface OperationScopeOperationIdParams {
  operationId: string;
}

interface OperationScopeOperationalScopeIdParams {
  operationalScopeId: string;
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

export default async function operationScopeRoutes(
  app: FastifyInstance,
): Promise<void> {
  app.post<{ Body: CreateOperationScopeRequest }>(
    "/",
    {
      preHandler: requireSession,
      schema: {
        body: createOperationScopeBodySchema,
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
        const data = await operationScopeService.createOperationScope(
          accessToken,
          request.body,
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

  app.get<{ Params: OperationScopeOperationIdParams }>(
    "/by-operation/:operationId",
    {
      preHandler: requireSession,
      schema: {
        params: operationScopeOperationIdParamsSchema,
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
          await operationScopeService.findOperationScopesByOperation(
            accessToken,
            request.params.operationId,
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

  app.get<{ Params: OperationScopeOperationalScopeIdParams }>(
    "/by-operational-scope/:operationalScopeId",
    {
      preHandler: requireSession,
      schema: {
        params: operationScopeOperationalScopeIdParamsSchema,
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
          await operationScopeService.findOperationScopesByOperationalScope(
            accessToken,
            request.params.operationalScopeId,
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

  app.get<{ Params: OperationScopeIdParams }>(
    "/:id",
    {
      preHandler: requireSession,
      schema: {
        params: operationScopeIdParamsSchema,
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
        const data = await operationScopeService.getOperationScope(
          accessToken,
          request.params.id,
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

  app.put<{
    Params: OperationScopeIdParams;
    Body: UpdateOperationScopeRequest;
  }>(
    "/:id",
    {
      preHandler: requireSession,
      schema: {
        params: operationScopeIdParamsSchema,
        body: updateOperationScopeBodySchema,
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
        const data = await operationScopeService.updateOperationScope(
          accessToken,
          request.params.id,
          request.body,
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

  app.patch<{
    Params: OperationScopeIdParams;
    Body: ChangeOperationScopeStatusRequest;
  }>(
    "/:id/status",
    {
      preHandler: requireSession,
      schema: {
        params: operationScopeIdParamsSchema,
        body: changeOperationScopeStatusBodySchema,
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
        const data = await operationScopeService.changeOperationScopeStatus(
          accessToken,
          request.params.id,
          request.body,
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
