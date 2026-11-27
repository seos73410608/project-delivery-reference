
export type OrganizationType =
  | "HEADQUARTERS"
  | "DIVISION"
  | "DEPARTMENT"
  | "TEAM"
  | "BRANCH"
  | "CENTER"
  | "OTHER";

export type OrganizationStatus = "ACTIVE" | "INACTIVE";

export interface CreateOrganizationRequest {
  organizationCode: string;
  organizationName: string;
  organizationType: OrganizationType;
  parentOrganizationId?: number | null;
  managerUserId?: number | null;
  effectiveFrom?: string | null;
  effectiveTo?: string | null;
  description?: string | null;
}

export interface UpdateOrganizationRequest {
  organizationName: string;
  organizationType: OrganizationType;
  parentOrganizationId?: number | null;
  managerUserId?: number | null;
  effectiveFrom?: string | null;
  effectiveTo?: string | null;
  description?: string | null;
}

export interface ChangeOrganizationStatusQuery {
  status: OrganizationStatus;
}

export interface OrganizationResponse {
  id: number;
  companyId: number;
  companyCode: string;
  companyName: string;
  organizationCode: string;
  organizationName: string;
  organizationType: OrganizationType;
  parentOrganizationId: number | null;
  parentOrganizationName: string | null;
  managerUserId: number | null;
  managerName: string | null;
  status: OrganizationStatus;
  effectiveFrom: string | null;
  effectiveTo: string | null;
  description: string | null;
  createdAt: string;
  updatedAt: string;
}
