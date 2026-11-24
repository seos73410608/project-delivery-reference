
import { backendApiClient } from "../../common/backend/backend-api.client.js";
import type { BackendApiAuthContext } from "../../common/backend/backend-api.types.js";
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
    authContext?: BackendApiAuthContext,
  ): Promise<ProjectOperationResponse | null> {
    return backendApiClient.request<ProjectOperationResponse>({
      method: "POST",
      path: "/api/project-operations",
      accessToken,
      body: request,
      ...(authContext ? { authContext } : {}),
    });
  }

  async getProjectOperation(
    accessToken: string,
    id: string,
    authContext?: BackendApiAuthContext,
  ): Promise<ProjectOperationResponse | null> {
    return backendApiClient.request<ProjectOperationResponse>({
      method: "GET",
      path: `/api/project-operations/${id}`,
      accessToken,
      ...(authContext ? { authContext } : {}),
    });
  }

  async updateProjectOperation(
    accessToken: string,
    id: string,
    request: UpdateProjectOperationRequest,
    authContext?: BackendApiAuthContext,
  ): Promise<ProjectOperationResponse | null> {
    return backendApiClient.request<ProjectOperationResponse>({
      method: "PUT",
      path: `/api/project-operations/${id}`,
      accessToken,
      body: request,
      ...(authContext ? { authContext } : {}),
    });
  }

  async changeProjectOperationStatus(
    accessToken: string,
    id: string,
    request: ChangeProjectOperationStatusRequest,
    authContext?: BackendApiAuthContext,
  ): Promise<ProjectOperationResponse | null> {
    return backendApiClient.request<ProjectOperationResponse>({
      method: "PATCH",
      path: `/api/project-operations/${id}/status`,
      accessToken,
      body: request,
      ...(authContext ? { authContext } : {}),
    });
  }

  async findProjectOperationsByProject(
    accessToken: string,
    projectId: string,
    authContext?: BackendApiAuthContext,
  ): Promise<ProjectOperationResponse[] | null> {
    return backendApiClient.request<ProjectOperationResponse[]>({
      method: "GET",
      path: `/api/project-operations/by-project/${projectId}`,
      accessToken,
      ...(authContext ? { authContext } : {}),
    });
  }

  async findProjectOperationsByOperation(
    accessToken: string,
    operationId: string,
    authContext?: BackendApiAuthContext,
  ): Promise<ProjectOperationResponse[] | null> {
    return backendApiClient.request<ProjectOperationResponse[]>({
      method: "GET",
      path: `/api/project-operations/by-operation/${operationId}`,
      accessToken,
      ...(authContext ? { authContext } : {}),
    });
  }
}

export const projectOperationService = new ProjectOperationService();
