
import { backendApiClient } from "../../common/backend/backend-api.client.js";
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
  ): Promise<ProjectResponse | null> {
    return backendApiClient.request<ProjectResponse>({
      method: "POST",
      path: "/api/projects",
      accessToken,
      body: request,
    });
  }

  async searchProjects(
    accessToken: string,
    query: SearchProjectsQuery,
  ): Promise<PageProjectResponse | null> {
    return backendApiClient.request<PageProjectResponse>({
      method: "GET",
      path: "/api/projects",
      accessToken,
      query: { ...query },
    });
  }

  async updateProject(
    accessToken: string,
    projectId: string,
    request: UpdateProjectRequest,
  ): Promise<ProjectResponse | null> {
    return backendApiClient.request<ProjectResponse>({
      method: "PUT",
      path: `/api/projects/${projectId}`,
      accessToken,
      body: request,
    });
  }

  async deleteProject(
    accessToken: string,
    projectId: string,
  ): Promise<string | null> {
    return backendApiClient.request<string>({
      method: "DELETE",
      path: `/api/projects/${projectId}`,
      accessToken,
    });
  }

  async getProjectDetail(
    accessToken: string,
    projectId: string,
  ): Promise<ProjectDetailResponse | null> {
    return backendApiClient.request<ProjectDetailResponse>({
      method: "GET",
      path: `/api/projects/${projectId}/detail`,
      accessToken,
    });
  }
}

export const projectService = new ProjectService();
