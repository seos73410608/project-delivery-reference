import client from '@/api/client';

import type {
  ApiResponse,
  ChangeApprovalRequest,
  ChangeCreateRequest,
  ChangeResponse,
  ChangeSearchParams,
  ChangeStatusUpdateRequest,
  ChangeUpdateRequest,
  PageResponse,
} from '../types/change';

const CHANGE_API = '/changes';
const PROJECT_API = '/projects';

/**
 * Change Management API
 *
 * Backend API Base URL:
 * /api
 *
 * 실제 요청 URL:
 * /api/changes/...
 * /api/projects/{projectId}/changes/...
 */

// ============================================================
// Change Detail
// ============================================================

/**
 * GET /api/changes/{id}
 *
 * Change 단건 조회
 */
export const getChange = async (
  id: number,
): Promise<ChangeResponse> => {
  const response = await client.get<ApiResponse<ChangeResponse>>(
    `${CHANGE_API}/${id}`,
  );

  return response.data.data;
};

// ============================================================
// Change Search
// ============================================================

/**
 * GET /api/changes
 *
 * Change 검색
 *
 * 지원 조건:
 * - projectId
 * - keyword
 * - status
 * - priority
 * - changeType
 * - impactLevel
 * - requesterId
 * - assigneeId
 * - page
 * - size
 * - sortBy
 * - direction
 */
export const searchChanges = async (
  params?: ChangeSearchParams,
): Promise<PageResponse<ChangeResponse>> => {
  const response = await client.get<
    ApiResponse<PageResponse<ChangeResponse>>
  >(CHANGE_API, {
    params,
  });

  return response.data.data;
};

// ============================================================
// Project Change List
// ============================================================

/**
 * GET /api/projects/{projectId}/changes
 *
 * 특정 프로젝트의 Change 목록 조회
 */
export const getProjectChanges = async (
  projectId: number,
): Promise<ChangeResponse[]> => {
  const response = await client.get<
    ApiResponse<PageResponse<ChangeResponse>>
  >(`${PROJECT_API}/${projectId}/changes`);

  return response.data.data.content;
};

// ============================================================
// Create Change
// ============================================================

/**
 * POST /api/projects/{projectId}/changes
 *
 * Change 생성
 *
 * changeKey와 status는 Backend에서 관리한다.
 */
export const createChange = async (
  projectId: number,
  request: ChangeCreateRequest,
): Promise<ChangeResponse> => {
  const response = await client.post<
    ApiResponse<ChangeResponse>
  >(`${PROJECT_API}/${projectId}/changes`, request);

  return response.data.data;
};

// ============================================================
// Update Change
// ============================================================

/**
 * PUT /api/changes/{id}
 *
 * Change 일반 정보 수정
 *
 * 상태 변경 및 승인 처리는 별도 API를 사용한다.
 */
export const updateChange = async (
  id: number,
  request: ChangeUpdateRequest,
): Promise<ChangeResponse> => {
  const response = await client.put<
    ApiResponse<ChangeResponse>
  >(`${CHANGE_API}/${id}`, request);

  return response.data.data;
};

// ============================================================
// Delete Change
// ============================================================

/**
 * DELETE /api/changes/{id}
 *
 * Change 삭제
 */
export const deleteChange = async (
  id: number,
): Promise<void> => {
  await client.delete(`${CHANGE_API}/${id}`);
};

// ============================================================
// Change Status
// ============================================================

/**
 * PATCH /api/changes/{id}/status
 *
 * Change 상태 변경
 *
 * 실제 Lifecycle 전이는 Backend에서 검증한다.
 *
 * REQUESTED
 *   -> ANALYZING
 *   -> PENDING_APPROVAL
 *   -> APPROVED
 *   -> IMPLEMENTING
 *   -> VERIFIED
 *   -> CLOSED
 *
 * 일부 상태에서는 CANCELLED / REJECTED 등의
 * 별도 전이가 적용된다.
 */
export const updateChangeStatus = async (
  id: number,
  request: ChangeStatusUpdateRequest,
): Promise<ChangeResponse> => {
  const response = await client.patch<
    ApiResponse<ChangeResponse>
  >(`${CHANGE_API}/${id}/status`, request);

  return response.data.data;
};

// ============================================================
// Change Approval
// ============================================================

/**
 * PATCH /api/changes/{id}/approval
 *
 * Change 승인 / 반려
 *
 * approved = true
 *   -> APPROVED
 *
 * approved = false
 *   -> REJECTED
 */
export const updateChangeApproval = async (
  id: number,
  request: ChangeApprovalRequest,
): Promise<ChangeResponse> => {
  const response = await client.patch<
    ApiResponse<ChangeResponse>
  >(`${CHANGE_API}/${id}/approval`, request);

  return response.data.data;
};