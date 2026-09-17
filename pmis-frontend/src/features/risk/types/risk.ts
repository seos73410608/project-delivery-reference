/**
 * Risk API 공통 응답
 */
export interface ApiResponse<T> {

    success: boolean;

    code: string;

    message: string;

    data: T;
}


/**
 * Risk Status
 */
export type RiskStatus =
    | 'OPEN'
    | 'IN_PROGRESS'
    | 'MITIGATED'
    | 'CLOSED'
    | 'ACCEPTED'
    | 'CANCELLED';


/**
 * Risk Priority
 */
export type RiskPriority =
    | 'LOW'
    | 'MEDIUM'
    | 'HIGH'
    | 'CRITICAL';


/**
 * Risk Probability
 */
export type RiskProbability =
    | 'LOW'
    | 'MEDIUM'
    | 'HIGH';


/**
 * Risk Impact
 */
export type RiskImpact =
    | 'LOW'
    | 'MEDIUM'
    | 'HIGH'
    | 'CRITICAL';


/**
 * Risk Status Label
 */
export const RISK_STATUS_LABEL: Record<
    RiskStatus,
    string
> = {
    OPEN: 'Open',
    IN_PROGRESS: 'In Progress',
    MITIGATED: 'Mitigated',
    CLOSED: 'Closed',
    ACCEPTED: 'Accepted',
    CANCELLED: 'Cancelled',
};


/**
 * Risk Response
 */
export interface RiskResponse {

    /**
     * Risk PK
     */
    id: number;


    /**
     * 소속 Project ID
     */
    projectId: number;


    /**
     * Risk 식별 Key
     *
     * Backend에서 생성한다.
     */
    riskKey?: string | null;


    /**
     * Risk 제목
     */
    title: string;


    /**
     * Risk 상세 내용
     */
    description: string | null;


    /**
     * Risk 상태
     */
    status: RiskStatus;


    /**
     * Risk 중요도
     */
    priority: RiskPriority;


    /**
     * 발생 가능성
     */
    probability: RiskProbability;


    /**
     * 영향도
     */
    impact: RiskImpact;


    /**
     * Risk Score
     *
     * Probability × Impact를 기반으로
     * Backend에서 계산된 Risk Score
     */
    riskScore: number;


    /**
     * 담당자 ID
     */
    assigneeId: number | null;


    /**
     * 등록자 ID
     */
    reporterId: number | null;


    /**
     * 최초 식별일
     */
    identifiedDate: string | null;


    /**
     * 조치 목표일
     */
    dueDate: string | null;


    /**
     * Risk 완화일
     */
    mitigatedDate: string | null;


    /**
     * 대응 계획
     */
    responsePlan: string | null;


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
 * Risk 생성 요청
 *
 * POST
 * /api/projects/{projectId}/risks
 */
export interface RiskCreateRequest {

    /**
     * Risk 제목
     */
    title: string;


    /**
     * Risk 상세 내용
     */
    description?: string;


    /**
     * Risk 상태
     */
    status: RiskStatus;


    /**
     * Risk 중요도
     */
    priority: RiskPriority;


    /**
     * 발생 가능성
     */
    probability: RiskProbability;


    /**
     * 영향도
     */
    impact: RiskImpact;


    /**
     * 담당자 ID
     */
    assigneeId?: number;


    /**
     * 최초 식별일
     */
    identifiedDate?: string;


    /**
     * 조치 목표일
     */
    dueDate?: string;


    /**
     * 대응 계획
     */
    responsePlan?: string;


    /**
     * 표시 순서
     */
    sortOrder?: number;
}


/**
 * Risk 수정 요청
 *
 * PUT
 * /api/risks/{id}
 */
export interface RiskUpdateRequest {

    /**
     * Risk 제목
     */
    title: string;


    /**
     * Risk 상세 내용
     */
    description?: string;


    /**
     * Risk 상태
     */
    status: RiskStatus;


    /**
     * Risk 중요도
     */
    priority: RiskPriority;


    /**
     * 발생 가능성
     */
    probability: RiskProbability;


    /**
     * 영향도
     */
    impact: RiskImpact;


    /**
     * 담당자 ID
     */
    assigneeId?: number;


    /**
     * 최초 식별일
     */
    identifiedDate?: string;


    /**
     * 조치 목표일
     */
    dueDate?: string;


    /**
     * 대응 계획
     */
    responsePlan?: string;


    /**
     * 표시 순서
     */
    sortOrder?: number;
}


/**
 * Risk 상태 변경 요청
 *
 * PATCH
 * /api/risks/{id}/status
 */
export interface RiskStatusUpdateRequest {

    /**
     * 변경할 Risk 상태
     */
    status: RiskStatus;

}


/**
 * Risk 검색 조건
 *
 * GET /api/risks
 *
 * Query Parameters:
 *
 * - projectId
 * - keyword
 * - status
 * - priority
 * - probability
 * - impact
 * - assigneeId
 * - page
 * - size
 * - sortBy
 * - direction
 */
export interface RiskSearchParams {

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
     * Risk 상태
     */
    status?: RiskStatus;


    /**
     * Risk 중요도
     */
    priority?: RiskPriority;


    /**
     * 발생 가능성
     */
    probability?: RiskProbability;


    /**
     * 영향도
     */
    impact?: RiskImpact;


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