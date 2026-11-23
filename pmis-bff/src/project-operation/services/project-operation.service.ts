import { backendApiClient } from "../../common/backend/backend-api.client.js";
import type {
  ChangeProjectOperationStatusRequest,
  CreateProjectOperationRequest,
  ProjectOperationResponse,
  UpdateProjectOperationRequest,
} from "../types/project-operation.types.js";

export class ProjectOperationService {
  async createProjectOperation(
    accessToken: string,
    request: CreateProjectOperationRequest,
  ): Promise<ProjectOperationResponse | null> {
    return backendApiClient.request<ProjectOperationResponse>({
      method: "POST",
      path: "/api/project-operations",
      accessToken,
      body: request,
    });
  }

  async getProjectOperation(
    accessToken: string,
    id: string,
  ): Promise<ProjectOperationResponse | null> {
    return backendApiClient.request<ProjectOperationResponse>({
      method: "GET",
      path: `/api/project-operations/${id}`,
      accessToken,
    });
  }

  async updateProjectOperation(
    accessToken: string,
    id: string,
    request: UpdateProjectOperationRequest,
  ): Promise<ProjectOperationResponse | null> {
    return backendApiClient.request<ProjectOperationResponse>({
      method: "PUT",
      path: `/api/project-operations/${id}`,
      accessToken,
      body: request,
    });
  }

  async changeProjectOperationStatus(
    accessToken: string,
    id: string,
    request: ChangeProjectOperationStatusRequest,
  ): Promise<ProjectOperationResponse | null> {
    return backendApiClient.request<ProjectOperationResponse>({
      method: "PATCH",
      path: `/api/project-operations/${id}/status`,
      accessToken,
      body: request,
    });
  }

  async findProjectOperationsByProject(
    accessToken: string,
    projectId: string,
  ): Promise<ProjectOperationResponse[] | null> {
    return backendApiClient.request<ProjectOperationResponse[]>({
      method: "GET",
      path: `/api/project-operations/by-project/${projectId}`,
      accessToken,
    });
  }

  async findProjectOperationsByOperation(
    accessToken: string,
    operationId: string,
  ): Promise<ProjectOperationResponse[] | null> {
    return backendApiClient.request<ProjectOperationResponse[]>({
      method: "GET",
      path: `/api/project-operations/by-operation/${operationId}`,
      accessToken,
    });
  }
}

export const projectOperationService = new ProjectOperationService();