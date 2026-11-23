
import { backendApiClient } from "../../common/backend/backend-api.client.js";
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
  ): Promise<ProjectOperationHandoverResponse | null> {
    return backendApiClient.request<ProjectOperationHandoverResponse>({
      method: "POST",
      path: `${BASE_PATH}/initial`,
      accessToken,
      body: request,
    });
  }

  async createOperatorChange(
    accessToken: string,
    request: ProjectOperationHandoverOperatorChangeCreateRequest,
  ): Promise<ProjectOperationHandoverResponse | null> {
    return backendApiClient.request<ProjectOperationHandoverResponse>({
      method: "POST",
      path: `${BASE_PATH}/operator-change`,
      accessToken,
      body: request,
    });
  }

  async createReHandover(
    accessToken: string,
    request: ProjectOperationHandoverReHandoverCreateRequest,
  ): Promise<ProjectOperationHandoverResponse | null> {
    return backendApiClient.request<ProjectOperationHandoverResponse>({
      method: "POST",
      path: `${BASE_PATH}/re-handover`,
      accessToken,
      body: request,
    });
  }

  async getById(
    accessToken: string,
    id: string,
  ): Promise<ProjectOperationHandoverResponse | null> {
    return backendApiClient.request<ProjectOperationHandoverResponse>({
      method: "GET",
      path: `${BASE_PATH}/${id}`,
      accessToken,
    });
  }

  async getByProjectOperation(
    accessToken: string,
    projectOperationId: string,
  ): Promise<ProjectOperationHandoverHistoryResponse | null> {
    return backendApiClient.request<ProjectOperationHandoverHistoryResponse>({
      method: "GET",
      path: `${BASE_PATH}/by-project-operation/${projectOperationId}`,
      accessToken,
    });
  }

  async request(
    accessToken: string,
    id: string,
  ): Promise<ProjectOperationHandoverResponse | null> {
    return this.patchStatus(accessToken, id, "request");
  }

  async start(
    accessToken: string,
    id: string,
  ): Promise<ProjectOperationHandoverResponse | null> {
    return this.patchStatus(accessToken, id, "start");
  }

  async submitForReview(
    accessToken: string,
    id: string,
  ): Promise<ProjectOperationHandoverResponse | null> {
    return this.patchStatus(accessToken, id, "submit-for-review");
  }

  async accept(
    accessToken: string,
    id: string,
  ): Promise<ProjectOperationHandoverResponse | null> {
    return this.patchStatus(accessToken, id, "accept");
  }

  async complete(
    accessToken: string,
    id: string,
  ): Promise<ProjectOperationHandoverResponse | null> {
    return this.patchStatus(accessToken, id, "complete");
  }

  async reject(
    accessToken: string,
    id: string,
  ): Promise<ProjectOperationHandoverResponse | null> {
    return this.patchStatus(accessToken, id, "reject");
  }

  async cancel(
    accessToken: string,
    id: string,
  ): Promise<ProjectOperationHandoverResponse | null> {
    return this.patchStatus(accessToken, id, "cancel");
  }

  private async patchStatus(
    accessToken: string,
    id: string,
    action: string,
  ): Promise<ProjectOperationHandoverResponse | null> {
    return backendApiClient.request<ProjectOperationHandoverResponse>({
      method: "PATCH",
      path: `${BASE_PATH}/${id}/${action}`,
      accessToken,
    });
  }
}

export const projectOperationHandoverService =
  new ProjectOperationHandoverService();
