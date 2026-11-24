import { beforeEach, describe, expect, it, vi } from 'vitest';

import client from '@/api/client';

import {
  createProject,
  deleteProject,
  getProject,
  getProjects,
  updateProject,
} from './projectApi';

import type {
  ApiResponse,
  PageResponse,
  Project,
  ProjectDetail,
} from '../types/project';

vi.mock('@/api/client', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}));

const mockProject: Project = {
  id: 1,
  projectCode: 'PRJ-2026-001',
  projectName: 'PMIS 구축',
  customerName: '내부',
  projectManager: '홍길동',
  description: null,
  startDate: '2026-01-01',
  endDate: '2026-12-31',
  status: 'IN_PROGRESS',
  priority: 'HIGH',
  createdAt: '2026-01-01T09:00:00',
  updatedAt: '2026-01-02T09:00:00',
};

const mockProjectDetail: ProjectDetail = {
  ...mockProject,
  progressRate: 35.5,
  totalPlannedManDays: 100,
  weightedCompletedManDays: 35.5,
  missingEffortTaskCount: 2,
};

const mockPageResponse: PageResponse<Project> = {
  content: [mockProject],
  pageable: {
    pageNumber: 0,
    pageSize: 10,
    sort: {
      empty: true,
      sorted: false,
      unsorted: true,
    },
    offset: 0,
    paged: true,
    unpaged: false,
  },
  last: true,
  totalElements: 1,
  totalPages: 1,
  size: 10,
  number: 0,
  sort: {
    empty: true,
    sorted: false,
    unsorted: true,
  },
  first: true,
  numberOfElements: 1,
  empty: false,
};

describe('projectApi', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('getProjects', () => {
    it('프로젝트 목록을 조회하고 data를 반환한다', async () => {
      const params = {
        projectName: 'PMIS',
        page: 0,
        size: 10,
      };

      vi.mocked(client.get).mockResolvedValue({
        data: {
          success: true,
          code: 'SUCCESS',
          data: mockPageResponse,
        } satisfies ApiResponse<PageResponse<Project>>,
      });

      const result = await getProjects(params);

      expect(client.get).toHaveBeenCalledWith(
        '/projects',
        { params },
      );
      expect(result).toEqual(mockPageResponse);
    });
  });

  describe('getProject', () => {
    it('프로젝트 상세를 조회하고 data를 반환한다', async () => {
      vi.mocked(client.get).mockResolvedValue({
        data: {
          success: true,
          code: 'SUCCESS',
          data: mockProjectDetail,
        } satisfies ApiResponse<ProjectDetail>,
      });

      const result = await getProject(1);

      expect(client.get).toHaveBeenCalledWith(
        '/projects/1/detail',
      );
      expect(result).toEqual(mockProjectDetail);
    });
  });

  describe('createProject', () => {
    it('프로젝트를 생성하고 생성 결과를 반환한다', async () => {
      const request = {
        projectCode: 'PRJ-2026-002',
        projectName: '신규 프로젝트',
        customerName: '내부',
        projectManager: '김철수',
        startDate: '2026-02-01',
        endDate: '2026-12-31',
        priority: 'MEDIUM',
      } as const;

      vi.mocked(client.post).mockResolvedValue({
        data: {
          success: true,
          code: 'SUCCESS',
          data: mockProject,
        } satisfies ApiResponse<Project>,
      });

      const result = await createProject(request);

      expect(client.post).toHaveBeenCalledWith(
        '/projects',
        request,
      );
      expect(result).toEqual(mockProject);
    });
  });

  describe('updateProject', () => {
    it('프로젝트를 수정하고 수정 결과를 반환한다', async () => {
      const request = {
        projectName: '수정된 프로젝트',
        customerName: '내부',
        projectManager: '홍길동',
        description: '수정 내용',
        startDate: '2026-01-01',
        endDate: '2026-12-31',
        status: 'IN_PROGRESS' as const,
        priority: 'HIGH' as const,
      };

      vi.mocked(client.put).mockResolvedValue({
        data: {
          success: true,
          code: 'SUCCESS',
          data: mockProject,
        } satisfies ApiResponse<Project>,
      });

      const result = await updateProject(1, request);

      expect(client.put).toHaveBeenCalledWith(
        '/projects/1',
        request,
      );
      expect(result).toEqual(mockProject);
    });
  });

  describe('deleteProject', () => {
    it('프로젝트를 삭제하고 응답 데이터를 반환하지 않는다', async () => {
      vi.mocked(client.delete).mockResolvedValue({
        data: {
          success: true,
          code: 'SUCCESS',
          data: null,
        } satisfies ApiResponse<null>,
      });

      const result = await deleteProject(1);

      expect(client.delete).toHaveBeenCalledWith(
        '/projects/1',
      );
      expect(result).toBeUndefined();
    });
  });
});