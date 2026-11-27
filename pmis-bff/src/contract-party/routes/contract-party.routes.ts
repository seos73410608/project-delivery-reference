import type {
  FastifyInstance,
  FastifyReply,
  FastifyRequest,
} from "fastify";
import { BackendApiError } from "../../common/backend/backend-api.client.js";
import { requireSession } from "../../common/auth/require-session.js";
import { contractPartyService } from "../services/contract-party.service.js";
import {
  changeContractPartyPrimaryQuerySchema,
  closeContractPartyQuerySchema,
  companyContractPartyParamsSchema,
  contractPartyItemParamsSchema,
  contractPartyParamsSchema,
  createContractPartyBodySchema,
} from "../schemas/contract-party.schema.js";
import type {
  ChangeContractPartyPrimaryQuery,
  CloseContractPartyQuery,
  CreateContractPartyRequest,
} from "../types/contract-party.types.js";

interface ContractPartyParams {
  contractId: string;
}

interface CompanyContractPartyParams {
  companyId: string;
}

interface ContractPartyItemParams {
  contractId: string;
  partyId: string;
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

export default async function contractPartyRoutes(
  app: FastifyInstance,
): Promise<void> {
  app.get<{ Params: ContractPartyParams }>(
    "/api/contracts/:contractId/parties",
    {
      preHandler: requireSession,
      schema: {
        params: contractPartyParamsSchema,
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
        const data = await contractPartyService.getContractParties(
          accessToken,
          request.params.contractId,
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

  app.get<{ Params: CompanyContractPartyParams }>(
    "/api/companies/:companyId/contract-parties",
    {
      preHandler: requireSession,
      schema: {
        params: companyContractPartyParamsSchema,
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
        const data = await contractPartyService.getCompanyContractParties(
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
    Params: ContractPartyParams;
    Body: CreateContractPartyRequest;
  }>(
    "/api/contracts/:contractId/parties",
    {
      preHandler: requireSession,
      schema: {
        params: contractPartyParamsSchema,
        body: createContractPartyBodySchema,
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
        const data = await contractPartyService.addContractParty(
          accessToken,
          request.params.contractId,
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
    Params: ContractPartyItemParams;
    Querystring: CloseContractPartyQuery;
  }>(
    "/api/contracts/:contractId/parties/:partyId/close",
    {
      preHandler: requireSession,
      schema: {
        params: contractPartyItemParamsSchema,
        querystring: closeContractPartyQuerySchema,
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
        const data = await contractPartyService.closeContractParty(
          accessToken,
          request.params.contractId,
          request.params.partyId,
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

  app.patch<{
    Params: ContractPartyItemParams;
    Querystring: ChangeContractPartyPrimaryQuery;
  }>(
    "/api/contracts/:contractId/parties/:partyId/primary",
    {
      preHandler: requireSession,
      schema: {
        params: contractPartyItemParamsSchema,
        querystring: changeContractPartyPrimaryQuerySchema,
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
          await contractPartyService.changeContractPartyPrimary(
            accessToken,
            request.params.contractId,
            request.params.partyId,
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