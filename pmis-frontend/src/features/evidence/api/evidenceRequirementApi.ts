import client from '@/api/client';

import type {
  ApiResponse,
  EvidenceRequirement,
  EvidenceRequirementCreateRequest,
  EvidenceRequirementSearchParams,
  EvidenceRequirementUpdateRequest,
  PageResponse,
} from '../types/evidenceRequirement';

const EVIDENCE_REQUIREMENT_API = '/evidence-requirements';
const PROJECT_API = '/projects';

/**
 * Evidence Requirement API
 *
 * Backend API Base URL:
 * /api
 *
 * 실제 요청 URL:
 * /api/evidence-requirements/...
 * /api/projects/{projectId}/evidence-requirements/...
 */

// ============================================================
// Evidence Requirement Detail
// ============================================================

/**
 * GET /api/evidence-requirements/{id}
 *
 * Evidence Requirement 단건 조회
 */
export const getEvidenceRequirement = async (
  id: number,
): Promise<EvidenceRequirement> => {
  const response = await client.get<
    ApiResponse<EvidenceRequirement>
  >(`${EVIDENCE_REQUIREMENT_API}/${id}`);

  return response.data.data;
};

// ============================================================
// Evidence Requirement Search
// ============================================================

/**
 * GET /api/evidence-requirements
 *
 * Evidence Requirement 검색
 *
 * 지원 조건:
 * - projectId
 * - wbsId
 * - keyword
 * - requirementType
 * - required
 * - status
 * - page
 * - size
 * - sortBy
 * - direction
 */
export const searchEvidenceRequirements = async (
  params?: EvidenceRequirementSearchParams,
): Promise<PageResponse<EvidenceRequirement>> => {
  const response = await client.get<
    ApiResponse<PageResponse<EvidenceRequirement>>
  >(EVIDENCE_REQUIREMENT_API, {
    params,
  });

  return response.data.data;
};

// ============================================================
// Project Evidence Requirement List
// ============================================================

/**
 * GET /api/projects/{projectId}/evidence-requirements
 *
 * 특정 프로젝트의 Evidence Requirement 목록 조회
 */
export const getProjectEvidenceRequirements = async (
  projectId: number,
): Promise<EvidenceRequirement[]> => {
  const response = await client.get<
    ApiResponse<EvidenceRequirement[]>
  >(
    `${PROJECT_API}/${projectId}/evidence-requirements`,
  );

  return response.data.data;
};

// ============================================================
// Create Evidence Requirement
// ============================================================

/**
 * POST /api/projects/{projectId}/evidence-requirements
 *
 * Evidence Requirement 생성
 *
 * requirementKey와 status는 Backend에서 관리한다.
 */
export const createEvidenceRequirement = async (
  projectId: number,
  request: EvidenceRequirementCreateRequest,
): Promise<EvidenceRequirement> => {
  const response = await client.post<
    ApiResponse<EvidenceRequirement>
  >(
    `${PROJECT_API}/${projectId}/evidence-requirements`,
    request,
  );

  return response.data.data;
};

// ============================================================
// Update Evidence Requirement
// ============================================================

/**
 * PUT /api/evidence-requirements/{id}
 *
 * Evidence Requirement 일반 정보 수정
 *
 * requirementKey와 status는 Backend에서 관리한다.
 */
export const updateEvidenceRequirement = async (
  id: number,
  request: EvidenceRequirementUpdateRequest,
): Promise<EvidenceRequirement> => {
  const response = await client.put<
    ApiResponse<EvidenceRequirement>
  >(`${EVIDENCE_REQUIREMENT_API}/${id}`, request);

  return response.data.data;
};

// ============================================================
// Delete Evidence Requirement
// ============================================================

/**
 * DELETE /api/evidence-requirements/{id}
 *
 * Evidence Requirement 삭제
 *
 * Evidence가 연결된 Requirement는 Backend 정책에 따라
 * 삭제할 수 없다.
 */
export const deleteEvidenceRequirement = async (
  id: number,
): Promise<void> => {
  await client.delete(`${EVIDENCE_REQUIREMENT_API}/${id}`);
};