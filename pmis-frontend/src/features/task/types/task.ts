
/**
 * Task 상태
 */
export type TaskStatus =
    | "PLANNED"
    | "IN_PROGRESS"
    | "ON_HOLD"
    | "COMPLETED"
    | "CANCELLED";

/**
 * Task 우선순위
 */
export type TaskPriority =
    | "LOW"
    | "MEDIUM"
    | "HIGH"
    | "CRITICAL";

/**
 * Task 응답
 */
export interface Task {
    id: number;
    projectId: number;
    wbsId: number;
    scheduleId: number | null;
    assigneeId: number | null;
    assigneeName: string | null;
    taskName: string;
    description: string | null;
    status: TaskStatus;
    progressRate: number;
    priority: TaskPriority;
    startDate: string | null;
    endDate: string | null;
    sortOrder: number;
    createdAt: string;
    updatedAt: string;
}

/**
 * Task 생성 요청
 */
export interface TaskCreateRequest {
    wbsId: number;
    scheduleId?: number | null;
    assigneeId?: number | null;
    taskName: string;
    description?: string | null;
    status?: TaskStatus;
    progressRate?: number;
    priority?: TaskPriority;
    startDate?: string | null;
    endDate?: string | null;
    sortOrder?: number;
}

/**
 * Task 수정 요청
 *
 * PUT 요청이므로 필수 필드는 반드시 전달한다.
 * 선택 필드도 원하는 최종 상태를 명시적으로 전달한다.
 */
export interface TaskUpdateRequest {
    wbsId: number;
    scheduleId: number | null;
    assigneeId: number | null;
    taskName: string;
    description: string | null;
    priority: TaskPriority;
    progressRate: number | null;
    startDate: string | null;
    endDate: string | null;
    sortOrder: number | null;
}

/**
 * Task 상태 변경 요청
 */
export interface TaskStatusChangeRequest {
    status: TaskStatus;
}

/**
 * Task 검색 조건
 */
export interface TaskSearchParams {
    projectId?: number;
    wbsId?: number;
    scheduleId?: number;
    assigneeId?: number;
    keyword?: string;
    status?: TaskStatus;
    priority?: TaskPriority;
    startDate?: string;
    endDate?: string;
    sortBy?: string;
    direction?: "ASC" | "DESC";
    page?: number;
    size?: number;
}

/**
 * Task 기간 검색 조건
 */
export interface TaskPeriodSearchParams extends TaskSearchParams {
    searchStart: string;
    searchEnd: string;
}

/**
 * Spring Page 응답
 */
export interface TaskPageResponse {
    content: Task[];
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

/**
 * Task 상태 한글 표시
 */
export const TASK_STATUS_LABELS: Record<TaskStatus, string> = {
    PLANNED: "예정",
    IN_PROGRESS: "진행 중",
    ON_HOLD: "보류",
    COMPLETED: "완료",
    CANCELLED: "취소",
};

/**
 * Task 우선순위 한글 표시
 */
export const TASK_PRIORITY_LABELS: Record<TaskPriority, string> = {
    LOW: "낮음",
    MEDIUM: "보통",
    HIGH: "높음",
    CRITICAL: "긴급",
};
