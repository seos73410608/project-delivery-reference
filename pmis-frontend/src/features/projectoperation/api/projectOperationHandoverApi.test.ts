
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
  acceptProjectOperationHandover,
  cancelProjectOperationHandover,
  completeProjectOperationHandover,
  createInitialHandover,
  createOperatorChangeHandover,
  createReHandover,
  getProjectOperationHandover,
  getProjectOperationHandoverHistory,
  rejectProjectOperationHandover,
  requestProjectOperationHandover,
  startProjectOperationHandover,
  submitProjectOperationHandoverForReview,
} from './projectOperationHandoverApi';

import type {
  InitialHandoverCreateRequest,
  OperatorChangeHandoverCreateRequest,
  ProjectOperationHandoverResponse,
  ReHandoverCreateRequest,
} from '../types/projectOperationHandover';

vi.mock('@/api/client', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    patch: vi.fn(),
  },
}));

const mockHandover: ProjectOperationHandoverResponse = {
  id: 1,

  handoverType: 'INITIAL',
  status: 'REQUESTED',
  sequenceNo: 1,

  projectOperation: {
    id: 1,
    relationshipType: 'BUILD_RESULT',
    status: 'ACTIVE',
  },

  project: {
    id: 1,
    projectCode: 'PRJ-2026-001',
    projectName: 'PMIS 구축',
    customerName: '내부',
    projectManager: '홍길동',
  },

  operation: {
    id: 1,
    operationCode: 'OPS-2026-001',
    operationName: 'PMIS 운영 서비스',
    operationType: 'APPLICATION_SERVICE',
    status: 'ACTIVE',
  },

  previousHandoverId: null,
  previousSequenceNo: null,
  previousHandoverType: null,

  fromParticipant: null,

  toParticipant: {
    id: 10,
    role: 'OPERATOR',
    primary: true,
    company: {
      id: 1,
      companyCode: 'COMP-001',
      companyName: '운영사',
    },
    organization: {
      id: 11,
      organizationCode: 'ORG-001',
      organizationName: '운영 조직',
    },
  },

  handedOverByUserId: 1,
  handedOverByName: '홍길동',
  acceptedByUserId: null,
  acceptedByName: null,

  plannedHandoverDate: '2026-12-01',
  requestedAt: '2026-11-26T09:00:00',
  acceptedAt: null,
  completedAt: null,

  notes: '최초 운영 인계',
  createdAt: '2026-11-26T09:00:00',
  updatedAt: '2026-11-26T09:00:00',
};

const mockHistory: ProjectOperationHandoverResponse[] = [
  mockHandover,
];

