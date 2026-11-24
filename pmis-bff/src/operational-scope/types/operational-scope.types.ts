
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

export interface CreateOperationalScopeRequest {
  scopeName: string;
  scopeType: OperationalScopeType;
  parentScopeId?: number;
  ownerId?: number;
  description?: string;
  effectiveFrom?: string;
  effectiveTo?: string;
}

export interface UpdateOperationalScopeRequest {
  version: number;
  scopeName: string;
  scopeType: OperationalScopeType;
  parentScopeId?: number;
  ownerId?: number;
  description?: string;
  effectiveFrom?: string;
  effectiveTo?: string;
}

export interface SearchOperationalScopesQuery {
  scopeCode?: string;
  scopeName?: string;
  scopeType?: OperationalScopeType;
  status?: OperationalScopeStatus;
  parentScopeId?: number;
  ownerId?: number;
  ownerKeyword?: string;
  page?: number;
  size?: number;
  sort?: string | string[];
}

export interface OperationalScopeResponse {
  id: number;
  scopeCode: string;
  scopeName: string;
  scopeType: OperationalScopeType;
  status: OperationalScopeStatus;
  parentScopeId: number | null;
  parentScopeCode: string | null;
  parentScopeName: string | null;
  ownerId: number | null;
  ownerUsername: string | null;
  ownerName: string | null;
  description: string | null;
  effectiveFrom: string | null;
  effectiveTo: string | null;
  createdAt: string;
  updatedAt: string;
  version: number;
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

export interface PageOperationalScopeResponse {
  totalElements: number;
  totalPages: number;
  size: number;
  content: OperationalScopeResponse[];
  number: number;
  sort: PageableSort;
  pageable: PageableInfo;
  first: boolean;
  last: boolean;
  numberOfElements: number;
  empty: boolean;
}
