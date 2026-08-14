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
 * 프로젝트의 WBS 목록 조회
 *
 * GET /api/projects/{projectId}/wbs
 *
 * 검색 조건이 필요한 경우 params를 전달한다.
 */
export const getWbsList = async (
    projectId: number,
    params?: Omit<WbsSearchRequest, "projectId">,
): Promise<WbsSearchResponse> => {
    const response = await apiClient.get<WbsSearchResponse>(
        `/projects/${projectId}/wbs`,
        {
            params,
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