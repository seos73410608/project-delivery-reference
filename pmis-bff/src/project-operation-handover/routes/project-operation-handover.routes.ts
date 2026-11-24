import type {
  FastifyInstance,
  FastifyReply,
  FastifyRequest,
} from "fastify";
import { BackendApiError } from "../../common/backend/backend-api.client.js";
import { requireSession } from "../../common/auth/require-session.js";
import { projectOperationHandoverService } from "../services/project-operation-handover.service.js";
import {
  projectOperationHandoverIdSchema,
  projectOperationHandoverInitialCreateSchema,
  projectOperationHandoverOperatorChangeCreateSchema,
  projectOperationHandoverProjectOperationIdSchema,
  projectOperationHandoverReHandoverCreateSchema,
} from "../schemas/project-operation-handover.schema.js";
import type {
  ProjectOperationHandoverInitialCreateRequest,
  ProjectOperationHandoverOperatorChangeCreateRequest,
  ProjectOperationHandoverReHandoverCreateRequest,
} from "../types/project-operation-handover.types.js";

interface ProjectOperationHandoverIdParams {
  id: string;
}

interface ProjectOperationHandoverProjectOperationIdParams {
  projectOperationId: string;
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

export default async function projectOperationHandoverRoutes(
  app: FastifyInstance,
): Promise<void> {
  app.post<{
    Body: ProjectOperationHandoverInitialCreateRequest;
  }>(
    "/initial",
    {
      preHandler: requireSession,
      schema: projectOperationHandoverInitialCreateSchema,
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
        const data = await projectOperationHandoverService.createInitial(
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

  app.post<{
    Body: ProjectOperationHandoverOperatorChangeCreateRequest;
  }>(
    "/operator-change",
    {
      preHandler: requireSession,
      schema: projectOperationHandoverOperatorChangeCreateSchema,
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
          await projectOperationHandoverService.createOperatorChange(
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

  app.post<{
    Body: ProjectOperationHandoverReHandoverCreateRequest;
  }>(
    "/re-handover",
    {
      preHandler: requireSession,
      schema: projectOperationHandoverReHandoverCreateSchema,
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
        const data = await projectOperationHandoverService.createReHandover(
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

  app.get<{
    Params: ProjectOperationHandoverIdParams;
  }>(
    "/:id",
    {
      preHandler: requireSession,
      schema: projectOperationHandoverIdSchema,
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
        const data = await projectOperationHandoverService.getById(
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

  app.get<{
    Params: ProjectOperationHandoverProjectOperationIdParams;
  }>(
    "/by-project-operation/:projectOperationId",
    {
      preHandler: requireSession,
      schema: projectOperationHandoverProjectOperationIdSchema,
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
          await projectOperationHandoverService.getByProjectOperation(
            accessToken,
            request.params.projectOperationId,
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
    Params: ProjectOperationHandoverIdParams;
  }>(
    "/:id/request",
    {
      preHandler: requireSession,
      schema: projectOperationHandoverIdSchema,
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
        const data = await projectOperationHandoverService.request(
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

  app.patch<{
    Params: ProjectOperationHandoverIdParams;
  }>(
    "/:id/start",
    {
      preHandler: requireSession,
      schema: projectOperationHandoverIdSchema,
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
        const data = await projectOperationHandoverService.start(
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

  app.patch<{
    Params: ProjectOperationHandoverIdParams;
  }>(
    "/:id/submit-for-review",
    {
      preHandler: requireSession,
      schema: projectOperationHandoverIdSchema,
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
          await projectOperationHandoverService.submitForReview(
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

  app.patch<{
    Params: ProjectOperationHandoverIdParams;
  }>(
    "/:id/accept",
    {
      preHandler: requireSession,
      schema: projectOperationHandoverIdSchema,
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
        const data = await projectOperationHandoverService.accept(
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

  app.patch<{
    Params: ProjectOperationHandoverIdParams;
  }>(
    "/:id/complete",
    {
      preHandler: requireSession,
      schema: projectOperationHandoverIdSchema,
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
        const data = await projectOperationHandoverService.complete(
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

  app.patch<{
    Params: ProjectOperationHandoverIdParams;
  }>(
    "/:id/reject",
    {
      preHandler: requireSession,
      schema: projectOperationHandoverIdSchema,
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
        const data = await projectOperationHandoverService.reject(
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

  app.patch<{
    Params: ProjectOperationHandoverIdParams;
  }>(
    "/:id/cancel",
    {
      preHandler: requireSession,
      schema: projectOperationHandoverIdSchema,
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
        const data = await projectOperationHandoverService.cancel(
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
}