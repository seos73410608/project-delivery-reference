
 // ============================================================
 // ProjectOperation Handover Enums
 // ============================================================

/**
 * 운영 인계 유형
 */
export type ProjectOperationHandoverType =
  | 'INITIAL'
  | 'OPERATOR_CHANGE'
  | 'RE_HANDOVER';

/**
 * 운영 인계 상태
 */
export type ProjectOperationHandoverStatus =
  | 'DRAFT'
  | 'REQUESTED'
  | 'IN_PROGRESS'
  | 'REVIEW'
  | 'ACCEPTED'
  | 'COMPLETED'
  | 'REJECTED'
  | 'CANCELLED';

/**
 * 운영 참여자 역할
 */
export type HandoverParticipantRole =
  | 'SERVICE_OWNER'
  | 'OPERATOR'
  | 'SERVICE_PROVIDER'
  | 'SUPPORT_PROVIDER'
  | 'CUSTOMER'
  | 'PARTNER';

// ============================================================
// ProjectOperation Summary
// ============================================================

/**
 * 인계 대상 프로젝트 운영 관계 요약
 */
export interface HandoverProjectOperationSummary {
  id: number;
  relationshipType: string;
  status: string;
}

// ============================================================
// Project Summary
// ============================================================

/**
 * 인계 대상 프로젝트 요약
 */
export interface HandoverProjectSummary {
  id: number;
  projectCode: string;
  projectName: string;
  customerName: string;
  projectManager: string;
}

// ============================================================
// Operation Summary
// ============================================================

/**
 * 인계 대상 운영 서비스 요약
 */
export interface HandoverOperationSummary {
  id: number;
  operationCode: string;
  operationName: string;
  operationType: string;
  status: string;
}

// ============================================================
// Participant Summary
// ============================================================

/**
 * 참여 회사 요약
 */
export interface HandoverCompanySummary {
  id: number;
  companyCode: string;
  companyName: string;
}

/**
 * 참여 조직 요약
 */
export interface HandoverOrganizationSummary {
  id: number;
  organizationCode: string;
  organizationName: string;
}

/**
 * 운영 인계 참여자 요약
 */
export interface HandoverParticipantSummary {
  id: number;
  role: HandoverParticipantRole;
  primary: boolean;
  company: HandoverCompanySummary;
  organization: HandoverOrganizationSummary | null;
}

// ============================================================
// Handover Create Requests
// ============================================================

/**
 * POST /api/project-operation-handovers/initial
 *
 * 최초 운영 인계 요청
 */
export interface InitialHandoverCreateRequest {
  projectOperationId: number;
  toParticipantId: number;
  plannedHandoverDate?: string | null;
  notes?: string | null;
}

/**
 * POST /api/project-operation-handovers/operator-change
 *
 * 운영자 변경 인계 요청
 */
export interface OperatorChangeHandoverCreateRequest {
  projectOperationId: number;
  fromParticipantId: number;
  toParticipantId: number;
  plannedHandoverDate?: string | null;
  notes?: string | null;
}

/**
 * POST /api/project-operation-handovers/re-handover
 *
 * 재인계 요청
 */
export interface ReHandoverCreateRequest {
  projectOperationId: number;
  fromParticipantId: number;
  toParticipantId: number;
  plannedHandoverDate?: string | null;
  notes?: string | null;
}

// ============================================================
// Handover Response
// ============================================================

/**
 * GET /api/project-operation-handovers/{id}
 *
 * 운영 인계 상세 응답
 */
export interface ProjectOperationHandoverResponse {
  id: number;

  // 인계 정보
  handoverType: ProjectOperationHandoverType;
  status: ProjectOperationHandoverStatus;
  sequenceNo: number;

  // 프로젝트 운영 관계
  projectOperation: HandoverProjectOperationSummary;

  // 프로젝트 및 운영 서비스
  project: HandoverProjectSummary;
  operation: HandoverOperationSummary;

  // 이전 인계 정보
  previousHandoverId: number | null;
  previousSequenceNo: number | null;
  previousHandoverType: ProjectOperationHandoverType | null;

  // 인계 참여자
  fromParticipant: HandoverParticipantSummary | null;
  toParticipant: HandoverParticipantSummary;

  // 인계 담당자
  handedOverByUserId: number | null;
  handedOverByName: string | null;
  acceptedByUserId: number | null;
  acceptedByName: string | null;

  // 인계 일정 및 처리 일시
  plannedHandoverDate: string | null;
  requestedAt: string | null;
  acceptedAt: string | null;
  completedAt: string | null;

  // 비고 및 생성 정보
  notes: string | null;
  createdAt: string;
  updatedAt: string;
}

// ============================================================
// Handover History Response
// ============================================================

/**
 * GET /api/project-operation-handovers/by-project-operation/{id}
 *
 * 프로젝트 운영 관계별 인계 이력
 */
export type ProjectOperationHandoverHistoryResponse =
  ProjectOperationHandoverResponse[];

// ============================================================
// Handover Action Requests
// ============================================================

/**
 * 인계 처리 액션의 공통 요청 본문
 *
 * BFF 라우트에서 별도 요청 본문을 사용하지 않는 액션에는
 * API 호출 시 body를 전달하지 않는다.
 */
export interface HandoverNotesRequest {
  notes?: string | null;
}
