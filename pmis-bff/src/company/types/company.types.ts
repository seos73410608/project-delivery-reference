export type CompanyStatus = "ACTIVE" | "INACTIVE";

export interface CreateCompanyRequest {
  companyCode: string;
  companyName: string;
  legalName?: string;
  businessRegistrationNumber?: string;
  countryCode: string;
  website?: string;
  description?: string;
}

export interface UpdateCompanyRequest {
  companyName: string;
  legalName?: string;
  businessRegistrationNumber?: string;
  countryCode: string;
  website?: string;
  description?: string;
}

export interface SearchCompaniesQuery {
  companyCode?: string;
  companyName?: string;
  businessRegistrationNumber?: string;
  countryCode?: string;
  status?: CompanyStatus;
  page?: number;
  size?: number;
  sort?: string | string[];
}

export interface ChangeCompanyStatusQuery {
  status: CompanyStatus;
}

export interface CreateCompanyRoleAssignmentRequest {
  roleCode: string;
  effectiveFrom: string;
  description?: string;
}

export interface CloseCompanyRoleAssignmentRequest {
  effectiveTo: string;
}

export interface CompanyResponse {
  id: number;
  companyCode: string;
  companyName: string;
  legalName: string | null;
  businessRegistrationNumber: string | null;
  countryCode: string;
  website: string | null;
  status: CompanyStatus;
  description: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface CompanyRoleAssignmentResponse {
  id: number;
  companyId: number;
  companyCode: string;
  companyName: string;
  companyRoleId: number;
  roleCode: string;
  roleName: string;
  effectiveFrom: string;
  effectiveTo: string | null;
  description: string | null;
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

export interface PageCompanyResponse {
  totalElements: number;
  totalPages: number;
  size: number;
  content: CompanyResponse[];
  number: number;
  sort: PageableSort;
  pageable: PageableInfo;
  first: boolean;
  last: boolean;
  numberOfElements: number;
  empty: boolean;
}