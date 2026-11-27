import type {
  FastifyInstance,
  FastifyReply,
  FastifyRequest,
} from "fastify";
import { BackendApiError } from "../../common/backend/backend-api.client.js";
import { requireSession } from "../../common/auth/require-session.js";
import { companyService } from "../services/company.service.js";
import {
  changeCompanyStatusQuerySchema,
  closeCompanyRoleAssignmentBodySchema,
  companyIdParamsSchema,
  companyRoleAssignmentParamsSchema,
  createCompanyBodySchema,
  createCompanyRoleAssignmentBodySchema,
  searchCompaniesQuerySchema,
  updateCompanyBodySchema,
} from "../schemas/company.schema.js";
import type {
  ChangeCompanyStatusQuery,
  CloseCompanyRoleAssignmentRequest,
  CreateCompanyRequest,
  CreateCompanyRoleAssignmentRequest,
  SearchCompaniesQuery,
  UpdateCompanyRequest,
} from "../types/company.types.js";

interface CompanyIdParams {
  companyId: string;
}

interface CompanyRoleAssignmentParams {
  companyId: string;
  assignmentId: string;
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

export default async function companyRoutes(
  app: FastifyInstance,
): Promise<void> {
  app.post<{ Body: CreateCompanyRequest }>(
    "/",
    {
      preHandler: requireSession,
      schema: {
        body: createCompanyBodySchema,
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
        const data = await companyService.createCompany(
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

  app.get<{ Querystring: SearchCompaniesQuery }>(
    "/",
    {
      preHandler: requireSession,
      schema: {
        querystring: searchCompaniesQuerySchema,
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
        const data = await companyService.searchCompanies(
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

  app.get<{ Params: CompanyIdParams }>(
    "/:companyId",
    {
      preHandler: requireSession,
      schema: {
        params: companyIdParamsSchema,
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
        const data = await companyService.getCompany(
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

  app.put<{
    Params: CompanyIdParams;
    Body: UpdateCompanyRequest;
  }>(
    "/:companyId",
    {
      preHandler: requireSession,
      schema: {
        params: companyIdParamsSchema,
        body: updateCompanyBodySchema,
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
        const data = await companyService.updateCompany(
          accessToken,
          request.params.companyId,
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
    Params: CompanyIdParams;
    Querystring: ChangeCompanyStatusQuery;
  }>(
    "/:companyId/status",
    {
      preHandler: requireSession,
      schema: {
        params: companyIdParamsSchema,
        querystring: changeCompanyStatusQuerySchema,
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
        const data = await companyService.changeCompanyStatus(
          accessToken,
          request.params.companyId,
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

  app.get<{ Params: CompanyIdParams }>(
    "/:companyId/roles",
    {
      preHandler: requireSession,
      schema: {
        params: companyIdParamsSchema,
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
        const data = await companyService.getCompanyRoles(
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

  app.post<{
    Params: CompanyIdParams;
    Body: CreateCompanyRoleAssignmentRequest;
  }>(
    "/:companyId/roles",
    {
      preHandler: requireSession,
      schema: {
        params: companyIdParamsSchema,
        body: createCompanyRoleAssignmentBodySchema,
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
        const data = await companyService.createCompanyRoleAssignment(
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

  app.patch<{
    Params: CompanyRoleAssignmentParams;
    Body: CloseCompanyRoleAssignmentRequest;
  }>(
    "/:companyId/roles/:assignmentId/close",
    {
      preHandler: requireSession,
      schema: {
        params: companyRoleAssignmentParamsSchema,
        body: closeCompanyRoleAssignmentBodySchema,
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
        const data = await companyService.closeCompanyRoleAssignment(
          accessToken,
          request.params.companyId,
          request.params.assignmentId,
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