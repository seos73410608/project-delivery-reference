/**
 * Change Management types
 *
 * Backend API base path: /api
 * Feature API path: /changes
 */

// ============================================================
// Common API types
// ============================================================

export interface ApiResponse<T> {
  success: boolean;
  code: string;
  message: string;
  data: T;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

// ============================================================
// Enums
// ============================================================

export type ChangeStatus =
  | 'REQUESTED'
  | 'ANALYZING'
  | 'PENDING_APPROVAL'
  | 'APPROVED'
  | 'REJECTED'
  | 'IMPLEMENTING'
  | 'VERIFIED'
  | 'CLOSED'
  | 'CANCELLED';

export type ChangePriority =
  | 'LOW'
  | 'MEDIUM'
  | 'HIGH'
  | 'CRITICAL';

export type ChangeType =
  | 'SCOPE'
  | 'SCHEDULE'
  | 'COST'
  | 'QUALITY'
  | 'TECHNICAL'
  | 'INFRASTRUCTURE'
  | 'SECURITY'
  | 'DATA'
  | 'INTERFACE'
  | 'OPERATION'
  | 'REQUIREMENT'
  | 'OTHER';

export type ChangeImpactLevel =
  | 'LOW'
  | 'MEDIUM'
  | 'HIGH'
  | 'CRITICAL';

// ============================================================
// Enum labels
// ============================================================

export const CHANGE_STATUS_LABELS: Record<ChangeStatus, string> = {
  REQUESTED: '요청',
  ANALYZING: '분석 중',
  PENDING_APPROVAL: '승인 대기',
  APPROVED: '승인',
  REJECTED: '반려',
  IMPLEMENTING: '구현 중',
  VERIFIED: '검증 완료',
  CLOSED: '종료',
  CANCELLED: '취소',
};

export const CHANGE_PRIORITY_LABELS: Record<ChangePriority, string> = {
  LOW: '낮음',
  MEDIUM: '보통',
  HIGH: '높음',
  CRITICAL: '긴급',
};

export const CHANGE_TYPE_LABELS: Record<ChangeType, string> = {
  SCOPE: '범위',
  SCHEDULE: '일정',
  COST: '비용',
  QUALITY: '품질',
  TECHNICAL: '기술',
  INFRASTRUCTURE: '인프라',
  SECURITY: '보안',
  DATA: '데이터',
  INTERFACE: '인터페이스',
  OPERATION: '운영',
  REQUIREMENT: '요구사항',
  OTHER: '기타',
};

export const CHANGE_IMPACT_LEVEL_LABELS: Record<
  ChangeImpactLevel,
  string
> = {
  LOW: '낮음',
  MEDIUM: '보통',
  HIGH: '높음',
  CRITICAL: '치명적',
};

// ============================================================
// Change response
// ============================================================

/**
 * Change 상세 및 목록 API 응답 데이터
 *
 * 날짜는 API에서 전달하는 ISO 형식 문자열로 취급한다.
 * nullable 필드는 아직 값이 등록되지 않았을 수 있다.
 */
export interface ChangeResponse {
  id: number;
  projectId: number;

  /** 프로젝트별로 백엔드가 생성하는 키 (예: CHG-001) */
  changeKey: string;

  title: string;
  description: string | null;

  status: ChangeStatus;
  priority: ChangePriority;
  changeType: ChangeType;
  impactLevel: ChangeImpactLevel;

  requesterId: number | null;
  assigneeId: number | null;

  identifiedDate: string | null;
  requestedDate: string | null;
  dueDate: string | null;

  approvedDate: string | null;
  implementedDate: string | null;
  verifiedDate: string | null;
  closedDate: string | null;

  impactAnalysis: string | null;
  implementationPlan: string | null;
  verificationResult: string | null;
  approvalComment: string | null;

  sortOrder: number | null;

  /** 초과 여부는 백엔드에서 계산해 전달한다. */
  overdue: boolean;
}

// ============================================================
// Create request
// ============================================================

/**
 * POST /api/projects/{projectId}/changes
 *
 * id, projectId, changeKey, status 및 처리일자 등
 * 백엔드 관리 필드는 요청에 포함하지 않는다.
 */
export interface ChangeCreateRequest {
  title: string;
  description?: string;

  priority: ChangePriority;
  changeType: ChangeType;
  impactLevel: ChangeImpactLevel;

  requesterId?: number;
  assigneeId?: number;

  identifiedDate?: string;
  requestedDate?: string;
  dueDate?: string;

  impactAnalysis?: string;
  implementationPlan?: string;
}

// ============================================================
// Update request
// ============================================================

/**
 * PUT /api/changes/{id}
 *
 * 상태 변경, 승인, 검증, 종료 관련 필드는
 * 일반 수정 요청에서 변경하지 않는다.
 */
export interface ChangeUpdateRequest {
  title?: string;
  description?: string;

  priority?: ChangePriority;
  changeType?: ChangeType;
  impactLevel?: ChangeImpactLevel;

  requesterId?: number;
  assigneeId?: number;

  identifiedDate?: string;
  requestedDate?: string;
  dueDate?: string;

  impactAnalysis?: string;
  implementationPlan?: string;

  sortOrder?: number;
}

// ============================================================
// Search params
// ============================================================

/**
 * GET /api/changes
 *
 * 목록 조회 필터 및 페이지네이션 파라미터
 */
export interface ChangeSearchParams {
  projectId?: number;
  keyword?: string;

  status?: ChangeStatus;
  priority?: ChangePriority;
  changeType?: ChangeType;
  impactLevel?: ChangeImpactLevel;

  requesterId?: number;
  assigneeId?: number;

  page?: number;
  size?: number;

  sortBy?: string;
  direction?: 'ASC' | 'DESC';
}

// ============================================================
// Status update request
// ============================================================

/**
 * PATCH /api/changes/{id}/status
 *
 * 실제 허용 전이는 백엔드에서 검증한다.
 */
export interface ChangeStatusUpdateRequest {
  status: ChangeStatus;
}

// ============================================================
// Approval request
// ============================================================

/**
 * PATCH /api/changes/{id}/approval
 *
 * 승인 또는 반려 처리
 */
export interface ChangeApprovalRequest {
  approved: boolean;
  approvalComment: string;
}