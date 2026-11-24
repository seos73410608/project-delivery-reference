import { backendApiClient } from "../../common/backend/backend-api.client.js";
import type { BackendApiAuthContext } from "../../common/backend/backend-api.types.js";
import type {
  ProjectOperationHandoverInitialCreateRequest,
  ProjectOperationHandoverOperatorChangeCreateRequest,
  ProjectOperationHandoverReHandoverCreateRequest,
  ProjectOperationHandoverResponse,
  ProjectOperationHandoverHistoryResponse,
} from "../types/project-operation-handover.types.js";

const BASE_PATH = "/api/project-operation-handovers";

export class ProjectOperationHandoverService {
  async createInitial(
    accessToken: string,
    request: ProjectOperationHandoverInitialCreateRequest,
    authContext?: BackendApiAuthContext,
  ): Promise<ProjectOperationHandoverResponse | null> {
    return backendApiClient.request<ProjectOperationHandoverResponse>({
      method: "POST",
      path: `${BASE_PATH}/initial`,
      accessToken,
      ...(authContext ? { authContext } : {}),
      body: request,
    });
  }

  async createOperatorChange(
    accessToken: string,
    request: ProjectOperationHandoverOperatorChangeCreateRequest,
    authContext?: BackendApiAuthContext,
  ): Promise<ProjectOperationHandoverResponse | null> {
    return backendApiClient.request<ProjectOperationHandoverResponse>({
      method: "POST",
      path: `${BASE_PATH}/operator-change`,
      accessToken,
      ...(authContext ? { authContext } : {}),
      body: request,
    });
  }

  async createReHandover(
    accessToken: string,
    request: ProjectOperationHandoverReHandoverCreateRequest,
    authContext?: BackendApiAuthContext,
  ): Promise<ProjectOperationHandoverResponse | null> {
    return backendApiClient.request<ProjectOperationHandoverResponse>({
      method: "POST",
      path: `${BASE_PATH}/re-handover`,
      accessToken,
      ...(authContext ? { authContext } : {}),
      body: request,
    });
  }

  async getById(
    accessToken: string,
    id: string,
    authContext?: BackendApiAuthContext,
  ): Promise<ProjectOperationHandoverResponse | null> {
    return backendApiClient.request<ProjectOperationHandoverResponse>({
      method: "GET",
      path: `${BASE_PATH}/${id}`,
      accessToken,
      ...(authContext ? { authContext } : {}),
    });
  }

  async getByProjectOperation(
    accessToken: string,
    projectOperationId: string,
    authContext?: BackendApiAuthContext,
  ): Promise<ProjectOperationHandoverHistoryResponse | null> {
    return backendApiClient.request<ProjectOperationHandoverHistoryResponse>({
      method: "GET",
      path: `${BASE_PATH}/by-project-operation/${projectOperationId}`,
      accessToken,
      ...(authContext ? { authContext } : {}),
    });
  }

  async request(
    accessToken: string,
    id: string,
    authContext?: BackendApiAuthContext,
  ): Promise<ProjectOperationHandoverResponse | null> {
    return this.patchStatus(accessToken, id, "request", authContext);
  }

  async start(
    accessToken: string,
    id: string,
    authContext?: BackendApiAuthContext,
  ): Promise<ProjectOperationHandoverResponse | null> {
    return this.patchStatus(accessToken, id, "start", authContext);
  }

  async submitForReview(
    accessToken: string,
    id: string,
    authContext?: BackendApiAuthContext,
  ): Promise<ProjectOperationHandoverResponse | null> {
    return this.patchStatus(accessToken, id, "submit-for-review", authContext);
  }

  async accept(
    accessToken: string,
    id: string,
    authContext?: BackendApiAuthContext,
  ): Promise<ProjectOperationHandoverResponse | null> {
    return this.patchStatus(accessToken, id, "accept", authContext);
  }

  async complete(
    accessToken: string,
    id: string,
    authContext?: BackendApiAuthContext,
  ): Promise<ProjectOperationHandoverResponse | null> {
    return this.patchStatus(accessToken, id, "complete", authContext);
  }

  async reject(
    accessToken: string,
    id: string,
    authContext?: BackendApiAuthContext,
  ): Promise<ProjectOperationHandoverResponse | null> {
    return this.patchStatus(accessToken, id, "reject", authContext);
  }

  async cancel(
    accessToken: string,
    id: string,
    authContext?: BackendApiAuthContext,
  ): Promise<ProjectOperationHandoverResponse | null> {
    return this.patchStatus(accessToken, id, "cancel", authContext);
  }

  private async patchStatus(
    accessToken: string,
    id: string,
    action: string,
    authContext?: BackendApiAuthContext,
  ): Promise<ProjectOperationHandoverResponse | null> {
    return backendApiClient.request<ProjectOperationHandoverResponse>({
      method: "PATCH",
      path: `${BASE_PATH}/${id}/${action}`,
      accessToken,
      ...(authContext ? { authContext } : {}),
    });
  }
}

export const projectOperationHandoverService =
  new ProjectOperationHandoverService();