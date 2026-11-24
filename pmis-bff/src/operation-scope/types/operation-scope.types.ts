import type {
  OperationStatus,
  OperationType,
} from "../../project-operation/types/project-operation.types.js";

export type OperationScopeStatus =
  | "PLANNED"
  | "ACTIVE"
  | "COMPLETED"
  | "CANCELLED";

export type OperationalScopeType =
  | "SERVICE"
  | "SYSTEM"
  | "ENVIRONMENT"
  | "PLATFORM"
  | "DATABASE"
  | "SERVER_GROUP"
  | "CI_SCOPE";

export type OperationalScopeStatus =
  | "PLANNED"
  | "ACTIVE"
  | "SUSPENDED"
  | "CLOSED";

export interface CreateOperationScopeRequest {
  operationId: number;
  operationalScopeId: number;
  effectiveFrom?: string | null;
  effectiveTo?: string | null;
  description?: string | null;
}

export interface UpdateOperationScopeRequest {
  effectiveFrom?: string | null;
  effectiveTo?: string | null;
  description?: string | null;
}

export interface ChangeOperationScopeStatusRequest {
  status: OperationScopeStatus;
}

export interface OperationScopeResponse {
  id: number;
  status: OperationScopeStatus;
  operationId: number;
  operationCode: string;
  operationName: string;
  operationType: OperationType;
  operationStatus: OperationStatus;
  operationalScopeId: number;
  scopeCode: string;
  scopeName: string;
  scopeType: OperationalScopeType;
  operationalScopeStatus: OperationalScopeStatus;
  effectiveFrom: string | null;
  effectiveTo: string | null;
  description: string | null;
  createdAt: string;
  updatedAt: string;
}