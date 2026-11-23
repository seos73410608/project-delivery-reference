
import type {
  OperationStatus,
  OperationType,
  ProjectOperationRelationshipType,
  ProjectOperationStatus,
} from "../../project-operation/types/project-operation.types.js";

export type ProjectOperationHandoverType =
  | "INITIAL"
  | "OPERATOR_CHANGE"
  | "RE_HANDOVER";

export type ProjectOperationHandoverStatus =
  | "DRAFT"
  | "REQUESTED"
  | "IN_PROGRESS"
  | "REVIEW"
  | "ACCEPTED"
  | "COMPLETED"
  | "REJECTED"
  | "CANCELLED";

export type OperationParticipantRole =
  | "SERVICE_OWNER"
  | "OPERATOR"
  | "SERVICE_PROVIDER"
  | "SUPPORT_PROVIDER"
  | "CUSTOMER"
  | "PARTNER";

export interface ProjectOperationHandoverInitialCreateRequest {
  projectOperationId: number;
  toParticipantId: number;
  plannedHandoverDate?: string | null;
  notes?: string | null;
}

export interface ProjectOperationHandoverOperatorChangeCreateRequest {
  projectOperationId: number;
  fromParticipantId: number;
  toParticipantId: number;
  plannedHandoverDate?: string | null;
  notes?: string | null;
}

export interface ProjectOperationHandoverReHandoverCreateRequest {
  projectOperationId: number;
  fromParticipantId: number;
  toParticipantId: number;
  plannedHandoverDate?: string | null;
  notes?: string | null;
}

export interface ProjectOperationHandoverProjectOperationSummary {
  id: number;
  relationshipType: ProjectOperationRelationshipType;
  status: ProjectOperationStatus;
}

export interface ProjectOperationHandoverProjectSummary {
  id: number;
  projectCode: string;
  projectName: string;
  customerName: string;
  projectManager: string;
}

export interface ProjectOperationHandoverOperationSummary {
  id: number;
  operationCode: string;
  operationName: string;
  operationType: OperationType;
  status: OperationStatus;
}

export interface ProjectOperationHandoverCompanySummary {
  id: number;
  companyCode: string;
  companyName: string;
}

export interface ProjectOperationHandoverOrganizationSummary {
  id: number;
  organizationCode: string;
  organizationName: string;
}

export interface ProjectOperationHandoverParticipantSummary {
  id: number;
  role: OperationParticipantRole;
  primary: boolean;
  company: ProjectOperationHandoverCompanySummary;
  organization: ProjectOperationHandoverOrganizationSummary;
}

export interface ProjectOperationHandoverResponse {
  id: number;
  handoverType: ProjectOperationHandoverType;
  status: ProjectOperationHandoverStatus;
  sequenceNo: number;

  projectOperation: ProjectOperationHandoverProjectOperationSummary;
  project: ProjectOperationHandoverProjectSummary;
  operation: ProjectOperationHandoverOperationSummary;

  previousHandoverId: number | null;
  previousSequenceNo: number | null;
  previousHandoverType: ProjectOperationHandoverType | null;

  fromParticipant: ProjectOperationHandoverParticipantSummary | null;
  toParticipant: ProjectOperationHandoverParticipantSummary;

  handedOverByUserId: number | null;
  handedOverByName: string | null;

  acceptedByUserId: number | null;
  acceptedByName: string | null;

  plannedHandoverDate: string | null;
  requestedAt: string | null;
  acceptedAt: string | null;
  completedAt: string | null;
  notes: string | null;

  createdAt: string;
  updatedAt: string;
}

export type ProjectOperationHandoverHistoryResponse =
  ProjectOperationHandoverResponse[];
