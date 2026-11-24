import { backendApiClient } from "../../common/backend/backend-api.client.js";
import type { BackendApiAuthContext } from "../../common/backend/backend-api.types.js";
import type {
  ChangeOperationScopeStatusRequest,
  CreateOperationScopeRequest,
  OperationScopeResponse,
  UpdateOperationScopeRequest,
} from "../types/operation-scope.types.js";

export class OperationScopeService {
  async createOperationScope(
    accessToken: string,
    request: CreateOperationScopeRequest,
    authContext?: BackendApiAuthContext,
  ): Promise<OperationScopeResponse | null> {
    return backendApiClient.request<OperationScopeResponse>({
      method: "POST",
      path: "/api/operation-scopes",
      accessToken,
      body: request,
      ...(authContext ? { authContext } : {}),
    });
  }

  async getOperationScope(
    accessToken: string,
    id: string,
    authContext?: BackendApiAuthContext,
  ): Promise<OperationScopeResponse | null> {
    return backendApiClient.request<OperationScopeResponse>({
      method: "GET",
      path: `/api/operation-scopes/${id}`,
      accessToken,
      ...(authContext ? { authContext } : {}),
    });
  }

  async findOperationScopesByOperation(
    accessToken: string,
    operationId: string,
    authContext?: BackendApiAuthContext,
  ): Promise<OperationScopeResponse[] | null> {
    return backendApiClient.request<OperationScopeResponse[]>({
      method: "GET",
      path: `/api/operation-scopes/by-operation/${operationId}`,
      accessToken,
      ...(authContext ? { authContext } : {}),
    });
  }

  async findOperationScopesByOperationalScope(
    accessToken: string,
    operationalScopeId: string,
    authContext?: BackendApiAuthContext,
  ): Promise<OperationScopeResponse[] | null> {
    return backendApiClient.request<OperationScopeResponse[]>({
      method: "GET",
      path: `/api/operation-scopes/by-operational-scope/${operationalScopeId}`,
      accessToken,
      ...(authContext ? { authContext } : {}),
    });
  }

  async updateOperationScope(
    accessToken: string,
    id: string,
    request: UpdateOperationScopeRequest,
    authContext?: BackendApiAuthContext,
  ): Promise<OperationScopeResponse | null> {
    return backendApiClient.request<OperationScopeResponse>({
      method: "PUT",
      path: `/api/operation-scopes/${id}`,
      accessToken,
      body: request,
      ...(authContext ? { authContext } : {}),
    });
  }

  async changeOperationScopeStatus(
    accessToken: string,
    id: string,
    request: ChangeOperationScopeStatusRequest,
    authContext?: BackendApiAuthContext,
  ): Promise<OperationScopeResponse | null> {
    return backendApiClient.request<OperationScopeResponse>({
      method: "PATCH",
      path: `/api/operation-scopes/${id}/status`,
      accessToken,
      body: request,
      ...(authContext ? { authContext } : {}),
    });
  }
}

export const operationScopeService = new OperationScopeService();