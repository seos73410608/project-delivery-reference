
import apiClient from '@/api/client';
import type {
  ApiResponse,
  Evidence,
  EvidenceType,
  EvidenceVersion,
  EvidenceVerificationHistory,
} from '../types/evidence';

const EVIDENCE_REQUIREMENTS_API = '/evidence-requirements';
const PROJECTS_API = '/projects';
const EVIDENCE_API = '/evidence';
const RUNTIME_OCR_API = '/runtime/ocr';
const EVIDENCE_OCR_JOBS_API = '/evidence-ocr-jobs';

/**
 * 증적 등록 요청 DTO
 *
 * POST /api/evidence-requirements/{requirementId}/evidence
 */
export interface EvidenceCreateRequest {
  wbsId?: number | null;
  evidenceType: EvidenceType;
  title: string;
  description?: string | null;
  fileName?: string | null;
  filePath?: string | null;
  sourceFileId?: string | null;
  executionUuid?: string | null;
  sourceJobId?: string | null;
  submittedAt?: string | null;
}

/**
 * 증적 버전 생성 요청 DTO
 *
 * POST /api/evidence/{evidenceId}/versions
 *
 * versionNumber, submittedAt, 검증 상태 등은
 * Backend에서 관리하므로 요청에 포함하지 않는다.
 */
export interface EvidenceVersionCreateRequest {
  evidenceType: EvidenceType;
  title: string;
  description?: string | null;
  fileName?: string | null;
  filePath?: string | null;
  sourceFileId?: string | null;
  executionUuid?: string | null;
  sourceJobId?: string | null;
}

/**
 * 증적 버전 검증 요청 DTO
 *
 * PATCH /api/evidence/{evidenceId}/versions/{versionNumber}/verification
 *
 * 허용 상태:
 * - APPROVED
 * - REJECTED
 *
 * PENDING은 검증 결과로 사용할 수 없다.
 */
export interface EvidenceVersionVerificationRequest {
  status: 'APPROVED' | 'REJECTED';
  remark?: string | null;
}

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
 * 증적 OCR 작업 생성 요청
 */
export interface CreateEvidenceOcrJobRequest {
  inputFileId: string;
}

/**
 * 증적 OCR 작업 상태
 */
export type EvidenceOcrJobStatus =
  | 'PENDING'
  | 'RUNNING'
  | 'SUCCEEDED'
  | 'FAILED';

/**
 * 증적 OCR 작업 정보
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
 * 프로젝트의 전체 증적 목록 조회
 *
 * GET /api/projects/{projectId}/evidence
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
 * 요구사항에 연결된 증적 목록 조회
 *
 * GET /api/evidence-requirements/{requirementId}/evidence
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
 * 증적 등록
 *
 * POST /api/evidence-requirements/{requirementId}/evidence
 * 성공 상태: 201 Created
 */
export async function createEvidence(
  requirementId: number,
  request: EvidenceCreateRequest,
): Promise<Evidence> {
  const response = await apiClient.post<ApiResponse<Evidence>>(
    `${EVIDENCE_REQUIREMENTS_API}/${requirementId}/evidence`,
    request,
  );

  return response.data.data;
}

/**
 * 증적 단건 조회
 *
 * GET /api/evidence/{evidenceId}
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
 * 증적 버전 생성
 *
 * POST /api/evidence/{evidenceId}/versions
 * 성공 상태: 201 Created
 *
 * Backend가 다음 버전 번호와 제출 시각 등을 생성한다.
 */
export async function createEvidenceVersion(
  evidenceId: number,
  request: EvidenceVersionCreateRequest,
): Promise<EvidenceVersion> {
  const response = await apiClient.post<ApiResponse<EvidenceVersion>>(
    `${EVIDENCE_API}/${evidenceId}/versions`,
    request,
  );

  return response.data.data;
}

/**
 * 증적 버전 목록 조회
 *
 * GET /api/evidence/{evidenceId}/versions
 *
 * Backend: List<EvidenceVersionResponse>
 * 최신 버전부터 반환하므로 첫 번째 항목이 최신 버전이다.
 */
export async function getEvidenceVersions(
  evidenceId: number,
): Promise<EvidenceVersion[]> {
  const response = await apiClient.get<ApiResponse<EvidenceVersion[]>>(
    `${EVIDENCE_API}/${evidenceId}/versions`,
  );

  return response.data.data;
}

/**
 * 증적 특정 버전 상세 조회
 *
 * GET /api/evidence/{evidenceId}/versions/{versionNumber}
 *
 * Backend: EvidenceVersionResponse
 */
export async function getEvidenceVersion(
  evidenceId: number,
  versionNumber: number,
): Promise<EvidenceVersion> {
  const response = await apiClient.get<ApiResponse<EvidenceVersion>>(
    `${EVIDENCE_API}/${evidenceId}/versions/${versionNumber}`,
  );

  return response.data.data;
}

/**
 * 증적 특정 버전의 검증 이력 조회
 *
 * GET /api/evidence/{evidenceId}/versions/{versionNumber}/verification-history
 *
 * Backend: List<EvidenceVerificationHistoryResponse>
 * 검증 이력이 없으면 빈 배열을 반환한다.
 */
export async function getEvidenceVersionVerificationHistory(
  evidenceId: number,
  versionNumber: number,
): Promise<EvidenceVerificationHistory[]> {
  const response = await apiClient.get<
    ApiResponse<EvidenceVerificationHistory[]>
  >(
    `${EVIDENCE_API}/${evidenceId}/versions/${versionNumber}/verification-history`,
  );

  return response.data.data;
}

/**
 * 증적 특정 버전 검증
 *
 * PATCH /api/evidence/{evidenceId}/versions/{versionNumber}/verification
 *
 * Backend: EvidenceResponse
 *
 * 주의:
 * - 응답의 verificationStatus는 Evidence 대표 검증 상태다.
 * - 선택한 Version의 검증 상태는 getEvidenceVersion()으로 재조회한다.
 * - 과거 Version 검증 시 Evidence 대표 상태는 변경되지 않는다.
 * - 최신 Version 검증 시 Evidence 대표 상태도 함께 변경된다.
 */
export async function verifyEvidenceVersion(
  evidenceId: number,
  versionNumber: number,
  request: EvidenceVersionVerificationRequest,
): Promise<Evidence> {
  const response = await apiClient.patch<ApiResponse<Evidence>>(
    `${EVIDENCE_API}/${evidenceId}/versions/${versionNumber}/verification`,
    request,
  );

  return response.data.data;
}

/**
 * OCR 입력 파일 업로드
 *
 * POST /api/runtime/ocr/uploads
 * Content-Type과 multipart boundary는 Axios/브라우저가 설정한다.
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
 *
 * POST /api/evidences/{evidenceId}/ocr-jobs
 * 응답은 기존 확인 결과에 따라 일반 JSON으로 처리한다.
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
 *
 * GET /api/evidence-ocr-jobs/{jobId}
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
 *
 * POST /api/evidence-ocr-jobs/{jobId}/refresh
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
 *
 * GET /api/evidences/{evidenceId}/ocr-jobs
 */
export async function getEvidenceOcrJobs(
  evidenceId: number,
): Promise<EvidenceOcrJob[]> {
  const response = await apiClient.get<EvidenceOcrJob[]>(
    `${EVIDENCE_API}s/${evidenceId}/ocr-jobs`,
  );

  return response.data;
}
