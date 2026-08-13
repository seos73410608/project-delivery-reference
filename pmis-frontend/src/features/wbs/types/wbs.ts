/**
 * WBS Status
 *
 * Backend WbsStatus Enum과 동일하게 정의한다.
 */
export type WbsStatus =
    | "PLANNED"
    | "IN_PROGRESS"
    | "COMPLETED"
    | "ON_HOLD"
    | "CANCELLED";


/**
 * WBS Response
 *
 * Backend WbsResponse와 대응한다.
 */
export interface WbsResponse {
    id: number;
    projectId: number;
    parentId: number | null;
    wbsCode: string;
    wbsName: string;
    level: number;
    sortOrder: number;
    status: WbsStatus;
    description: string | null;
    createdAt: string;
    updatedAt: string;
}


/**
 * WBS Tree Response
 *
 * Backend WbsTreeResponse와 대응한다.
 *
 * children을 통해 WBS의 Parent / Child 계층 구조를 표현한다.
 */
export interface WbsTreeResponse extends WbsResponse {
    children: WbsTreeResponse[];
}


/**
 * WBS Create Request
 *
 * POST /api/projects/{projectId}/wbs
 */
export interface WbsCreateRequest {
    parentId: number | null;
    wbsCode: string;
    wbsName: string;
    description: string | null;
    status: WbsStatus;
    sortOrder: number;
}


/**
 * WBS Update Request
 *
 * PUT /api/wbs/{id}
 */
export interface WbsUpdateRequest {
    parentId: number | null;
    wbsCode: string;
    wbsName: string;
    description: string | null;
    status: WbsStatus;
    sortOrder: number;
}


/**
 * WBS Search Request
 *
 * GET /api/wbs
 */
export interface WbsSearchRequest {
    projectId?: number;
    keyword?: string;
    status?: WbsStatus;
    parentId?: number;
    sortBy?: string;
    direction?: string;
    page?: number;
    size?: number;
}


/**
 * WBS Search Response
 *
 * Backend Page 형태의 WBS 검색 결과에 대응한다.
 */
export interface WbsSearchResponse {
    totalPages: number;
    totalElements: number;
    size: number;
    content: WbsResponse[];
    number: number;
    first: boolean;
    last: boolean;
    numberOfElements: number;
    empty: boolean;
}


/**
 * WBS Status Label
 *
 * 화면에서 Backend Enum을 사용자에게 표시하기 위한 Label이다.
 */
export const WBS_STATUS_LABEL: Record<WbsStatus, string> = {
    PLANNED: "계획",
    IN_PROGRESS: "진행 중",
    COMPLETED: "완료",
    ON_HOLD: "보류",
    CANCELLED: "취소",
};


/**
 * WBS Status Color
 *
 * Mock 화면 및 Status Badge에서 사용할 수 있는
 * 상태별 UI 표현 정보를 정의한다.
 *
 * 실제 디자인 시스템이 만들어지면
 * 별도 UI 상수로 이동할 수 있다.
 */
export const WBS_STATUS_COLOR: Record<WbsStatus, string> = {
    PLANNED: "#757575",
    IN_PROGRESS: "#1976d2",
    COMPLETED: "#2e7d32",
    ON_HOLD: "#ed6c02",
    CANCELLED: "#d32f2f",
};