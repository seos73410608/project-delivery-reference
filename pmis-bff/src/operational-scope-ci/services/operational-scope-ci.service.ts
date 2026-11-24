
import { backendApiClient } from "../../common/backend/backend-api.client.js";
import type {
  OperationalScopeCiResponse,
} from "../types/operational-scope-ci.types.js";

export class OperationalScopeCiService {
  async connect(
    accessToken: string,
    scopeId: string,
    ciId: string,
  ): Promise<OperationalScopeCiResponse | null> {
    return backendApiClient.request<OperationalScopeCiResponse>({
      method: "POST",
      path: `/api/operational-scopes/${scopeId}/cis/${ciId}`,
      accessToken,
    });
  }

  async getAll(
    accessToken: string,
    scopeId: string,
  ): Promise<OperationalScopeCiResponse[] | null> {
    return backendApiClient.request<OperationalScopeCiResponse[]>({
      method: "GET",
      path: `/api/operational-scopes/${scopeId}/cis`,
      accessToken,
    });
  }

  async getDetail(
    accessToken: string,
    scopeId: string,
    ciId: string,
  ): Promise<OperationalScopeCiResponse | null> {
    return backendApiClient.request<OperationalScopeCiResponse>({
      method: "GET",
      path: `/api/operational-scopes/${scopeId}/cis/${ciId}`,
      accessToken,
    });
  }

  async disconnect(
    accessToken: string,
    scopeId: string,
    ciId: string,
  ): Promise<string | null> {
    return backendApiClient.request<string>({
      method: "DELETE",
      path: `/api/operational-scopes/${scopeId}/cis/${ciId}`,
      accessToken,
    });
  }
}

export const operationalScopeCiService =
  new OperationalScopeCiService();
