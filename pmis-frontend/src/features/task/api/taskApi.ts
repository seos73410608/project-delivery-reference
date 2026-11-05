
import apiClient from "@/api/client";

import type {
    Task,
    TaskCreateRequest,
    TaskPageResponse,
    TaskPeriodSearchParams,
    TaskSearchParams,
    TaskStatusChangeRequest,
    TaskUpdateRequest,
} from "../types/task";

/**
 * Task 단건 조회
 *
 * GET /api/tasks/{id}
 */
export const getTask = async (id: number): Promise<Task> => {
    const response = await apiClient.get<Task>(`/tasks/${id}`);

    return response.data;
};

/**
 * Project별 Task 목록 조회
 *
 * GET /api/projects/{projectId}/tasks
 */
export const getProjectTasks = async (
    projectId: number,
): Promise<Task[]> => {
    const response = await apiClient.get<Task[]>(
        `/projects/${projectId}/tasks`,
    );

    return response.data;
};

/**
 * WBS별 Task 목록 조회
 *
 * GET /api/wbs/{wbsId}/tasks
 */
export const getWbsTasks = async (
    wbsId: number,
): Promise<Task[]> => {
    const response = await apiClient.get<Task[]>(
        `/wbs/${wbsId}/tasks`,
    );

    return response.data;
};

/**
 * Schedule별 Task 목록 조회
 *
 * GET /api/schedules/{scheduleId}/tasks
 */
export const getScheduleTasks = async (
    scheduleId: number,
): Promise<Task[]> => {
    const response = await apiClient.get<Task[]>(
        `/schedules/${scheduleId}/tasks`,
    );

    return response.data;
};

/**
 * 담당자별 Task 목록 조회
 *
 * GET /api/users/{assigneeId}/tasks
 */
export const getAssigneeTasks = async (
    assigneeId: number,
): Promise<Task[]> => {
    const response = await apiClient.get<Task[]>(
        `/users/${assigneeId}/tasks`,
    );

    return response.data;
};

/**
 * Task 조건 검색
 *
 * GET /api/tasks
 */
export const searchTasks = async (
    params: TaskSearchParams,
): Promise<TaskPageResponse> => {
    const response = await apiClient.get<TaskPageResponse>(
        "/tasks",
        {
            params,
        },
    );

    return response.data;
};

/**
 * Task 기간 조건 검색
 *
 * GET /api/tasks/period
 */
export const searchTasksByPeriod = async (
    params: TaskPeriodSearchParams,
): Promise<TaskPageResponse> => {
    const response = await apiClient.get<TaskPageResponse>(
        "/tasks/period",
        {
            params,
        },
    );

    return response.data;
};

/**
 * Task 생성
 *
 * POST /api/projects/{projectId}/tasks
 */
export const createTask = async (
    projectId: number,
    request: TaskCreateRequest,
): Promise<Task> => {
    const response = await apiClient.post<Task>(
        `/projects/${projectId}/tasks`,
        request,
    );

    return response.data;
};

/**
 * Task 수정
 *
 * PUT /api/tasks/{id}
 */
export const updateTask = async (
    id: number,
    request: TaskUpdateRequest,
): Promise<Task> => {
    const response = await apiClient.put<Task>(
        `/tasks/${id}`,
        request,
    );

    return response.data;
};

/**
 * Task 상태 변경
 *
 * PATCH /api/tasks/{id}/status
 */
export const changeTaskStatus = async (
    id: number,
    request: TaskStatusChangeRequest,
): Promise<Task> => {
    const response = await apiClient.patch<Task>(
        `/tasks/${id}/status`,
        request,
    );

    return response.data;
};

/**
 * Task 삭제
 *
 * DELETE /api/tasks/{id}
 *
 * 성공 시 HTTP 204 No Content
 */
export const deleteTask = async (
    id: number,
): Promise<void> => {
    await apiClient.delete(`/tasks/${id}`);
};
