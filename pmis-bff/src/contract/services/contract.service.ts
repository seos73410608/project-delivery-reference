import { backendApiClient } from "../../common/backend/backend-api.client.js";
import type { BackendApiAuthContext } from "../../common/backend/backend-api.types.js";
import type {
  ChangeContractStatusQuery,
  ContractResponse,
  CreateContractRequest,
  PageContractResponse,
  SearchContractsQuery,
  UpdateContractRequest,
} from "../types/contract.types.js";

export class ContractService {
  async createContract(
    accessToken: string,
    request: CreateContractRequest,
    authContext?: BackendApiAuthContext,
  ): Promise<ContractResponse | null> {
    return backendApiClient.request<ContractResponse>({
      method: "POST",
      path: "/api/contracts",
      accessToken,
      ...(authContext ? { authContext } : {}),
      body: request,
    });
  }

  async searchContracts(
    accessToken: string,
    query: SearchContractsQuery,
    authContext?: BackendApiAuthContext,
  ): Promise<PageContractResponse | null> {
    return backendApiClient.request<PageContractResponse>({
      method: "GET",
      path: "/api/contracts",
      accessToken,
      ...(authContext ? { authContext } : {}),
      query: { ...query },
    });
  }

  async getContract(
    accessToken: string,
    contractId: string,
    authContext?: BackendApiAuthContext,
  ): Promise<ContractResponse | null> {
    return backendApiClient.request<ContractResponse>({
      method: "GET",
      path: `/api/contracts/${contractId}`,
      accessToken,
      ...(authContext ? { authContext } : {}),
    });
  }

  async updateContract(
    accessToken: string,
    contractId: string,
    request: UpdateContractRequest,
    authContext?: BackendApiAuthContext,
  ): Promise<ContractResponse | null> {
    return backendApiClient.request<ContractResponse>({
      method: "PUT",
      path: `/api/contracts/${contractId}`,
      accessToken,
      ...(authContext ? { authContext } : {}),
      body: request,
    });
  }

  async changeContractStatus(
    accessToken: string,
    contractId: string,
    query: ChangeContractStatusQuery,
    authContext?: BackendApiAuthContext,
  ): Promise<ContractResponse | null> {
    return backendApiClient.request<ContractResponse>({
      method: "PATCH",
      path: `/api/contracts/${contractId}/status`,
      accessToken,
      ...(authContext ? { authContext } : {}),
      query: { ...query },
    });
  }
}

export const contractService = new ContractService();