
export type ProjectStatus =
  | "PLANNING"
  | "IN_PROGRESS"
  | "ON_HOLD"
  | "COMPLETED"
  | "CANCELLED";

export type ProjectPriority =
  | "LOW"
  | "MEDIUM"
  | "HIGH"
  | "CRITICAL";

export interface CreateProjectRequest {
  projectCode: string;
  projectName: string;
  customerName: string;
  projectManager: string;
  description?: string;
  startDate: string;
  endDate: string;
  priority: ProjectPriority;
}

export interface UpdateProjectRequest {
  projectName: string;
  customerName: string;
  projectManager: string;
  description?: string;
  startDate: string;
  endDate: string;
  status: ProjectStatus;
  priority: ProjectPriority;
}

export interface SearchProjectsQuery {
  projectCode?: string;
  projectName?: string;
  customerName?: string;
  projectManager?: string;
  status?: ProjectStatus;
  priority?: ProjectPriority;
  startDateFrom?: string;
  startDateTo?: string;
  endDateFrom?: string;
  endDateTo?: string;
  page?: number;
  size?: number;
  sort?: string | string[];
}

export interface ProjectResponse {
  id: number;
  projectCode: string;
  projectName: string;
  customerName: string;
  projectManager: string;
  description: string | null;
  startDate: string;
  endDate: string;
  status: ProjectStatus;
  priority: ProjectPriority;
  createdAt: string;
  updatedAt: string;
}

export interface ProjectDetailResponse extends ProjectResponse {
  progressRate: number | null;
  totalPlannedManDays: number;
  weightedCompletedManDays: number;
  missingEffortTaskCount: number;
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

export interface PageProjectResponse {
  totalElements: number;
  totalPages: number;
  size: number;
  content: ProjectResponse[];
  number: number;
  sort: PageableSort;
  pageable: PageableInfo;
  first: boolean;
  last: boolean;
  numberOfElements: number;
  empty: boolean;
}
