export type ProjectStatus =
  | 'PLANNING'
  | 'IN_PROGRESS'
  | 'ON_HOLD'
  | 'COMPLETED'
  | 'CANCELLED';

export type ProjectPriority =
  | 'LOW'
  | 'MEDIUM'
  | 'HIGH'
  | 'CRITICAL';

export interface Project {
  id: number;
  projectCode: string;
  projectName: string;
  customerName: string;
  projectManager: string;
  description: string;
  startDate: string;
  endDate: string;
  status: ProjectStatus;
  priority: ProjectPriority;
  createdAt: string;
  updatedAt: string;
}

export interface ProjectCreateRequest {
  projectCode: string;
  projectName: string;
  customerName: string;
  projectManager: string;
  description: string;
  startDate: string;
  endDate: string;
  priority: ProjectPriority;
}

export interface ProjectUpdateRequest {
  projectName: string;
  customerName: string;
  projectManager: string;
  description: string;
  startDate: string;
  endDate: string;
  status: ProjectStatus;
  priority: ProjectPriority;
}

export interface ProjectSearchParams {
  projectCode?: string;
  projectName?: string;
  customerName?: string;
  status?: ProjectStatus;
  priority?: ProjectPriority;
  page?: number;
  size?: number;
  sort?: string;
}

export interface PageResponse<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
    sort: {
      empty: boolean;
      sorted: boolean;
      unsorted: boolean;
    };
    offset: number;
    paged: boolean;
    unpaged: boolean;
  };
  last: boolean;
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  sort: {
    empty: boolean;
    sorted: boolean;
    unsorted: boolean;
  };
  first: boolean;
  numberOfElements: number;
  empty: boolean;
}

export interface ApiResponse<T> {
  success: boolean;
  code: string;
  message?: string;
  data: T;
}