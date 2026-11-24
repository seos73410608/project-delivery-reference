import { backendApiClient } from "../../common/backend/backend-api.client.js";
import type { BackendApiAuthContext } from "../../common/backend/backend-api.types.js";
import type {
  CreateProjectRequest,
  PageProjectResponse,
  ProjectDetailResponse,
  ProjectResponse,
  SearchProjectsQuery,
  UpdateProjectRequest,
} from "../types/project.types.js";

export class ProjectService {
  async createProject(
    accessToken: string,
    request: CreateProjectRequest,
    authContext?: BackendApiAuthContext,
  ): Promise<ProjectResponse | null> {
    return backendApiClient.request<ProjectResponse>({
      method: "POST",
      path: "/api/projects",
      accessToken,
      ...(authContext ? { authContext } : {}),
      body: request,
    });
  }

  async searchProjects(
    accessToken: string,
    query: SearchProjectsQuery,
    authContext?: BackendApiAuthContext,
  ): Promise<PageProjectResponse | null> {
    return backendApiClient.request<PageProjectResponse>({
      method: "GET",
      path: "/api/projects",
      accessToken,
      ...(authContext ? { authContext } : {}),
      query: { ...query },
    });
  }

  async updateProject(
    accessToken: string,
    projectId: string,
    request: UpdateProjectRequest,
    authContext?: BackendApiAuthContext,
  ): Promise<ProjectResponse | null> {
    return backendApiClient.request<ProjectResponse>({
      method: "PUT",
      path: `/api/projects/${projectId}`,
      accessToken,
      ...(authContext ? { authContext } : {}),
      body: request,
    });
  }

  async deleteProject(
    accessToken: string,
    projectId: string,
    authContext?: BackendApiAuthContext,
  ): Promise<string | null> {
    return backendApiClient.request<string>({
      method: "DELETE",
      path: `/api/projects/${projectId}`,
      accessToken,
      ...(authContext ? { authContext } : {}),
    });
  }

  async getProjectDetail(
    accessToken: string,
    projectId: string,
    authContext?: BackendApiAuthContext,
  ): Promise<ProjectDetailResponse | null> {
    return backendApiClient.request<ProjectDetailResponse>({
      method: "GET",
      path: `/api/projects/${projectId}/detail`,
      accessToken,
      ...(authContext ? { authContext } : {}),
    });
  }
}

export const projectService = new ProjectService();