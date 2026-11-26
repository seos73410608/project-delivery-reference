
import client from '@/api/client';

import type {
  ApiResponse,
} from '@/features/project/types/project';

import type {
  OperationCreateRequest,
  OperationParticipantResponse,
  OperationResponse,
  OperationStatusChangeRequest,
  OperationUpdateRequest,
} from '../types/operation';

const OPERATION_API = '/operations';

// ============================================================
// Operation Query
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

/**
 * GET /api/operations/by-code/{operationCode}
 *
 * 운영 서비스 코드 기준 상세 조회
 */
export const getOperationByCode = async (
  operationCode: string,
): Promise<OperationResponse> => {
  const response = await client.get<
    ApiResponse<OperationResponse>
  >(
    `${OPERATION_API}/by-code/${encodeURIComponent(operationCode)}`,
  );

  return response.data.data;
};

/**
 * GET /api/operations/{operationId}/participants
 *
 * 인계 가능한 운영 참여자 목록 조회
 *
 * plannedHandoverDate가 전달되면 해당 날짜의 유효 참여자만 조회
 */
export const getOperationParticipants = async (
  operationId: number,
  plannedHandoverDate?: string,
): Promise<OperationParticipantResponse[]> => {
  const response = await client.get<
    ApiResponse<OperationParticipantResponse[]>
  >(
    `${OPERATION_API}/${operationId}/participants`,
    {
      params: plannedHandoverDate
        ? { plannedHandoverDate }
        : undefined,
    },
  );

  return response.data.data;
};

// ============================================================
// Operation Create
// ============================================================

/**
 * POST /api/operations
 *
 * 운영 서비스 등록
 */
export const createOperation = async (
  request: OperationCreateRequest,
): Promise<OperationResponse> => {
  const response = await client.post<
    ApiResponse<OperationResponse>
  >(OPERATION_API, request);

  return response.data.data;
};

// ============================================================
// Operation Update
// ============================================================

/**
 * PUT /api/operations/{id}
 *
 * 운영 서비스 수정
 *
 * operationCode는 수정할 수 없음
 */
export const updateOperation = async (
  operationId: number,
  request: OperationUpdateRequest,
): Promise<OperationResponse> => {
  const response = await client.put<
    ApiResponse<OperationResponse>
  >(`${OPERATION_API}/${operationId}`, request);

  return response.data.data;
};

// ============================================================
// Operation Status
// ============================================================

/**
 * PATCH /api/operations/{id}/status
 *
 * 운영 서비스 상태 변경
 */
export const updateOperationStatus = async (
  operationId: number,
  request: OperationStatusChangeRequest,
): Promise<OperationResponse> => {
  const response = await client.patch<
    ApiResponse<OperationResponse>
  >(
    `${OPERATION_API}/${operationId}/status`,
    request,
  );

  return response.data.data;
};
