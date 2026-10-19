
import apiClient from '@/api/client';

/**
 * Runtime 실행 결과 파일
 *
 * GET /api/runtime/executions/{executionUuid}/files 응답 기준
 */
export interface RuntimeFile {
  file_id: string;
  job_id: string;
  execution_uuid: string;
  vendor_name: string;
  file_type: string;
  created_at: string;
}

/**
 * 공통 API 응답 형식
 */
interface ApiResponse<T> {
  success: boolean;
  code: string;
  message?: string;
  data: T;
}

const RUNTIME_API = '/runtime';

/**
 * 실행 UUID에 해당하는 결과 파일 목록 조회
 *
 * GET /api/runtime/executions/{executionUuid}/files
 */
export async function getExecutionFiles(
  executionUuid: string,
): Promise<RuntimeFile[]> {
  const response = await apiClient.get<ApiResponse<RuntimeFile[]>>(
    `${RUNTIME_API}/executions/${encodeURIComponent(executionUuid)}/files`,
  );

  if (!response.data.success) {
    throw new Error(
      response.data.message ?? '실행 결과 파일 목록을 조회하지 못했습니다.',
    );
  }

  return response.data.data;
}

/**
 * 결과 파일 메타데이터 조회
 *
 * GET /api/runtime/files/{fileId}
 */
export async function getRuntimeFile(
  fileId: string,
): Promise<RuntimeFile> {
  const response = await apiClient.get<ApiResponse<RuntimeFile>>(
    `${RUNTIME_API}/files/${encodeURIComponent(fileId)}`,
  );

  if (!response.data.success) {
    throw new Error(
      response.data.message ?? '파일 정보를 조회하지 못했습니다.',
    );
  }

  return response.data.data;
}

/**
 * 결과 파일 다운로드
 *
 * GET /api/runtime/files/{fileId}/content
 */
export async function downloadRuntimeFile(
  fileId: string,
): Promise<Blob> {
  const response = await apiClient.get<Blob>(
    `${RUNTIME_API}/files/${encodeURIComponent(fileId)}/content`,
    {
      responseType: 'blob',
    },
  );

  return response.data;
}
