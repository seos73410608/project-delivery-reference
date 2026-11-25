
import client from '@/api/client';

import type {
  ApiResponse,
} from '@/features/project/types/project';

import type {
  OperationResponse,
  ProjectOperationCreateRequest,
  ProjectOperationResponse,
  ProjectOperationStatusChangeRequest,
  ProjectOperationUpdateRequest,
} from '../types/projectOperation';

const PROJECT_OPERATION_API = '/project-operations';
const OPERATION_API = '/operations';

// ============================================================
// Operation
// ============================================================

/**
 * GET /api/operations
 *
 * 전체 운영 서비스 목록 조회
 */
export const getOperations = async (): Promise<OperationResponse[]> => {
  const response = await client.get<
    ApiResponse<OperationResponse[]>
  >(OPERATION_API);

  return response.data.data;
};

/**
 * GET /api/operations/{id}
 *
 * 운영 서비스 상세 조회
 */
export const getOperation = async (
  operationId: number,
): Promise<OperationResponse> => {
  const response = await client.get<
    ApiResponse<OperationResponse>
  >(`${OPERATION_API}/${operationId}`);

  return response.data.data;
};

// ============================================================
// ProjectOperation
// ============================================================

/**
 * GET /api/project-operations/{id}
 *
 * 프로젝트 운영 관계 상세 조회
 */
export const getProjectOperation = async (
  id: number,
): Promise<ProjectOperationResponse> => {
  const response = await client.get<
    ApiResponse<ProjectOperationResponse>
  >(`${PROJECT_OPERATION_API}/${id}`);

  return response.data.data;
};

/**
 * GET /api/project-operations/by-project/{projectId}
 *
 * 프로젝트 기준 운영 관계 목록 조회
 */
export const getProjectOperationsByProject = async (
  projectId: number,
): Promise<ProjectOperationResponse[]> => {
  const response = await client.get<
    ApiResponse<ProjectOperationResponse[]>
  >(`${PROJECT_OPERATION_API}/by-project/${projectId}`);

  return response.data.data;
};

/**
 * GET /api/project-operations/by-operation/{operationId}
 *
 * 운영 서비스 기준 프로젝트 관계 목록 조회
 */
export const getProjectOperationsByOperation = async (
  operationId: number,
): Promise<ProjectOperationResponse[]> => {
  const response = await client.get<
    ApiResponse<ProjectOperationResponse[]>
  >(`${PROJECT_OPERATION_API}/by-operation/${operationId}`);

  return response.data.data;
};

/**
 * POST /api/project-operations
 *
 * 프로젝트 운영 관계 생성
 */
export const createProjectOperation = async (
  request: ProjectOperationCreateRequest,
): Promise<ProjectOperationResponse> => {
  const response = await client.post<
    ApiResponse<ProjectOperationResponse>
  >(PROJECT_OPERATION_API, request);

  return response.data.data;
};

/**
 * PUT /api/project-operations/{id}
 *
 * 프로젝트 운영 관계 수정
 */
export const updateProjectOperation = async (
  id: number,
  request: ProjectOperationUpdateRequest,
): Promise<ProjectOperationResponse> => {
  const response = await client.put<
    ApiResponse<ProjectOperationResponse>
  >(`${PROJECT_OPERATION_API}/${id}`, request);

  return response.data.data;
};

/**
 * PATCH /api/project-operations/{id}/status
 *
 * 프로젝트 운영 관계 상태 변경
 */
export const updateProjectOperationStatus = async (
  id: number,
  request: ProjectOperationStatusChangeRequest,
): Promise<ProjectOperationResponse> => {
  const response = await client.patch<
    ApiResponse<ProjectOperationResponse>
  >(`${PROJECT_OPERATION_API}/${id}/status`, request);

  return response.data.data;
};
