export type ContractPartyRole =
  | "CLIENT"
  | "CONTRACTOR"
  | "SUBCONTRACTOR"
  | "SUPPLIER"
  | "PARTNER";

export interface CreateContractPartyRequest {
  companyId: number;
  organizationId?: number;
  partyRole: ContractPartyRole;
  primary?: boolean;
  effectiveFrom: string;
  description?: string;
}

export interface CloseContractPartyQuery {
  effectiveTo: string;
}

export interface ChangeContractPartyPrimaryQuery {
  primary: boolean;
}

export interface ContractPartyResponse {
  id: number;
  contractId: number;
  contractCode: string;
  contractName: string;
  companyId: number;
  companyCode: string;
  companyName: string;
  organizationId: number | null;
  organizationCode: string | null;
  organizationName: string | null;
  partyRole: ContractPartyRole;
  primary: boolean;
  effectiveFrom: string;
  effectiveTo: string | null;
  description: string | null;
  createdAt: string;
  updatedAt: string;
}