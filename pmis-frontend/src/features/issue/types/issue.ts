/**
 * Issue API 공통 응답
 */
export interface ApiResponse<T> {
  success: boolean;
  code: string;
  message: string;
  data: T;
}


/**
 * Issue Status
 */
export type IssueStatus =
  | 'OPEN'
  | 'IN_PROGRESS'
  | 'RESOLVED'
  | 'CLOSED'
  | 'ON_HOLD'
  | 'CANCELLED';


/**
 * Issue Priority
 */
export type IssuePriority =
  | 'LOW'
  | 'MEDIUM'
  | 'HIGH'
  | 'CRITICAL';


/**
 * Issue Response
 */
export interface IssueResponse {

  /**
   * Issue PK
   */
  id: number;


  /**
   * 소속 Project ID
   */
  projectId: number;


  /**
   * Issue 식별 Key
   */
  issueKey: string;


  /**
   * Issue 제목
   */
  title: string;


  /**
   * Issue 상세 내용
   */
  description: string | null;


  /**
   * Issue 상태
   */
  status: IssueStatus;


  /**
   * Issue 중요도
   */
  priority: IssuePriority;


  /**
   * 담당자 ID
   */
  assigneeId: number | null;


  /**
   * 등록자 ID
   */
  reporterId: number | null;


  /**
   * 실제 Issue 발생일
   */
  occurredDate: string | null;


  /**
   * 조치 목표일
   */
  dueDate: string | null;


  /**
   * Issue 해결일
   */
  resolvedDate: string | null;


  /**
   * 표시 순서
   */
  sortOrder: number;


  /**
   * 생성일시
   */
  createdAt: string;


  /**
   * 수정일시
   */
  updatedAt: string;

}


/**
 * Issue 생성 요청
 *
 * POST
 * /api/projects/{projectId}/issues
 */
export interface IssueCreateRequest {

  /**
   * Issue 제목
   */
  title: string;


  /**
   * Issue 상세 내용
   */
  description?: string;


  /**
   * Issue 상태
   */
  status: IssueStatus;


  /**
   * Issue 중요도
   */
  priority: IssuePriority;


  /**
   * 담당자 ID
   */
  assigneeId?: number | null;


  /**
   * 실제 발생일
   */
  occurredDate?: string | null;


  /**
   * 조치 목표일
   */
  dueDate?: string | null;


  /**
   * 표시 순서
   */
  sortOrder?: number;

}


/**
 * Issue 수정 요청
 *
 * PUT
 * /api/issues/{id}
 */
export interface IssueUpdateRequest {

  /**
   * Issue 제목
   */
  title: string;


  /**
   * Issue 상세 내용
   */
  description?: string;


  /**
   * Issue 상태
   */
  status: IssueStatus;


  /**
   * Issue 중요도
   */
  priority: IssuePriority;


  /**
   * 담당자 ID
   */
  assigneeId?: number | null;


  /**
   * 실제 발생일
   */
  occurredDate?: string | null;


  /**
   * 조치 목표일
   */
  dueDate?: string | null;


  /**
   * 표시 순서
   */
  sortOrder?: number;

}


/**
 * Issue 상태 변경 요청
 *
 * PATCH
 * /api/issues/{id}/status
 */
export interface IssueStatusUpdateRequest {

  /**
   * 변경할 Issue 상태
   */
  status: IssueStatus;

}


/**
 * Issue 검색 조건
 *
 * GET /api/issues
 *
 * Query Parameters:
 *
 * - projectId
 * - keyword
 * - status
 * - priority
 * - assigneeId
 * - page
 * - size
 * - sortBy
 * - direction
 */
export interface IssueSearchParams {

  /**
   * 프로젝트 ID
   */
  projectId?: number;


  /**
   * 키워드 검색
   *
   * 제목과 상세 내용을 대상으로 검색한다.
   */
  keyword?: string;


  /**
   * Issue 상태
   */
  status?: IssueStatus;


  /**
   * Issue 중요도
   */
  priority?: IssuePriority;


  /**
   * 담당자 ID
   */
  assigneeId?: number;


  /**
   * 페이지 번호
   *
   * 0부터 시작한다.
   */
  page?: number;


  /**
   * 페이지 크기
   */
  size?: number;


  /**
   * 정렬 필드
   */
  sortBy?: string;


  /**
   * 정렬 방향
   */
  direction?: 'ASC' | 'DESC';

}


/**
 * Page Response
 */
export interface PageResponse<T> {

  /**
   * 현재 페이지 데이터
   */
  content: T[];


  /**
   * 전체 페이지 수
   */
  totalPages: number;


  /**
   * 전체 데이터 수
   */
  totalElements: number;


  /**
   * 페이지 크기
   */
  size: number;


  /**
   * 현재 페이지 번호
   */
  number: number;


  /**
   * 현재 페이지 데이터 수
   */
  numberOfElements: number;


  /**
   * 첫 번째 페이지 여부
   */
  first: boolean;


  /**
   * 마지막 페이지 여부
   */
  last: boolean;

}