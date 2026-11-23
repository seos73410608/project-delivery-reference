
import type {
  FastifyInstance,
} from "fastify";
import { BackendApiError } from "../../common/backend/backend-api.client.js";
import { requireSession } from "../../common/auth/require-session.js";
import { projectService } from "../services/project.service.js";
import { projectIdParamsSchema } from "../schemas/project.schema.js";
import type { ProjectDetailResponse } from "../types/project.types.js";

interface ProjectIdParams {
  projectId: string;
}

export default async function projectDetailRoutes(
  app: FastifyInstance,
): Promise<void> {
  app.get<{ Params: ProjectIdParams }>(
    "/:projectId/detail",
    {
      preHandler: requireSession,
      schema: {
        params: projectIdParamsSchema,
      },
    },
    async (request, reply) => {
      const accessToken = request.backendAccessToken;

      if (!accessToken) {
        return reply.status(401).send({
          success: false,
          code: "UNAUTHORIZED",
          message: "인증이 필요합니다.",
        });
      }

      try {
        const data: ProjectDetailResponse | null =
          await projectService.getProjectDetail(
            accessToken,
            request.params.projectId,
          );

        return reply.send({
          success: true,
          code: "SUCCESS",
          data,
        });
      } catch (error) {
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
    },
  );
}
