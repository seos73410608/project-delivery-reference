import client from '@/api/client';

import type {
  IssueCreateRequest,
  IssueResponse,
  IssueSearchParams,
  IssueStatusUpdateRequest,
  IssueUpdateRequest,
  PageResponse,
} from '../types/issue';


const ISSUE_API = '/issues';

const PROJECT_API = '/projects';


/**
 * =====================================================
 * Project별 Issue 목록 조회
 * =====================================================
 *
 * GET /api/projects/{projectId}/issues
 *
 * Backend Response:
 *
 * [
 *   {
 *     id,
 *     projectId,
 *     issueKey,
 *     title,
 *     description,
 *     status,
 *     priority,
 *     assigneeId,
 *     reporterId,
 *     occurredDate,
 *     dueDate,
 *     resolvedDate,
 *     sortOrder,
 *     createdAt,
 *     updatedAt
 *   }
 * ]
 */
export const getProjectIssues = async (
  projectId: number,
): Promise<IssueResponse[]> => {

  const response =
    await client.get<IssueResponse[]>(
      `${PROJECT_API}/${projectId}/issues`,
    );


  return response.data;

};


/**
 * =====================================================
 * Issue 검색
 * =====================================================
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
 *
 * Example:
 *
 * GET
 * /api/issues
 * ?projectId=1
 * &status=OPEN
 * &priority=HIGH
 * &page=0
 * &size=20
 */
export const searchIssues = async (
  params: IssueSearchParams,
): Promise<PageResponse<IssueResponse>> => {

  const response =
    await client.get<PageResponse<IssueResponse>>(
      ISSUE_API,
      {
        params,
      },
    );


  return response.data;

};


/**
 * =====================================================
 * Issue 단건 조회
 * =====================================================
 *
 * GET /api/issues/{id}
 *
 * Backend Response:
 *
 * {
 *   id,
 *   projectId,
 *   issueKey,
 *   title,
 *   description,
 *   status,
 *   priority,
 *   assigneeId,
 *   reporterId,
 *   occurredDate,
 *   dueDate,
 *   resolvedDate,
 *   sortOrder,
 *   createdAt,
 *   updatedAt
 * }
 */
export const getIssue = async (
  issueId: number,
): Promise<IssueResponse> => {

  const response =
    await client.get<IssueResponse>(
      `${ISSUE_API}/${issueId}`,
    );


  return response.data;

};


/**
 * =====================================================
 * Issue 생성
 * =====================================================
 *
 * POST /api/projects/{projectId}/issues
 *
 * Issue는 반드시 특정 Project에 소속된다.
 */
export const createIssue = async (
  projectId: number,
  request: IssueCreateRequest,
): Promise<IssueResponse> => {

  const response =
    await client.post<IssueResponse>(
      `${PROJECT_API}/${projectId}/issues`,
      request,
    );


  return response.data;

};


/**
 * =====================================================
 * Issue 수정
 * =====================================================
 *
 * PUT /api/issues/{id}
 *
 * Project는 변경하지 않는다.
 */
export const updateIssue = async (
  issueId: number,
  request: IssueUpdateRequest,
): Promise<IssueResponse> => {

  const response =
    await client.put<IssueResponse>(
      `${ISSUE_API}/${issueId}`,
      request,
    );


  return response.data;

};


/**
 * =====================================================
 * Issue 상태 변경
 * =====================================================
 *
 * PATCH /api/issues/{id}/status
 *
 * Backend에서 상태 전이 규칙을 검증한다.
 *
 * Example:
 *
 * OPEN
 *   ↓
 * IN_PROGRESS
 *   ↓
 * RESOLVED
 *   ↓
 * CLOSED
 */
export const changeIssueStatus = async (
  issueId: number,
  request: IssueStatusUpdateRequest,
): Promise<IssueResponse> => {

  const response =
    await client.patch<IssueResponse>(
      `${ISSUE_API}/${issueId}/status`,
      request,
    );


  return response.data;

};


/**
 * =====================================================
 * Issue 삭제
 * =====================================================
 *
 * DELETE /api/issues/{id}
 *
 * Backend Response:
 *
 * 204 No Content
 */
export const deleteIssue = async (
  issueId: number,
): Promise<void> => {

  await client.delete(
    `${ISSUE_API}/${issueId}`,
  );

};