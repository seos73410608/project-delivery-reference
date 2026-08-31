/**
 * Schedule API 공통 응답
 */
export interface ApiResponse<T> {
  success: boolean;
  code: string;
  message: string;
  data: T;
}

/**
 * Schedule Status
 */
export type ScheduleStatus =
  | 'PLANNED'
  | 'IN_PROGRESS'
  | 'COMPLETED'
  | 'ON_HOLD'
  | 'CANCELLED';

/**
 * Schedule Response
 */
export interface ScheduleResponse {
  id: number;
  projectId: number;
  wbsId: number;
  scheduleName: string;
  startDate: string;
  endDate: string;
  status: ScheduleStatus;
  description: string | null;
  sortOrder: number;
  createdAt: string;
  updatedAt: string;
}

/**
 * Schedule 생성 요청
 */
export interface ScheduleCreateRequest {
  wbsId: number;
  scheduleName: string;
  startDate: string;
  endDate: string;
  status: ScheduleStatus;
  description?: string;
  sortOrder?: number;
}

/**
 * Schedule 수정 요청
 */
export interface ScheduleUpdateRequest {
  wbsId: number;
  scheduleName: string;
  startDate: string;
  endDate: string;
  status: ScheduleStatus;
  description?: string;
  sortOrder?: number;
}

/**
 * Schedule 검색 조건
 */
export interface ScheduleSearchParams {
  projectId?: number;
  wbsId?: number;
  keyword?: string;
  status?: ScheduleStatus;
  sortBy?: string;
  direction?: 'ASC' | 'DESC';
  page?: number;
  size?: number;
}

/**
 * Page Response
 */
export interface PageResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  numberOfElements: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}