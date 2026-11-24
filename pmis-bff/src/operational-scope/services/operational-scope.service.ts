
import { backendApiClient } from "../../common/backend/backend-api.client.js";
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
  ): Promise<OperationalScopeResponse | null> {
    return backendApiClient.request<OperationalScopeResponse>({
      method: "POST",
      path: "/api/operational-scopes",
      accessToken,
      body: request,
    });
  }

  async searchOperationalScopes(
    accessToken: string,
    query: SearchOperationalScopesQuery,
  ): Promise<PageOperationalScopeResponse | null> {
    return backendApiClient.request<PageOperationalScopeResponse>({
      method: "GET",
      path: "/api/operational-scopes",
      accessToken,
      query: { ...query },
    });
  }

  async getOperationalScope(
    accessToken: string,
    id: string,
  ): Promise<OperationalScopeResponse | null> {
    return backendApiClient.request<OperationalScopeResponse>({
      method: "GET",
      path: `/api/operational-scopes/${id}`,
      accessToken,
    });
  }

  async updateOperationalScope(
    accessToken: string,
    id: string,
    request: UpdateOperationalScopeRequest,
  ): Promise<OperationalScopeResponse | null> {
    return backendApiClient.request<OperationalScopeResponse>({
      method: "PUT",
      path: `/api/operational-scopes/${id}`,
      accessToken,
      body: request,
    });
  }
}

export const operationalScopeService = new OperationalScopeService();
