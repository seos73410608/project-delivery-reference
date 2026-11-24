
import { backendApiClient } from "../../common/backend/backend-api.client.js";
import type { BackendApiAuthContext } from "../../common/backend/backend-api.types.js";
import type {
  CreateOperationalScopeRequest,
  OperationalScopeResponse,
  PageOperationalScopeResponse,
  SearchOperationalScopesQuery,
  UpdateOperationalScopeRequest,
} from "../types/operational-scope.types.js";

export class OperationalScopeService {
  async createOperationalScope(
    accessToken: string,
    request: CreateOperationalScopeRequest,
    authContext?: BackendApiAuthContext,
  ): Promise<OperationalScopeResponse | null> {
    return backendApiClient.request<OperationalScopeResponse>({
      method: "POST",
      path: "/api/operational-scopes",
      accessToken,
      body: request,
      ...(authContext ? { authContext } : {}),
    });
  }

  async searchOperationalScopes(
    accessToken: string,
    query: SearchOperationalScopesQuery,
    authContext?: BackendApiAuthContext,
  ): Promise<PageOperationalScopeResponse | null> {
    return backendApiClient.request<PageOperationalScopeResponse>({
      method: "GET",
      path: "/api/operational-scopes",
      accessToken,
      query: { ...query },
      ...(authContext ? { authContext } : {}),
    });
  }

  async getOperationalScope(
    accessToken: string,
    id: string,
    authContext?: BackendApiAuthContext,
  ): Promise<OperationalScopeResponse | null> {
    return backendApiClient.request<OperationalScopeResponse>({
      method: "GET",
      path: `/api/operational-scopes/${id}`,
      accessToken,
      ...(authContext ? { authContext } : {}),
    });
  }

  async updateOperationalScope(
    accessToken: string,
    id: string,
    request: UpdateOperationalScopeRequest,
    authContext?: BackendApiAuthContext,
  ): Promise<OperationalScopeResponse | null> {
    return backendApiClient.request<OperationalScopeResponse>({
      method: "PUT",
      path: `/api/operational-scopes/${id}`,
      accessToken,
      body: request,
      ...(authContext ? { authContext } : {}),
    });
  }
}

export const operationalScopeService = new OperationalScopeService();
