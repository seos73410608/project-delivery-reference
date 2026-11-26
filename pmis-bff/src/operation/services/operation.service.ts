
import { backendApiClient } from "../../common/backend/backend-api.client.js";
import type { BackendApiAuthContext } from "../../common/backend/backend-api.types.js";
import type {
  CreateOperationRequest,
  OperationResponse,
  UpdateOperationRequest,
  UpdateOperationStatusRequest,
} from "../types/operation.types.js";

export class OperationService {
  async getOperations(
    accessToken: string,
    authContext?: BackendApiAuthContext,
  ): Promise<OperationResponse[] | null> {
    return backendApiClient.request<OperationResponse[]>({
      method: "GET",
      path: "/api/operations",
      accessToken,
      ...(authContext ? { authContext } : {}),
    });
  }

  async getOperation(
    accessToken: string,
    operationId: string,
    authContext?: BackendApiAuthContext,
  ): Promise<OperationResponse | null> {
    return backendApiClient.request<OperationResponse>({
      method: "GET",
      path: `/api/operations/${operationId}`,
      accessToken,
      ...(authContext ? { authContext } : {}),
    });
  }

  async getOperationByCode(
    accessToken: string,
    operationCode: string,
    authContext?: BackendApiAuthContext,
  ): Promise<OperationResponse | null> {
    return backendApiClient.request<OperationResponse>({
      method: "GET",
      path: `/api/operations/by-code/${encodeURIComponent(operationCode)}`,
      accessToken,
      ...(authContext ? { authContext } : {}),
    });
  }

  async createOperation(
    accessToken: string,
    request: CreateOperationRequest,
    authContext?: BackendApiAuthContext,
  ): Promise<OperationResponse | null> {
    return backendApiClient.request<OperationResponse>({
      method: "POST",
      path: "/api/operations",
      accessToken,
      ...(authContext ? { authContext } : {}),
      body: request,
    });
  }

  async updateOperation(
    accessToken: string,
    operationId: string,
    request: UpdateOperationRequest,
    authContext?: BackendApiAuthContext,
  ): Promise<OperationResponse | null> {
    return backendApiClient.request<OperationResponse>({
      method: "PUT",
      path: `/api/operations/${operationId}`,
      accessToken,
      ...(authContext ? { authContext } : {}),
      body: request,
    });
  }

  async updateOperationStatus(
    accessToken: string,
    operationId: string,
    request: UpdateOperationStatusRequest,
    authContext?: BackendApiAuthContext,
  ): Promise<OperationResponse | null> {
    return backendApiClient.request<OperationResponse>({
      method: "PATCH",
      path: `/api/operations/${operationId}/status`,
      accessToken,
      ...(authContext ? { authContext } : {}),
      body: request,
    });
  }
}

export const operationService = new OperationService();