describe('projectOperationHandoverApi', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  // ============================================================
  // Handover Create
  // ============================================================

  describe('createInitialHandover', () => {
    it('최초 운영 인계를 생성한다', async () => {
      const request: InitialHandoverCreateRequest = {
        projectOperationId: 1,
        toParticipantId: 10,
        plannedHandoverDate: '2026-12-01',
        notes: '최초 운영 인계',
      };

      vi.mocked(client.post).mockResolvedValue({
        data: {
          success: true,
          code: 'SUCCESS',
          data: mockHandover,
        } satisfies ApiResponse<ProjectOperationHandoverResponse>,
      });

      const result = await createInitialHandover(request);

      expect(client.post).toHaveBeenCalledWith(
        '/project-operation-handovers/initial',
        request,
      );
      expect(result).toEqual(mockHandover);
    });
  });

  describe('createOperatorChangeHandover', () => {
    it('운영자 변경 인계를 생성한다', async () => {
      const request: OperatorChangeHandoverCreateRequest = {
        projectOperationId: 1,
        fromParticipantId: 10,
        toParticipantId: 20,
        plannedHandoverDate: '2026-12-01',
        notes: '운영자 변경',
      };

      vi.mocked(client.post).mockResolvedValue({
        data: {
          success: true,
          code: 'SUCCESS',
          data: {
            ...mockHandover,
            handoverType: 'OPERATOR_CHANGE',
          },
        } satisfies ApiResponse<ProjectOperationHandoverResponse>,
      });

      const result = await createOperatorChangeHandover(request);

      expect(client.post).toHaveBeenCalledWith(
        '/project-operation-handovers/operator-change',
        request,
      );
      expect(result.handoverType).toBe('OPERATOR_CHANGE');
    });
  });

  describe('createReHandover', () => {
    it('재인계를 생성한다', async () => {
      const request: ReHandoverCreateRequest = {
        projectOperationId: 1,
        fromParticipantId: 10,
        toParticipantId: 20,
        plannedHandoverDate: '2026-12-15',
        notes: '재인계',
      };

      vi.mocked(client.post).mockResolvedValue({
        data: {
          success: true,
          code: 'SUCCESS',
          data: {
            ...mockHandover,
            handoverType: 'RE_HANDOVER',
          },
        } satisfies ApiResponse<ProjectOperationHandoverResponse>,
      });

      const result = await createReHandover(request);

      expect(client.post).toHaveBeenCalledWith(
        '/project-operation-handovers/re-handover',
        request,
      );
      expect(result.handoverType).toBe('RE_HANDOVER');
    });
  });

  // ============================================================
  // Handover Query
  // ============================================================

  describe('getProjectOperationHandover', () => {
    it('운영 인계 상세를 조회한다', async () => {
      vi.mocked(client.get).mockResolvedValue({
        data: {
          success: true,
          code: 'SUCCESS',
          data: mockHandover,
        } satisfies ApiResponse<ProjectOperationHandoverResponse>,
      });

      const result = await getProjectOperationHandover(1);

      expect(client.get).toHaveBeenCalledWith(
        '/project-operation-handovers/1',
      );
      expect(result).toEqual(mockHandover);
    });
  });

  describe('getProjectOperationHandoverHistory', () => {
    it('프로젝트 운영 관계별 인계 이력을 조회한다', async () => {
      vi.mocked(client.get).mockResolvedValue({
        data: {
          success: true,
          code: 'SUCCESS',
          data: mockHistory,
        } satisfies ApiResponse<ProjectOperationHandoverResponse[]>,
      });

      const result = await getProjectOperationHandoverHistory(1);

      expect(client.get).toHaveBeenCalledWith(
        '/project-operation-handovers/by-project-operation/1',
      );
      expect(result).toEqual(mockHistory);
    });
  });

  // ============================================================
  // Handover Workflow
  // ============================================================

  describe('requestProjectOperationHandover', () => {
    it('운영 인계를 요청한다', async () => {
      vi.mocked(client.patch).mockResolvedValue({
        data: {
          success: true,
          code: 'SUCCESS',
          data: mockHandover,
        } satisfies ApiResponse<ProjectOperationHandoverResponse>,
      });

      const result = await requestProjectOperationHandover(1);

      expect(client.patch).toHaveBeenCalledWith(
        '/project-operation-handovers/1/request',
      );
      expect(result).toEqual(mockHandover);
    });
  });

  describe('startProjectOperationHandover', () => {
    it('운영 인계를 시작한다', async () => {
      vi.mocked(client.patch).mockResolvedValue({
        data: {
          success: true,
          code: 'SUCCESS',
          data: {
            ...mockHandover,
            status: 'IN_PROGRESS',
          },
        } satisfies ApiResponse<ProjectOperationHandoverResponse>,
      });

      const result = await startProjectOperationHandover(1);

      expect(client.patch).toHaveBeenCalledWith(
        '/project-operation-handovers/1/start',
      );
      expect(result.status).toBe('IN_PROGRESS');
    });
  });

  describe('submitProjectOperationHandoverForReview', () => {
    it('운영 인계를 검토 요청한다', async () => {
      vi.mocked(client.patch).mockResolvedValue({
        data: {
          success: true,
          code: 'SUCCESS',
          data: {
            ...mockHandover,
            status: 'REVIEW',
          },
        } satisfies ApiResponse<ProjectOperationHandoverResponse>,
      });

      const result = await submitProjectOperationHandoverForReview(1);

      expect(client.patch).toHaveBeenCalledWith(
        '/project-operation-handovers/1/submit-for-review',
      );
      expect(result.status).toBe('REVIEW');
    });
  });

  describe('acceptProjectOperationHandover', () => {
    it('운영 인계를 승인한다', async () => {
      vi.mocked(client.patch).mockResolvedValue({
        data: {
          success: true,
          code: 'SUCCESS',
          data: {
            ...mockHandover,
            status: 'ACCEPTED',
          },
        } satisfies ApiResponse<ProjectOperationHandoverResponse>,
      });

      const result = await acceptProjectOperationHandover(1);

      expect(client.patch).toHaveBeenCalledWith(
        '/project-operation-handovers/1/accept',
      );
      expect(result.status).toBe('ACCEPTED');
    });
  });

  describe('completeProjectOperationHandover', () => {
    it('운영 인계를 완료한다', async () => {
      vi.mocked(client.patch).mockResolvedValue({
        data: {
          success: true,
          code: 'SUCCESS',
          data: {
            ...mockHandover,
            status: 'COMPLETED',
          },
        } satisfies ApiResponse<ProjectOperationHandoverResponse>,
      });

      const result = await completeProjectOperationHandover(1);

      expect(client.patch).toHaveBeenCalledWith(
        '/project-operation-handovers/1/complete',
      );
      expect(result.status).toBe('COMPLETED');
    });
  });

  describe('rejectProjectOperationHandover', () => {
    it('운영 인계를 거절한다', async () => {
      vi.mocked(client.patch).mockResolvedValue({
        data: {
          success: true,
          code: 'SUCCESS',
          data: {
            ...mockHandover,
            status: 'REJECTED',
          },
        } satisfies ApiResponse<ProjectOperationHandoverResponse>,
      });

      const result = await rejectProjectOperationHandover(1);

      expect(client.patch).toHaveBeenCalledWith(
        '/project-operation-handovers/1/reject',
      );
      expect(result.status).toBe('REJECTED');
    });
  });

  describe('cancelProjectOperationHandover', () => {
    it('운영 인계를 취소한다', async () => {
      vi.mocked(client.patch).mockResolvedValue({
        data: {
          success: true,
          code: 'SUCCESS',
          data: {
            ...mockHandover,
            status: 'CANCELLED',
          },
        } satisfies ApiResponse<ProjectOperationHandoverResponse>,
      });

      const result = await cancelProjectOperationHandover(1);

      expect(client.patch).toHaveBeenCalledWith(
        '/project-operation-handovers/1/cancel',
      );
      expect(result.status).toBe('CANCELLED');
    });
  });
});
