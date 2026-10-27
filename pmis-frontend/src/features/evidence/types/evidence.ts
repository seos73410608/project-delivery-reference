
/**
 * 공통 API 응답
 */
export interface ApiResponse<T> {
  success: boolean;
  code: string;
  message?: string;
  data: T;
}

/**
 * 증적 유형
 */
export type EvidenceType =
  | 'OTHER'
  | 'LOG'
  | 'SCREENSHOT'
  | 'TEST_RESULT'
  | 'IMAGE'
  | 'REPORT'
  | 'CONFIGURATION'
  | 'DOCUMENT';

/**
 * 증적 검증 상태
 */
export type VerificationStatus =
  | 'PENDING'
  | 'APPROVED'
  | 'REJECTED';

/**
 * 증적 등록 요청
 *
 * POST /api/evidence-requirements/{requirementId}/evidence
 */
export interface EvidenceCreateRequest {
  /** 연결할 WBS ID */
  wbsId: number | null;

  /** 증적 유형 */
  evidenceType: EvidenceType;

  /** 증적 제목 (최대 200자) */
  title: string;

  /** 증적 설명 */
  description?: string | null;

  /** 표시용 파일명 */
  fileName?: string | null;

  /** 기존 수동 등록 호환용 경로 */
  filePath?: string | null;

  /** Automation Runtime 파일 ID */
  sourceFileId?: string | null;

  /** Automation Runtime 실행 UUID */
  executionUuid?: string | null;

  /** Automation Runtime Job ID */
  sourceJobId?: string | null;

  /** 제출 일시 */
  submittedAt?: string | null;
}

/**
 * 증적 정보
 */
export interface Evidence {
  id: number;
  projectId: number;
  requirementId: number;
  wbsId: number | null;

  evidenceKey: string;
  evidenceType: EvidenceType;
  title: string;
  description: string | null;

  fileName: string | null;
  filePath: string | null;

  submittedById: number;
  submittedByName: string;
  submittedAt: string;

  verificationStatus: VerificationStatus;
  verifiedById: number | null;
  verifiedByName: string | null;
  verifiedAt: string | null;
  verificationRemark: string | null;

  createdAt: string;
  updatedAt: string;
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
