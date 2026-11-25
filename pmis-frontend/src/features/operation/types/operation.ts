/**
 * Operation Type
 *
 * Backend:
 * OperationType.java
 */
export type OperationType =
    | "BUSINESS_SERVICE"
    | "APPLICATION_SERVICE"
    | "INFRASTRUCTURE_SERVICE"
    | "PLATFORM_SERVICE"
    | "DATABASE_SERVICE"
    | "OTHER";


/**
 * Operation Status
 *
 * Backend:
 * OperationStatus.java
 */
export type OperationStatus =
    | "PLANNED"
    | "ACTIVE"
    | "SUSPENDED"
    | "CLOSED";


/**
 * Operation Response
 *
 * GET /api/operations
 * GET /api/operations/{id}
 * GET /api/operations/by-code/{operationCode}
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


/**
 * Operation Create Request
 *
 * POST /api/operations
 *
 * operationCode is immutable after creation.
 */
export interface OperationCreateRequest {
    operationCode: string;
    operationName: string;
    operationType: OperationType;
    managerUserId?: number | null;
    description?: string | null;
    effectiveFrom?: string | null;
    effectiveTo?: string | null;
}


/**
 * Operation Update Request
 *
 * PUT /api/operations/{id}
 *
 * operationCode cannot be changed.
 */
export interface OperationUpdateRequest {
    operationName: string;
    operationType: OperationType;
    managerUserId?: number | null;
    description?: string | null;
    effectiveFrom?: string | null;
    effectiveTo?: string | null;
}


/**
 * Operation Status Change Request
 *
 * PATCH /api/operations/{id}/status
 */
export interface OperationStatusChangeRequest {
    status: OperationStatus;
}


/**
 * Operation Search Parameters
 *
 * Backend currently provides GET /api/operations
 * without search or pagination parameters.
 *
 * These parameters are for client-side filtering.
 */
export interface OperationSearchParams {
    operationCode?: string;
    operationName?: string;
    operationType?: OperationType;
    status?: OperationStatus;
}