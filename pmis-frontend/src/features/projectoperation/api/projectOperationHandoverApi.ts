
import client from '@/api/client';

import type {
  ApiResponse,
} from '@/features/project/types/project';

import type {
  InitialHandoverCreateRequest,
  OperatorChangeHandoverCreateRequest,
  ProjectOperationHandoverHistoryResponse,
  ProjectOperationHandoverResponse,
  ReHandoverCreateRequest,
} from '../types/projectOperationHandover';

const HANDOVER_API = '/project-operation-handovers';

// ============================================================
// Handover Create
// ============================================================

/**
 * POST /api/project-operation-handovers/initial
 *
 * 최초 운영 인계 생성
 */
export const createInitialHandover = async (
  request: InitialHandoverCreateRequest,
): Promise<ProjectOperationHandoverResponse> => {
  const response = await client.post<
    ApiResponse<ProjectOperationHandoverResponse>
  >(`${HANDOVER_API}/initial`, request);

  return response.data.data;
};

/**
 * POST /api/project-operation-handovers/operator-change
 *
 * 운영자 변경 인계 생성
 */
export const createOperatorChangeHandover = async (
  request: OperatorChangeHandoverCreateRequest,
): Promise<ProjectOperationHandoverResponse> => {
  const response = await client.post<
    ApiResponse<ProjectOperationHandoverResponse>
  >(`${HANDOVER_API}/operator-change`, request);

  return response.data.data;
};

/**
 * POST /api/project-operation-handovers/re-handover
 *
 * 재인계 생성
 */
export const createReHandover = async (
  request: ReHandoverCreateRequest,
): Promise<ProjectOperationHandoverResponse> => {
  const response = await client.post<
    ApiResponse<ProjectOperationHandoverResponse>
  >(`${HANDOVER_API}/re-handover`, request);

  return response.data.data;
};

// ============================================================
// Handover Query
// ============================================================

/**
 * GET /api/project-operation-handovers/{id}
 *
 * 운영 인계 상세 조회
 */
export const getProjectOperationHandover = async (
  handoverId: number,
): Promise<ProjectOperationHandoverResponse> => {
  const response = await client.get<
    ApiResponse<ProjectOperationHandoverResponse>
  >(`${HANDOVER_API}/${handoverId}`);

  return response.data.data;
};

/**
 * GET /api/project-operation-handovers/by-project-operation/{id}
 *
 * 프로젝트 운영 관계별 인계 이력 조회
 */
export const getProjectOperationHandoverHistory = async (
  projectOperationId: number,
): Promise<ProjectOperationHandoverHistoryResponse> => {
  const response = await client.get<
    ApiResponse<ProjectOperationHandoverHistoryResponse>
  >(
    `${HANDOVER_API}/by-project-operation/${projectOperationId}`,
  );

  return response.data.data;
};

// ============================================================
// Handover Workflow
// ============================================================

/**
 * PATCH /api/project-operation-handovers/{id}/request
 *
 * 인계 요청
 */
export const requestProjectOperationHandover = async (
  handoverId: number,
): Promise<ProjectOperationHandoverResponse> => {
  const response = await client.patch<
    ApiResponse<ProjectOperationHandoverResponse>
  >(`${HANDOVER_API}/${handoverId}/request`);

  return response.data.data;
};

/**
 * PATCH /api/project-operation-handovers/{id}/start
 *
 * 인계 진행 시작
 */
export const startProjectOperationHandover = async (
  handoverId: number,
): Promise<ProjectOperationHandoverResponse> => {
  const response = await client.patch<
    ApiResponse<ProjectOperationHandoverResponse>
  >(`${HANDOVER_API}/${handoverId}/start`);

  return response.data.data;
};

/**
 * PATCH /api/project-operation-handovers/{id}/submit-for-review
 *
 * 인계 검토 요청
 */
export const submitProjectOperationHandoverForReview = async (
  handoverId: number,
): Promise<ProjectOperationHandoverResponse> => {
  const response = await client.patch<
    ApiResponse<ProjectOperationHandoverResponse>
  >(`${HANDOVER_API}/${handoverId}/submit-for-review`);

  return response.data.data;
};

/**
 * PATCH /api/project-operation-handovers/{id}/accept
 *
 * 인계 승인
 */
export const acceptProjectOperationHandover = async (
  handoverId: number,
): Promise<ProjectOperationHandoverResponse> => {
  const response = await client.patch<
    ApiResponse<ProjectOperationHandoverResponse>
  >(`${HANDOVER_API}/${handoverId}/accept`);

  return response.data.data;
};

/**
 * PATCH /api/project-operation-handovers/{id}/complete
 *
 * 인계 완료
 */
export const completeProjectOperationHandover = async (
  handoverId: number,
): Promise<ProjectOperationHandoverResponse> => {
  const response = await client.patch<
    ApiResponse<ProjectOperationHandoverResponse>
  >(`${HANDOVER_API}/${handoverId}/complete`);

  return response.data.data;
};

/**
 * PATCH /api/project-operation-handovers/{id}/reject
 *
 * 인계 거절
 */
export const rejectProjectOperationHandover = async (
  handoverId: number,
): Promise<ProjectOperationHandoverResponse> => {
  const response = await client.patch<
    ApiResponse<ProjectOperationHandoverResponse>
  >(`${HANDOVER_API}/${handoverId}/reject`);

  return response.data.data;
};

/**
 * PATCH /api/project-operation-handovers/{id}/cancel
 *
 * 인계 취소
 */
export const cancelProjectOperationHandover = async (
  handoverId: number,
): Promise<ProjectOperationHandoverResponse> => {
  const response = await client.patch<
    ApiResponse<ProjectOperationHandoverResponse>
  >(`${HANDOVER_API}/${handoverId}/cancel`);

  return response.data.data;
};
