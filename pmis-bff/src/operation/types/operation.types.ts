
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

export interface CreateOperationRequest {
  operationCode: string;
  operationName: string;
  operationType: OperationType;
  managerUserId?: number;
  description?: string;
  effectiveFrom?: string;
  effectiveTo?: string;
}

export interface UpdateOperationRequest {
  operationName: string;
  operationType: OperationType;
  managerUserId?: number;
  description?: string;
  effectiveFrom?: string;
  effectiveTo?: string;
}

export interface UpdateOperationStatusRequest {
  status: OperationStatus;
}

export type OperationParticipantRole =
  | "SERVICE_OWNER"
  | "OPERATOR"
  | "SERVICE_PROVIDER"
  | "SUPPORT_PROVIDER"
  | "CUSTOMER"
  | "PARTNER";

export interface OperationParticipantCompanySummary {
  id: number;
  companyCode: string;
  companyName: string;
}

export interface OperationParticipantOrganizationSummary {
  id: number;
  organizationCode: string;
  organizationName: string;
}

export interface OperationParticipantResponse {
  id: number;
  role: OperationParticipantRole;
  primary: boolean;
  company: OperationParticipantCompanySummary;
  organization: OperationParticipantOrganizationSummary | null;
}

export interface OperationParticipantsQuery {
  plannedHandoverDate?: string;
}
