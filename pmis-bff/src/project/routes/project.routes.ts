import type {
  FastifyInstance,
  FastifyReply,
  FastifyRequest,
} from "fastify";
import { BackendApiError } from "../../common/backend/backend-api.client.js";
import { requireSession } from "../../common/auth/require-session.js";
import { projectService } from "../services/project.service.js";
import {
  createProjectBodySchema,
  projectIdParamsSchema,
  searchProjectsQuerySchema,
  updateProjectBodySchema,
} from "../schemas/project.schema.js";
import type {
  CreateProjectRequest,
  SearchProjectsQuery,
  UpdateProjectRequest,
} from "../types/project.types.js";

interface ProjectIdParams {
  projectId: string;
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

  requestLogError(reply, error);

  return reply.status(500).send({
    success: false,
    code: "INTERNAL_SERVER_ERROR",
    message: "요청 처리 중 오류가 발생했습니다.",
  });
}

function requestLogError(
  reply: FastifyReply,
  error: unknown,
): void {
  reply.log.error(error);
}

export default async function projectRoutes(
  app: FastifyInstance,
): Promise<void> {
  app.post<{ Body: CreateProjectRequest }>(
    "/",
    {
      preHandler: requireSession,
      schema: {
        body: createProjectBodySchema,
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
        const data = await projectService.createProject(
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

  app.get<{ Querystring: SearchProjectsQuery }>(
    "/",
    {
      preHandler: requireSession,
      schema: {
        querystring: searchProjectsQuerySchema,
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
        const data = await projectService.searchProjects(
          accessToken,
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

  app.put<{
    Params: ProjectIdParams;
    Body: UpdateProjectRequest;
  }>(
    "/:projectId",
    {
      preHandler: requireSession,
      schema: {
        params: projectIdParamsSchema,
        body: updateProjectBodySchema,
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
        const data = await projectService.updateProject(
          accessToken,
          request.params.projectId,
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

  app.delete<{ Params: ProjectIdParams }>(
    "/:projectId",
    {
      preHandler: requireSession,
      schema: {
        params: projectIdParamsSchema,
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
        const data = await projectService.deleteProject(
          accessToken,
          request.params.projectId,
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