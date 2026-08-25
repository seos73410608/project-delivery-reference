import client from '@/api/client';

import type {
  ApiResponse,
  PageResponse,
  Project,
  ProjectCreateRequest,
  ProjectSearchParams,
  ProjectUpdateRequest,
} from '../types/project';

const PROJECT_API = '/projects';

/**
 * 프로젝트 목록 조회
 *
 * GET /api/projects
 */
export const getProjects = async (
  params?: ProjectSearchParams,
): Promise<PageResponse<Project>> => {
  const response = await client.get<ApiResponse<PageResponse<Project>>>(
    PROJECT_API,
    {
      params,
    },
  );

  return response.data.data;
};

/**
 * 프로젝트 상세 조회
 *
 * GET /api/projects/{id}/detail
 */
export const getProject = async (
  projectId: number,
): Promise<Project> => {
  const response = await client.get<ApiResponse<Project>>(
    `${PROJECT_API}/${projectId}/detail`,
  );

  return response.data.data;
};

/**
 * 프로젝트 생성
 *
 * POST /api/projects
 */
export const createProject = async (
  request: ProjectCreateRequest,
): Promise<Project> => {
  const response = await client.post<ApiResponse<Project>>(
    PROJECT_API,
    request,
  );

  return response.data.data;
};

/**
 * 프로젝트 수정
 *
 * PUT /api/projects/{id}
 */
export const updateProject = async (
  projectId: number,
  request: ProjectUpdateRequest,
): Promise<Project> => {
  const response = await client.put<ApiResponse<Project>>(
    `${PROJECT_API}/${projectId}`,
    request,
  );

  return response.data.data;
};

/**
 * 프로젝트 삭제
 *
 * DELETE /api/projects/{id}
 *
 * Backend 정책:
 * ADMIN 권한만 삭제 가능
 */
export const deleteProject = async (
  projectId: number,
): Promise<void> => {
  await client.delete<ApiResponse<null>>(
    `${PROJECT_API}/${projectId}`,
  );
};