import apiClient from '@/api/client';
import type { ApiResponse, Evidence } from '../types/evidence';

const EVIDENCE_REQUIREMENTS_API = '/evidence-requirements';
const PROJECTS_API = '/projects';
const EVIDENCE_API = '/evidence';
const RUNTIME_OCR_API = '/runtime/ocr';
const EVIDENCE_OCR_JOBS_API = '/evidence-ocr-jobs';

/**
 * OCR 입력 파일 업로드 응답
 */
export interface OcrUploadResponse {
  status: string;
  file_id: string;
  file_name: string;
  media_type: string;
  size_bytes: number;
  created_at: string;
}

/**
 * OCR 작업 생성 요청
 */
export interface CreateEvidenceOcrJobRequest {
  inputFileId: string;
}

/**
 * OCR 작업 상태
 */
export type EvidenceOcrJobStatus =
  | 'PENDING'
  | 'RUNNING'
  | 'SUCCEEDED'
  | 'FAILED';

/**
 * OCR 작업 응답
 */
export interface EvidenceOcrJob {
  id: number;
  evidenceId: number;
  pmisRequestId: string;
  ocrRequestId: string;
  inputFileId: string;
  status: EvidenceOcrJobStatus | (string & {});
  recognizedText: string | null;
  engineName: string | null;
  engineVersion: string | null;
  warnings: string[] | null;
  errorDetail: string | null;
  requestedAt: string;
  startedAt: string | null;
  finishedAt: string | null;
}

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

/**
 * OCR 입력 파일 업로드
 *
 * 파일을 multipart/form-data로 전송한다.
 * Content-Type과 boundary는 Axios/브라우저가 설정하도록 둔다.
 */
export async function uploadOcrFile(
  file: File,
): Promise<OcrUploadResponse> {
  const formData = new FormData();
  formData.append('file', file);

  const response = await apiClient.post<ApiResponse<OcrUploadResponse>>(
    `${RUNTIME_OCR_API}/uploads`,
    formData,
  );

  return response.data.data;
}

/**
 * 증적 OCR 작업 생성
 */
export async function createEvidenceOcrJob(
  evidenceId: number,
  request: CreateEvidenceOcrJobRequest,
): Promise<EvidenceOcrJob> {
  const response = await apiClient.post<EvidenceOcrJob>(
    `${EVIDENCE_API}s/${evidenceId}/ocr-jobs`,
    request,
  );

  return response.data;
}

/**
 * 증적 OCR 작업 조회
 */
export async function getEvidenceOcrJob(
  jobId: number,
): Promise<EvidenceOcrJob> {
  const response = await apiClient.get<EvidenceOcrJob>(
    `${EVIDENCE_OCR_JOBS_API}/${jobId}`,
  );

  return response.data;
}

/**
 * OCR 작업 상태 갱신 및 결과 동기화
 */
export async function refreshEvidenceOcrJob(
  jobId: number,
): Promise<EvidenceOcrJob> {
  const response = await apiClient.post<EvidenceOcrJob>(
    `${EVIDENCE_OCR_JOBS_API}/${jobId}/refresh`,
  );

  return response.data;
}

/**
 * 특정 증적의 OCR 작업 목록 조회
 */
export async function getEvidenceOcrJobs(
  evidenceId: number,
): Promise<EvidenceOcrJob[]> {
  const response = await apiClient.get<EvidenceOcrJob[]>(
    `${EVIDENCE_API}s/${evidenceId}/ocr-jobs`,
  );

  return response.data;
}