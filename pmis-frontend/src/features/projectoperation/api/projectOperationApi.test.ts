
import {
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from 'vitest';

import client from '@/api/client';

import type {
  ApiResponse,
} from '@/features/project/types/project';

import {
  createProjectOperation,
  getOperation,
  getOperations,
  getProjectOperation,
  getProjectOperationsByOperation,
  getProjectOperationsByProject,
  updateProjectOperation,
  updateProjectOperationStatus,
} from './projectOperationApi';

import type {
  OperationResponse,
  ProjectOperationCreateRequest,
  ProjectOperationResponse,
  ProjectOperationStatusChangeRequest,
  ProjectOperationUpdateRequest,
} from '../types/projectOperation';

vi.mock('@/api/client', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    patch: vi.fn(),
  },
}));

const mockOperation: OperationResponse = {
  id: 1,
  operationCode: 'OPS-2026-001',
  operationName: 'PMIS 운영 서비스',
  operationType: 'APPLICATION_SERVICE',
  status: 'ACTIVE',
  managerUserId: 1,
  managerName: '홍길동',
  description: 'PMIS 운영 서비스',
  effectiveFrom: '2026-01-01',
  effectiveTo: '2026-12-31',
  createdAt: '2026-01-01T09:00:00',
  updatedAt: '2026-01-02T09:00:00',
};

const mockProjectOperation: ProjectOperationResponse = {
  id: 1,

  relationshipType: 'BUILD_RESULT',
  status: 'ACTIVE',

  projectId: 1,
  projectCode: 'PRJ-2026-001',
  projectName: 'PMIS 구축',
  customerName: '내부',
  projectManager: '홍길동',

  operationId: 1,
  operationCode: 'OPS-2026-001',
  operationName: 'PMIS 운영 서비스',
  operationType: 'APPLICATION_SERVICE',
  operationStatus: 'ACTIVE',

  effectiveFrom: '2026-01-01',
  effectiveTo: '2026-12-31',

  description: '프로젝트 구축 결과 운영 서비스',

  createdAt: '2026-01-01T09:00:00',
  updatedAt: '2026-01-02T09:00:00',
};

describe('projectOperationApi', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  // ============================================================
  // Operation
  // ============================================================

  describe('getOperations', () => {
    it('전체 운영 서비스 목록을 조회한다', async () => {
      vi.mocked(client.get).mockResolvedValue({
        data: {
          success: true,
          code: 'SUCCESS',
          data: [mockOperation],
        } satisfies ApiResponse<OperationResponse[]>,
      });

      const result = await getOperations();

      expect(client.get).toHaveBeenCalledWith('/operations');
      expect(result).toEqual([mockOperation]);
    });
  });

  describe('getOperation', () => {
    it('운영 서비스 상세를 조회한다', async () => {
      vi.mocked(client.get).mockResolvedValue({
        data: {
          success: true,
          code: 'SUCCESS',
          data: mockOperation,
        } satisfies ApiResponse<OperationResponse>,
      });

      const result = await getOperation(1);

      expect(client.get).toHaveBeenCalledWith('/operations/1');
      expect(result).toEqual(mockOperation);
    });
  });

  // ============================================================
  // ProjectOperation Query
  // ============================================================

  describe('getProjectOperation', () => {
    it('프로젝트 운영 관계 상세를 조회한다', async () => {
      vi.mocked(client.get).mockResolvedValue({
        data: {
          success: true,
          code: 'SUCCESS',
          data: mockProjectOperation,
        } satisfies ApiResponse<ProjectOperationResponse>,
      });

      const result = await getProjectOperation(1);

      expect(client.get).toHaveBeenCalledWith(
        '/project-operations/1',
      );
      expect(result).toEqual(mockProjectOperation);
    });
  });

  describe('getProjectOperationsByProject', () => {
    it('프로젝트 기준 운영 관계 목록을 조회한다', async () => {
      vi.mocked(client.get).mockResolvedValue({
        data: {
          success: true,
          code: 'SUCCESS',
          data: [mockProjectOperation],
        } satisfies ApiResponse<ProjectOperationResponse[]>,
      });

      const result = await getProjectOperationsByProject(1);

      expect(client.get).toHaveBeenCalledWith(
        '/project-operations/by-project/1',
      );
      expect(result).toEqual([mockProjectOperation]);
    });
  });

  describe('getProjectOperationsByOperation', () => {
    it('운영 서비스 기준 프로젝트 관계 목록을 조회한다', async () => {
      vi.mocked(client.get).mockResolvedValue({
        data: {
          success: true,
          code: 'SUCCESS',
          data: [mockProjectOperation],
        } satisfies ApiResponse<ProjectOperationResponse[]>,
      });

      const result = await getProjectOperationsByOperation(1);

      expect(client.get).toHaveBeenCalledWith(
        '/project-operations/by-operation/1',
      );
      expect(result).toEqual([mockProjectOperation]);
    });
  });

  // ============================================================
  // ProjectOperation Create
  // ============================================================

  describe('createProjectOperation', () => {
    it('프로젝트 운영 관계를 생성한다', async () => {
      const request: ProjectOperationCreateRequest = {
        projectId: 1,
        operationId: 1,
        relationshipType: 'BUILD_RESULT',
        effectiveFrom: '2026-01-01',
        effectiveTo: '2026-12-31',
        description: '프로젝트 구축 결과',
      };

      vi.mocked(client.post).mockResolvedValue({
        data: {
          success: true,
          code: 'SUCCESS',
          data: mockProjectOperation,
        } satisfies ApiResponse<ProjectOperationResponse>,
      });

      const result = await createProjectOperation(request);

      expect(client.post).toHaveBeenCalledWith(
        '/project-operations',
        request,
      );
      expect(result).toEqual(mockProjectOperation);
    });
  });

  // ============================================================
  // ProjectOperation Update
  // ============================================================

  describe('updateProjectOperation', () => {
    it('프로젝트 운영 관계를 수정한다', async () => {
      const request: ProjectOperationUpdateRequest = {
        relationshipType: 'ENHANCEMENT',
        effectiveFrom: '2026-02-01',
        effectiveTo: '2026-12-31',
        description: '운영 서비스 개선',
      };

      vi.mocked(client.put).mockResolvedValue({
        data: {
          success: true,
          code: 'SUCCESS',
          data: mockProjectOperation,
        } satisfies ApiResponse<ProjectOperationResponse>,
      });

      const result = await updateProjectOperation(1, request);

      expect(client.put).toHaveBeenCalledWith(
        '/project-operations/1',
        request,
      );
      expect(result).toEqual(mockProjectOperation);
    });
  });

  // ============================================================
  // ProjectOperation Status
  // ============================================================

  describe('updateProjectOperationStatus', () => {
    it('프로젝트 운영 관계 상태를 변경한다', async () => {
      const request: ProjectOperationStatusChangeRequest = {
        status: 'COMPLETED',
      };

      vi.mocked(client.patch).mockResolvedValue({
        data: {
          success: true,
          code: 'SUCCESS',
          data: {
            ...mockProjectOperation,
            status: 'COMPLETED',
          },
        } satisfies ApiResponse<ProjectOperationResponse>,
      });

      const result = await updateProjectOperationStatus(1, request);

      expect(client.patch).toHaveBeenCalledWith(
        '/project-operations/1/status',
        request,
      );

      expect(result.status).toBe('COMPLETED');
    });
  });
});
