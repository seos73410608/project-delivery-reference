// =========================================
// Common API types
// =========================================

export interface ApiResponse<T> {
  success: boolean;
  code: string;
  message: string;
  data: T;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

// =========================================
// Enums
// =========================================

export type RequirementStatus =
  | 'PENDING'
  | 'PRESENT'
  | 'MISSING'
  | 'NOT_REQUIRED';

export type RequirementType =
  | 'INSTALLATION'
  | 'CONFIGURATION'
  | 'ACCOUNT'
  | 'TEST'
  | 'MIGRATION'
  | 'INSPECTION'
  | 'REPORT'
  | 'OPERATION'
  | 'SECURITY'
  | 'OTHER';

// =========================================
// Evidence Requirement
// =========================================

export interface EvidenceRequirement {
  id: number;
  projectId: number;
  wbsId: number | null;
  requirementKey: string;
  title: string;
  description: string | null;
  requirementType: RequirementType;
  required: boolean;
  dueDate: string | null;
  status: RequirementStatus;
  sortOrder: number;
  createdAt: string;
  updatedAt: string;
}

// =========================================
// Create Request
// =========================================

export interface EvidenceRequirementCreateRequest {
  wbsId?: number | null;
  title: string;
  description?: string | null;
  requirementType: RequirementType;
  required: boolean;
  dueDate?: string | null;
  sortOrder?: number;
}

// =========================================
// Update Request
// =========================================

export interface EvidenceRequirementUpdateRequest {
  wbsId?: number | null;
  title: string;
  description?: string | null;
  requirementType: RequirementType;
  required: boolean;
  dueDate?: string | null;
  sortOrder?: number;
}

// =========================================
// Search Parameters
// =========================================

export interface EvidenceRequirementSearchParams {
  projectId?: number;
  wbsId?: number;
  keyword?: string;
  requirementType?: RequirementType;
  required?: boolean;
  status?: RequirementStatus;
  page?: number;
  size?: number;
  sortBy?: string;
  direction?: 'ASC' | 'DESC';
}