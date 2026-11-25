
import type { ApiResponse } from '@/features/project/types/project';

// ============================================================
// ProjectOperation Enums
// ============================================================

/**
 * 프로젝트와 운영 서비스의 관계 유형
 */
export type ProjectOperationRelationshipType =
  | 'BUILD_RESULT'
  | 'ENHANCEMENT'
  | 'MIGRATION'
  | 'OTHER';

/**
 * 프로젝트 운영 관계 상태
 */
export type ProjectOperationStatus =
  | 'PLANNED'
  | 'ACTIVE'
  | 'COMPLETED'
  | 'CANCELLED';

// ============================================================
// Operation Enums
// ============================================================

/**
 * 운영 서비스 유형
 */
export type OperationType =
  | 'BUSINESS_SERVICE'
  | 'APPLICATION_SERVICE'
  | 'INFRASTRUCTURE_SERVICE'
  | 'PLATFORM_SERVICE'
  | 'DATABASE_SERVICE'
  | 'OTHER';

/**
 * 운영 서비스 자체의 상태
 */
export type OperationStatus =
  | 'PLANNED'
  | 'ACTIVE'
  | 'SUSPENDED'
  | 'CLOSED';

// ============================================================
// Operation Response
// ============================================================

/**
 * GET /api/operations
 * GET /api/operations/{id}
 *
 * 운영 서비스 조회 응답
 */
export interface OperationResponse {
  id: number;
  operationCode: string;
  operationName: string;
  operationType: OperationType;
  status: OperationStatus;
  managerUserId: number | null;
  managerName: string | null;
  description: string | null;
  effectiveFrom: string | null;
  effectiveTo: string | null;
  createdAt: string;
  updatedAt: string;
}

// ============================================================
// ProjectOperation Response
// ============================================================

/**
 * GET /api/project-operations/{id}
 * GET /api/project-operations/by-project/{projectId}
 * GET /api/project-operations/by-operation/{operationId}
 *
 * 프로젝트와 운영 서비스 간 관계 응답
 */
export interface ProjectOperationResponse {
  id: number;

  // 관계 정보
  relationshipType: ProjectOperationRelationshipType;
  status: ProjectOperationStatus;

  // 프로젝트 정보
  projectId: number;
  projectCode: string;
  projectName: string;
  customerName: string;
  projectManager: string;

  // 운영 서비스 정보
  operationId: number;
  operationCode: string;
  operationName: string;
  operationType: OperationType;
  operationStatus: OperationStatus;

  // 관계 적용 기간
  effectiveFrom: string | null;
  effectiveTo: string | null;

  // 관계 설명
  description: string | null;

  // 생성 및 수정 일시
  createdAt: string;
  updatedAt: string;
}

// ============================================================
// ProjectOperation Requests
// ============================================================

/**
 * POST /api/project-operations
 *
 * 프로젝트 운영 관계 생성 요청
 */
export interface ProjectOperationCreateRequest {
  projectId: number;
  operationId: number;
  relationshipType: ProjectOperationRelationshipType;
  effectiveFrom?: string;
  effectiveTo?: string;
  description?: string;
}

/**
 * PUT /api/project-operations/{id}
 *
 * 프로젝트 운영 관계 수정 요청
 *
 * 프로젝트와 운영 서비스 연결은 변경할 수 없다.
 */
export interface ProjectOperationUpdateRequest {
  relationshipType: ProjectOperationRelationshipType;
  effectiveFrom?: string;
  effectiveTo?: string;
  description?: string;
}

/**
 * PATCH /api/project-operations/{id}/status
 *
 * 프로젝트 운영 관계 상태 변경 요청
 */
export interface ProjectOperationStatusChangeRequest {
  status: ProjectOperationStatus;
}

// ============================================================
// API Response Types
// ============================================================

export type ProjectOperationApiResponse =
  ApiResponse<ProjectOperationResponse>;

export type ProjectOperationListApiResponse =
  ApiResponse<ProjectOperationResponse[]>;

export type OperationListApiResponse =
  ApiResponse<OperationResponse[]>;
