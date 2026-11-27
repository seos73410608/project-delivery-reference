import { backendApiClient } from "../../common/backend/backend-api.client.js";
import type { BackendApiAuthContext } from "../../common/backend/backend-api.types.js";
import type {
  ChangeContractPartyPrimaryQuery,
  CloseContractPartyQuery,
  ContractPartyResponse,
  CreateContractPartyRequest,
} from "../types/contract-party.types.js";

export class ContractPartyService {
  async getContractParties(
    accessToken: string,
    contractId: string,
    authContext?: BackendApiAuthContext,
  ): Promise<ContractPartyResponse[] | null> {
    return backendApiClient.request<ContractPartyResponse[]>({
      method: "GET",
      path: `/api/contracts/${contractId}/parties`,
      accessToken,
      ...(authContext ? { authContext } : {}),
    });
  }

  async getCompanyContractParties(
    accessToken: string,
    companyId: string,
    authContext?: BackendApiAuthContext,
  ): Promise<ContractPartyResponse[] | null> {
    return backendApiClient.request<ContractPartyResponse[]>({
      method: "GET",
      path: `/api/companies/${companyId}/contract-parties`,
      accessToken,
      ...(authContext ? { authContext } : {}),
    });
  }

  async addContractParty(
    accessToken: string,
    contractId: string,
    request: CreateContractPartyRequest,
    authContext?: BackendApiAuthContext,
  ): Promise<ContractPartyResponse | null> {
    return backendApiClient.request<ContractPartyResponse>({
      method: "POST",
      path: `/api/contracts/${contractId}/parties`,
      accessToken,
      ...(authContext ? { authContext } : {}),
      body: request,
    });
  }

  async closeContractParty(
    accessToken: string,
    contractId: string,
    partyId: string,
    query: CloseContractPartyQuery,
    authContext?: BackendApiAuthContext,
  ): Promise<ContractPartyResponse | null> {
    return backendApiClient.request<ContractPartyResponse>({
      method: "PATCH",
      path: `/api/contracts/${contractId}/parties/${partyId}/close`,
      accessToken,
      ...(authContext ? { authContext } : {}),
      query: { ...query },
    });
  }

  async changeContractPartyPrimary(
    accessToken: string,
    contractId: string,
    partyId: string,
    query: ChangeContractPartyPrimaryQuery,
    authContext?: BackendApiAuthContext,
  ): Promise<ContractPartyResponse | null> {
    return backendApiClient.request<ContractPartyResponse>({
      method: "PATCH",
      path: `/api/contracts/${contractId}/parties/${partyId}/primary`,
      accessToken,
      ...(authContext ? { authContext } : {}),
      query: { ...query },
    });
  }
}

export const contractPartyService = new ContractPartyService();