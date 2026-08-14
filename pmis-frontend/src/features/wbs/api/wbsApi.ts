import apiClient from "@/api/client";

import type {
    WbsCreateRequest,
    WbsResponse,
    WbsSearchRequest,
    WbsSearchResponse,
    WbsTreeResponse,
    WbsUpdateRequest,
} from "@/features/wbs/types/wbs";


/**
 * 프로젝트의 WBS Tree 조회
 *
 * GET /api/projects/{projectId}/wbs/tree
 *
 * 예:
 * GET /api/projects/2/wbs/tree
 */
export const getWbsTree = async (
    projectId: number,
): Promise<WbsTreeResponse[]> => {
    const response = await apiClient.get<WbsTreeResponse[]>(
        `/projects/${projectId}/wbs/tree`,
    );

    return response.data;
};


/**
 * WBS 검색
 *
 * GET /api/wbs
 *
 * Query Parameter:
 * - projectId
 * - keyword
 * - status
 * - parentId
 * - sortBy
 * - direction
 * - page
 * - size
 *
 * 예:
 * GET /api/wbs?projectId=2&keyword=일정
 *     &status=IN_PROGRESS&page=0&size=20
 */
export const searchWbs = async (
    request: WbsSearchRequest,
): Promise<WbsSearchResponse> => {
    const response = await apiClient.get<WbsSearchResponse>(
        "/wbs",
        {
            params: {
                projectId: request.projectId,
                keyword: request.keyword,
                status: request.status,
                parentId: request.parentId,
                sortBy: request.sortBy,
                direction: request.direction,
                page: request.page,
                size: request.size,
            },
        },
    );

    return response.data;
};


/**
 * WBS 단건 조회
 *
 * GET /api/wbs/{id}
 */
export const getWbs = async (
    id: number,
): Promise<WbsResponse> => {
    const response = await apiClient.get<WbsResponse>(
        `/wbs/${id}`,
    );

    return response.data;
};


/**
 * WBS 생성
 *
 * POST /api/projects/{projectId}/wbs
 */
export const createWbs = async (
    projectId: number,
    request: WbsCreateRequest,
): Promise<WbsResponse> => {
    const response = await apiClient.post<WbsResponse>(
        `/projects/${projectId}/wbs`,
        request,
    );

    return response.data;
};


/**
 * WBS 수정
 *
 * PUT /api/wbs/{id}
 */
export const updateWbs = async (
    id: number,
    request: WbsUpdateRequest,
): Promise<WbsResponse> => {
    const response = await apiClient.put<WbsResponse>(
        `/wbs/${id}`,
        request,
    );

    return response.data;
};


/**
 * WBS 삭제
 *
 * DELETE /api/wbs/{id}
 */
export const deleteWbs = async (
    id: number,
): Promise<void> => {
    await apiClient.delete(
        `/wbs/${id}`,
    );
};