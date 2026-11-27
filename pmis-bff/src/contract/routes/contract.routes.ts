import type {
  FastifyInstance,
  FastifyReply,
  FastifyRequest,
} from "fastify";
import { BackendApiError } from "../../common/backend/backend-api.client.js";
import { requireSession } from "../../common/auth/require-session.js";
import { contractService } from "../services/contract.service.js";
import {
  changeContractStatusQuerySchema,
  contractIdParamsSchema,
  createContractBodySchema,
  searchContractsQuerySchema,
  updateContractBodySchema,
} from "../schemas/contract.schema.js";
import type {
  ChangeContractStatusQuery,
  CreateContractRequest,
  SearchContractsQuery,
  UpdateContractRequest,
} from "../types/contract.types.js";

interface ContractIdParams {
  contractId: string;
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

export default async function contractRoutes(
  app: FastifyInstance,
): Promise<void> {
  app.post<{ Body: CreateContractRequest }>(
    "/",
    {
      preHandler: requireSession,
      schema: {
        body: createContractBodySchema,
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
        const data = await contractService.createContract(
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

  app.get<{ Querystring: SearchContractsQuery }>(
    "/",
    {
      preHandler: requireSession,
      schema: {
        querystring: searchContractsQuerySchema,
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
        const data = await contractService.searchContracts(
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

  app.get<{ Params: ContractIdParams }>(
    "/:contractId",
    {
      preHandler: requireSession,
      schema: {
        params: contractIdParamsSchema,
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
        const data = await contractService.getContract(
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

  app.put<{
    Params: ContractIdParams;
    Body: UpdateContractRequest;
  }>(
    "/:contractId",
    {
      preHandler: requireSession,
      schema: {
        params: contractIdParamsSchema,
        body: updateContractBodySchema,
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
        const data = await contractService.updateContract(
          accessToken,
          request.params.contractId,
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
    Params: ContractIdParams;
    Querystring: ChangeContractStatusQuery;
  }>(
    "/:contractId/status",
    {
      preHandler: requireSession,
      schema: {
        params: contractIdParamsSchema,
        querystring: changeContractStatusQuerySchema,
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
        const data = await contractService.changeContractStatus(
          accessToken,
          request.params.contractId,
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