import { backendApiClient } from "../../common/backend/backend-api.client.js";
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
  ): Promise<OperationScopeResponse | null> {
    return backendApiClient.request<OperationScopeResponse>({
      method: "POST",
      path: "/api/operation-scopes",
      accessToken,
      body: request,
    });
  }

  async getOperationScope(
    accessToken: string,
    id: string,
  ): Promise<OperationScopeResponse | null> {
    return backendApiClient.request<OperationScopeResponse>({
      method: "GET",
      path: `/api/operation-scopes/${id}`,
      accessToken,
    });
  }

  async findOperationScopesByOperation(
    accessToken: string,
    operationId: string,
  ): Promise<OperationScopeResponse[] | null> {
    return backendApiClient.request<OperationScopeResponse[]>({
      method: "GET",
      path: `/api/operation-scopes/by-operation/${operationId}`,
      accessToken,
    });
  }

  async findOperationScopesByOperationalScope(
    accessToken: string,
    operationalScopeId: string,
  ): Promise<OperationScopeResponse[] | null> {
    return backendApiClient.request<OperationScopeResponse[]>({
      method: "GET",
      path: `/api/operation-scopes/by-operational-scope/${operationalScopeId}`,
      accessToken,
    });
  }

  async updateOperationScope(
    accessToken: string,
    id: string,
    request: UpdateOperationScopeRequest,
  ): Promise<OperationScopeResponse | null> {
    return backendApiClient.request<OperationScopeResponse>({
      method: "PUT",
      path: `/api/operation-scopes/${id}`,
      accessToken,
      body: request,
    });
  }

  async changeOperationScopeStatus(
    accessToken: string,
    id: string,
    request: ChangeOperationScopeStatusRequest,
  ): Promise<OperationScopeResponse | null> {
    return backendApiClient.request<OperationScopeResponse>({
      method: "PATCH",
      path: `/api/operation-scopes/${id}/status`,
      accessToken,
      body: request,
    });
  }
}

export const operationScopeService = new OperationScopeService();