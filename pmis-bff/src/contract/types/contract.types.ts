export type ContractType =
  | "PROJECT"
  | "MAINTENANCE"
  | "OPERATION"
  | "SUPPLY"
  | "SERVICE"
  | "OTHER";

export type ContractStatus =
  | "DRAFT"
  | "ACTIVE"
  | "SUSPENDED"
  | "COMPLETED"
  | "EXPIRED"
  | "TERMINATED";

export interface CreateContractRequest {
  contractCode: string;
  contractName: string;
  contractType: ContractType;
  signedDate?: string;
  startDate: string;
  endDate: string;
  currencyCode: string;
  contractAmount?: number;
  description?: string;
}

export interface UpdateContractRequest {
  contractName: string;
  contractType: ContractType;
  signedDate?: string;
  startDate: string;
  endDate: string;
  currencyCode: string;
  contractAmount?: number;
  description?: string;
}

export interface SearchContractsQuery {
  contractCode?: string;
  contractName?: string;
  contractType?: ContractType;
  status?: ContractStatus;
  page?: number;
  size?: number;
  sort?: string | string[];
}

export interface ChangeContractStatusQuery {
  status: ContractStatus;
}

export interface ContractResponse {
  id: number;
  contractCode: string;
  contractName: string;
  contractType: ContractType;
  status: ContractStatus;
  signedDate: string | null;
  startDate: string;
  endDate: string;
  currencyCode: string;
  contractAmount: number | null;
  description: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface PageableSort {
  empty: boolean;
  sorted: boolean;
  unsorted: boolean;
}

export interface PageableInfo {
  offset: number;
  sort: PageableSort;
  paged: boolean;
  pageNumber: number;
  pageSize: number;
  unpaged: boolean;
}

export interface PageContractResponse {
  totalElements: number;
  totalPages: number;
  size: number;
  content: ContractResponse[];
  number: number;
  sort: PageableSort;
  pageable: PageableInfo;
  first: boolean;
  last: boolean;
  numberOfElements: number;
  empty: boolean;
}