
import type {
  FastifyInstance,
  FastifyReply,
  FastifyRequest,
} from "fastify";
import { BackendApiError } from "../../common/backend/backend-api.client.js";
import { requireSession } from "../../common/auth/require-session.js";
import { operationService } from "../services/operation.service.js";
import {
  createOperationBodySchema,
  operationCodeParamsSchema,
  operationIdParamsSchema,
  updateOperationBodySchema,
  updateOperationStatusBodySchema,
} from "../schemas/operation.schema.js";
import type {
  CreateOperationRequest,
  OperationResponse,
  UpdateOperationRequest,
  UpdateOperationStatusRequest,
} from "../types/operation.types.js";

interface OperationIdParams {
  operationId: string;
}

interface OperationCodeParams {
  operationCode: string;
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

function sendUnauthorized(reply: FastifyReply): FastifyReply {
  return reply.status(401).send({
    success: false,
    code: "UNAUTHORIZED",
    message: "인증이 필요합니다.",
  });
}

export default async function operationRoutes(
  app: FastifyInstance,
): Promise<void> {
  app.post<{ Body: CreateOperationRequest }>(
    "/",
    {
      preHandler: requireSession,
      schema: {
        body: createOperationBodySchema,
      },
    },
    async (request, reply) => {
      const accessToken = getAccessToken(request);

      if (!accessToken) {
        return sendUnauthorized(reply);
      }

      try {
        const data = await operationService.createOperation(
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

  app.get<{ Querystring: Record<string, never> }>(
    "/",
    {
      preHandler: requireSession,
    },
    async (request, reply) => {
      const accessToken = getAccessToken(request);

      if (!accessToken) {
        return sendUnauthorized(reply);
      }

      try {
        const data = await operationService.getOperations(
          accessToken,
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

  app.get<{ Params: OperationCodeParams }>(
    "/by-code/:operationCode",
    {
      preHandler: requireSession,
      schema: {
        params: operationCodeParamsSchema,
      },
    },
    async (request, reply) => {
      const accessToken = getAccessToken(request);

      if (!accessToken) {
        return sendUnauthorized(reply);
      }

      try {
        const data = await operationService.getOperationByCode(
          accessToken,
          request.params.operationCode,
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

  app.get<{ Params: OperationIdParams }>(
    "/:operationId",
    {
      preHandler: requireSession,
      schema: {
        params: operationIdParamsSchema,
      },
    },
    async (request, reply) => {
      const accessToken = getAccessToken(request);

      if (!accessToken) {
        return sendUnauthorized(reply);
      }

      try {
        const data = await operationService.getOperation(
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

  app.put<{
    Params: OperationIdParams;
    Body: UpdateOperationRequest;
  }>(
    "/:operationId",
    {
      preHandler: requireSession,
      schema: {
        params: operationIdParamsSchema,
        body: updateOperationBodySchema,
      },
    },
    async (request, reply) => {
      const accessToken = getAccessToken(request);

      if (!accessToken) {
        return sendUnauthorized(reply);
      }

      try {
        const data = await operationService.updateOperation(
          accessToken,
          request.params.operationId,
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
    Params: OperationIdParams;
    Body: UpdateOperationStatusRequest;
  }>(
    "/:operationId/status",
    {
      preHandler: requireSession,
      schema: {
        params: operationIdParamsSchema,
        body: updateOperationStatusBodySchema,
      },
    },
    async (request, reply) => {
      const accessToken = getAccessToken(request);

      if (!accessToken) {
        return sendUnauthorized(reply);
      }

      try {
        const data = await operationService.updateOperationStatus(
          accessToken,
          request.params.operationId,
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
