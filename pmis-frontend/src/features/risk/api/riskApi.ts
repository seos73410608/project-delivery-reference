import client from '@/api/client';

import type {
  ApiResponse,
  PageResponse,
  RiskCreateRequest,
  RiskResponse,
  RiskSearchParams,
  RiskStatusUpdateRequest,
  RiskUpdateRequest,
} from '../types/risk';


const RISK_API = '/risks';

const PROJECT_API = '/projects';


/**
 * =====================================================
 * Project별 Risk 목록 조회
 * =====================================================
 *
 * GET /api/projects/{projectId}/risks
 *
 * Backend Response:
 *
 * ApiResponse<PageResponse<RiskResponse>>
 *
 * 실제 데이터:
 *
 * response.data
 *   ↓
 * ApiResponse
 *   ↓
 * data
 *   ↓
 * PageResponse
 *   ↓
 * content
 *   ↓
 * RiskResponse[]
 */
export const getProjectRisks = async (
  projectId: number,
): Promise<RiskResponse[]> => {

  const response =
    await client.get<
      ApiResponse<
        PageResponse<RiskResponse>
      >
    >(
      `${PROJECT_API}/${projectId}/risks`,
    );


  return response.data.data.content;

};


/**
 * =====================================================
 * Risk 검색
 * =====================================================
 *
 * GET /api/risks
 *
 * Query Parameters:
 *
 * - projectId
 * - keyword
 * - status
 * - priority
 * - probability
 * - impact
 * - assigneeId
 * - page
 * - size
 * - sortBy
 * - direction
 *
 * Backend Response:
 *
 * ApiResponse<PageResponse<RiskResponse>>
 */
export const searchRisks = async (
  params: RiskSearchParams,
): Promise<
  PageResponse<RiskResponse>
> => {

  const response =
    await client.get<
      ApiResponse<
        PageResponse<RiskResponse>
      >
    >(
      RISK_API,
      {
        params,
      },
    );


  return response.data.data;

};


/**
 * =====================================================
 * Risk 단건 조회
 * =====================================================
 *
 * GET /api/risks/{id}
 *
 * Backend Response:
 *
 * ApiResponse<RiskResponse>
 */
export const getRisk = async (
  riskId: number,
): Promise<RiskResponse> => {

  const response =
    await client.get<
      ApiResponse<RiskResponse>
    >(
      `${RISK_API}/${riskId}`,
    );


  return response.data.data;

};


/**
 * =====================================================
 * Risk 생성
 * =====================================================
 *
 * POST /api/projects/{projectId}/risks
 *
 * Risk는 반드시 특정 Project에 소속된다.
 *
 * Risk Key는 Backend에서 생성한다.
 */
export const createRisk = async (
  projectId: number,
  request: RiskCreateRequest,
): Promise<RiskResponse> => {

  const response =
    await client.post<
      ApiResponse<RiskResponse>
    >(
      `${PROJECT_API}/${projectId}/risks`,
      request,
    );


  return response.data.data;

};


/**
 * =====================================================
 * Risk 수정
 * =====================================================
 *
 * PUT /api/risks/{id}
 *
 * Project와 Risk Key는 변경하지 않는다.
 */
export const updateRisk = async (
  riskId: number,
  request: RiskUpdateRequest,
): Promise<RiskResponse> => {

  const response =
    await client.put<
      ApiResponse<RiskResponse>
    >(
      `${RISK_API}/${riskId}`,
      request,
    );


  return response.data.data;

};


/**
 * =====================================================
 * Risk 상태 변경
 * =====================================================
 *
 * PATCH /api/risks/{id}/status
 *
 * Backend에서 상태 전이 규칙을 검증한다.
 */
export const changeRiskStatus = async (
  riskId: number,
  request: RiskStatusUpdateRequest,
): Promise<RiskResponse> => {

  const response =
    await client.patch<
      ApiResponse<RiskResponse>
    >(
      `${RISK_API}/${riskId}/status`,
      request,
    );


  return response.data.data;

};


/**
 * =====================================================
 * Risk 삭제
 * =====================================================
 *
 * DELETE /api/risks/{id}
 *
 * Backend Response:
 *
 * 204 No Content
 */
export const deleteRisk = async (
  riskId: number,
): Promise<void> => {

  await client.delete(
    `${RISK_API}/${riskId}`,
  );

};
