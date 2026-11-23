import type {
  FastifyInstance,
  FastifyReply,
  FastifyRequest,
} from "fastify";
import { BackendApiError } from "../../common/backend/backend-api.client.js";
import { requireSession } from "../../common/auth/require-session.js";
import { projectOperationService } from "../services/project-operation.service.js";
import {
  changeProjectOperationStatusBodySchema,
  createProjectOperationBodySchema,
  projectOperationIdParamsSchema,
  projectOperationOperationIdParamsSchema,
  projectOperationProjectIdParamsSchema,
  updateProjectOperationBodySchema,
} from "../schemas/project-operation.schema.js";
import type {
  ChangeProjectOperationStatusRequest,
  CreateProjectOperationRequest,
  UpdateProjectOperationRequest,
} from "../types/project-operation.types.js";

interface ProjectOperationIdParams {
  id: string;
}

interface ProjectOperationProjectIdParams {
  projectId: string;
}

interface ProjectOperationOperationIdParams {
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

export default async function projectOperationRoutes(
  app: FastifyInstance,
): Promise<void> {
  app.post<{ Body: CreateProjectOperationRequest }>(
    "/",
    {
      preHandler: requireSession,
      schema: {
        body: createProjectOperationBodySchema,
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
        const data = await projectOperationService.createProjectOperation(
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

  app.get<{ Params: ProjectOperationIdParams }>(
    "/:id",
    {
      preHandler: requireSession,
      schema: {
        params: projectOperationIdParamsSchema,
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
        const data = await projectOperationService.getProjectOperation(
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
    Params: ProjectOperationIdParams;
    Body: UpdateProjectOperationRequest;
  }>(
    "/:id",
    {
      preHandler: requireSession,
      schema: {
        params: projectOperationIdParamsSchema,
        body: updateProjectOperationBodySchema,
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
        const data = await projectOperationService.updateProjectOperation(
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

  app.patch<{
    Params: ProjectOperationIdParams;
    Body: ChangeProjectOperationStatusRequest;
  }>(
    "/:id/status",
    {
      preHandler: requireSession,
      schema: {
        params: projectOperationIdParamsSchema,
        body: changeProjectOperationStatusBodySchema,
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
          await projectOperationService.changeProjectOperationStatus(
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

  app.get<{ Params: ProjectOperationProjectIdParams }>(
    "/by-project/:projectId",
    {
      preHandler: requireSession,
      schema: {
        params: projectOperationProjectIdParamsSchema,
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
          await projectOperationService.findProjectOperationsByProject(
            accessToken,
            request.params.projectId,
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

  app.get<{ Params: ProjectOperationOperationIdParams }>(
    "/by-operation/:operationId",
    {
      preHandler: requireSession,
      schema: {
        params: projectOperationOperationIdParamsSchema,
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
          await projectOperationService.findProjectOperationsByOperation(
            accessToken,
            request.params.operationId,
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