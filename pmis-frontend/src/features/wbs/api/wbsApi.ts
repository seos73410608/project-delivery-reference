import apiClient from "@/api/client";

import type {
    WbsTreeResponse,
} from "@/features/wbs/types/wbs";


/**
 * 프로젝트의 WBS Tree 조회
 *
 * GET /api/projects/{projectId}/wbs/tree
 */
export const getWbsTree = async (
    projectId: number,
): Promise<WbsTreeResponse[]> => {
    const response = await apiClient.get<WbsTreeResponse[]>(
        `/projects/${projectId}/wbs/tree`,
    );

    return response.data;
};