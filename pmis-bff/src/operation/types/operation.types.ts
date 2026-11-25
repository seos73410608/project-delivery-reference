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