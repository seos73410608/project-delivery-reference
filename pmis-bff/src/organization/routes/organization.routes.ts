
import type {
  FastifyInstance,
  FastifyReply,
  FastifyRequest,
} from "fastify";
import { BackendApiError } from "../../common/backend/backend-api.client.js";
import { requireSession } from "../../common/auth/require-session.js";
import { organizationService } from "../services/organization.service.js";
import {
  changeOrganizationStatusQuerySchema,
  companyOrganizationParamsSchema,
  createOrganizationBodySchema,
  organizationParamsSchema,
  updateOrganizationBodySchema,
} from "../schemas/organization.schema.js";
import type {
  ChangeOrganizationStatusQuery,
  CreateOrganizationRequest,
  UpdateOrganizationRequest,
} from "../types/organization.types.js";

interface CompanyOrganizationParams {
  companyId: string;
}

interface OrganizationParams {
  companyId: string;
  organizationId: string;
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

export default async function organizationRoutes(
  app: FastifyInstance,
): Promise<void> {
  // 회사별 조직 목록 조회
  app.get<{ Params: CompanyOrganizationParams }>(
    "/:companyId/organizations",
    {
      preHandler: requireSession,
      schema: {
        params: companyOrganizationParamsSchema,
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
        const data = await organizationService.getOrganizations(
          accessToken,
          request.params.companyId,
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

  // 조직 상세 조회
  app.get<{ Params: OrganizationParams }>(
    "/:companyId/organizations/:organizationId",
    {
      preHandler: requireSession,
      schema: {
        params: organizationParamsSchema,
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
        const data = await organizationService.getOrganization(
          accessToken,
          request.params.companyId,
          request.params.organizationId,
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

  // 조직 생성
  app.post<{
    Params: CompanyOrganizationParams;
    Body: CreateOrganizationRequest;
  }>(
    "/:companyId/organizations",
    {
      preHandler: requireSession,
      schema: {
        params: companyOrganizationParamsSchema,
        body: createOrganizationBodySchema,
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
        const data = await organizationService.createOrganization(
          accessToken,
          request.params.companyId,
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

  // 조직 수정
  app.put<{
    Params: OrganizationParams;
    Body: UpdateOrganizationRequest;
  }>(
    "/:companyId/organizations/:organizationId",
    {
      preHandler: requireSession,
      schema: {
        params: organizationParamsSchema,
        body: updateOrganizationBodySchema,
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
        const data = await organizationService.updateOrganization(
          accessToken,
          request.params.companyId,
          request.params.organizationId,
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

  // 조직 상태 변경
  app.patch<{
    Params: OrganizationParams;
    Querystring: ChangeOrganizationStatusQuery;
  }>(
    "/:companyId/organizations/:organizationId/status",
    {
      preHandler: requireSession,
      schema: {
        params: organizationParamsSchema,
        querystring: changeOrganizationStatusQuerySchema,
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
        const data = await organizationService.changeOrganizationStatus(
          accessToken,
          request.params.companyId,
          request.params.organizationId,
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
