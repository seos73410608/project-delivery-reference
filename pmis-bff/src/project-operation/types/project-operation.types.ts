export type ProjectOperationRelationshipType =
  | "BUILD_RESULT"
  | "ENHANCEMENT"
  | "MIGRATION"
  | "OTHER";

export type ProjectOperationStatus =
  | "PLANNED"
  | "ACTIVE"
  | "COMPLETED"
  | "CANCELLED";

export type OperationType =
  | "BUSINESS_SERVICE"
  | "APPLICATION_SERVICE"
  | "INFRASTRUCTURE_SERVICE"
  | "PLATFORM_SERVICE"
  | "DATABASE_SERVICE"
  | "OTHER";

export type OperationStatus =
  | "PLANNED"
  | "ACTIVE"
  | "SUSPENDED"
  | "CLOSED";

export interface CreateProjectOperationRequest {
  projectId: number;
  operationId: number;
  relationshipType: ProjectOperationRelationshipType;
  effectiveFrom?: string | null;
  effectiveTo?: string | null;
  description?: string | null;
}

export interface UpdateProjectOperationRequest {
  relationshipType: ProjectOperationRelationshipType;
  effectiveFrom?: string | null;
  effectiveTo?: string | null;
  description?: string | null;
}

export interface ChangeProjectOperationStatusRequest {
  status: ProjectOperationStatus;
}

export interface ProjectOperationResponse {
  id: number;
  relationshipType: ProjectOperationRelationshipType;
  status: ProjectOperationStatus;
  projectId: number;
  projectCode: string;
  projectName: string;
  customerName: string;
  projectManager: string;
  operationId: number;
  operationCode: string;
  operationName: string;
  operationType: OperationType;
  operationStatus: OperationStatus;
  effectiveFrom: string | null;
  effectiveTo: string | null;
  description: string | null;
  createdAt: string;
  updatedAt: string;
}