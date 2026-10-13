import apiClient from '@/api/client';
import type { ApiResponse, Evidence } from '../types/evidence';

const EVIDENCE_REQUIREMENTS_API = '/evidence-requirements';
const PROJECTS_API = '/projects';
const EVIDENCE_API = '/evidence';

/**
 * 요구사항에 연결된 증적 목록 조회
 */
export async function getRequirementEvidence(
  requirementId: number,
): Promise<Evidence[]> {
  const response = await apiClient.get<ApiResponse<Evidence[]>>(
    `${EVIDENCE_REQUIREMENTS_API}/${requirementId}/evidence`,
  );

  return response.data.data;
}

/**
 * 프로젝트의 전체 증적 목록 조회
 */
export async function getProjectEvidence(
  projectId: number,
): Promise<Evidence[]> {
  const response = await apiClient.get<ApiResponse<Evidence[]>>(
    `${PROJECTS_API}/${projectId}/evidence`,
  );

  return response.data.data;
}

/**
 * 증적 단건 조회
 */
export async function getEvidence(
  evidenceId: number,
): Promise<Evidence> {
  const response = await apiClient.get<ApiResponse<Evidence>>(
    `${EVIDENCE_API}/${evidenceId}`,
  );

  return response.data.data;
}