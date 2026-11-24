
import { backendApiClient } from "../../common/backend/backend-api.client.js";
import type { BackendApiAuthContext } from "../../common/backend/backend-api.types.js";
import type {
  OperationalScopeCiResponse,
} from "../types/operational-scope-ci.types.js";

export class OperationalScopeCiService {
  async connect(
    accessToken: string,
    scopeId: string,
    ciId: string,
    authContext?: BackendApiAuthContext,
  ): Promise<OperationalScopeCiResponse | null> {
    return backendApiClient.request<OperationalScopeCiResponse>({
      method: "POST",
      path: `/api/operational-scopes/${scopeId}/cis/${ciId}`,
      accessToken,
      ...(authContext ? { authContext } : {}),
    });
  }

  async getAll(
    accessToken: string,
    scopeId: string,
    authContext?: BackendApiAuthContext,
  ): Promise<OperationalScopeCiResponse[] | null> {
    return backendApiClient.request<OperationalScopeCiResponse[]>({
      method: "GET",
      path: `/api/operational-scopes/${scopeId}/cis`,
      accessToken,
      ...(authContext ? { authContext } : {}),
    });
  }

  async getDetail(
    accessToken: string,
    scopeId: string,
    ciId: string,
    authContext?: BackendApiAuthContext,
  ): Promise<OperationalScopeCiResponse | null> {
    return backendApiClient.request<OperationalScopeCiResponse>({
      method: "GET",
      path: `/api/operational-scopes/${scopeId}/cis/${ciId}`,
      accessToken,
      ...(authContext ? { authContext } : {}),
    });
  }

  async disconnect(
    accessToken: string,
    scopeId: string,
    ciId: string,
    authContext?: BackendApiAuthContext,
  ): Promise<string | null> {
    return backendApiClient.request<string>({
      method: "DELETE",
      path: `/api/operational-scopes/${scopeId}/cis/${ciId}`,
      accessToken,
      ...(authContext ? { authContext } : {}),
    });
  }
}

export const operationalScopeCiService =
  new OperationalScopeCiService();
