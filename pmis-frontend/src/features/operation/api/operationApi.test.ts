
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
  createOperation,
  getOperation,
  getOperationByCode,
  getOperationParticipants,
  getOperations,
  updateOperation,
  updateOperationStatus,
} from './operationApi';

import type {
  OperationCreateRequest,
  OperationParticipantResponse,
  OperationResponse,
  OperationStatusChangeRequest,
  OperationUpdateRequest,
} from '../types/operation';

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

const mockOperationParticipants: OperationParticipantResponse[] = [
  {
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
  {
    id: 20,
    role: 'SERVICE_PROVIDER',
    primary: false,
    company: {
      id: 2,
      companyCode: 'COMP-002',
      companyName: '서비스 제공사',
    },
    organization: null,
  },
];

describe('operationApi', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  // ============================================================
  // Operation Query
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
    it('ID 기준 운영 서비스 상세를 조회한다', async () => {
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

  describe('getOperationByCode', () => {
    it('코드 기준 운영 서비스 상세를 조회한다', async () => {
      vi.mocked(client.get).mockResolvedValue({
        data: {
          success: true,
          code: 'SUCCESS',
          data: mockOperation,
        } satisfies ApiResponse<OperationResponse>,
      });

      const result = await getOperationByCode('OPS-2026-001');

      expect(client.get).toHaveBeenCalledWith(
        '/operations/by-code/OPS-2026-001',
      );
      expect(result).toEqual(mockOperation);
    });

    it('운영 서비스 코드의 URL 특수문자를 인코딩한다', async () => {
      vi.mocked(client.get).mockResolvedValue({
        data: {
          success: true,
          code: 'SUCCESS',
          data: mockOperation,
        } satisfies ApiResponse<OperationResponse>,
      });

      await getOperationByCode('OPS/2026 001');

      expect(client.get).toHaveBeenCalledWith(
        '/operations/by-code/OPS%2F2026%20001',
      );
    });
  });

  // ============================================================
  // Operation Participants
  // ============================================================

  describe('getOperationParticipants', () => {
    it('인계 가능한 운영 참여자 목록을 조회한다', async () => {
      vi.mocked(client.get).mockResolvedValue({
        data: {
          success: true,
          code: 'SUCCESS',
          data: mockOperationParticipants,
        } satisfies ApiResponse<OperationParticipantResponse[]>,
      });

      const result = await getOperationParticipants(1);

      expect(client.get).toHaveBeenCalledWith(
        '/operations/1/participants',
        {
          params: undefined,
        },
      );
      expect(result).toEqual(mockOperationParticipants);
    });

    it('인계 예정일을 전달하여 유효한 운영 참여자 목록을 조회한다', async () => {
      vi.mocked(client.get).mockResolvedValue({
        data: {
          success: true,
          code: 'SUCCESS',
          data: mockOperationParticipants,
        } satisfies ApiResponse<OperationParticipantResponse[]>,
      });

      const result = await getOperationParticipants(
        1,
        '2026-12-01',
      );

      expect(client.get).toHaveBeenCalledWith(
        '/operations/1/participants',
        {
          params: {
            plannedHandoverDate: '2026-12-01',
          },
        },
      );
      expect(result).toEqual(mockOperationParticipants);
    });
  });

  // ============================================================
  // Operation Create
  // ============================================================

  describe('createOperation', () => {
    it('운영 서비스를 등록한다', async () => {
      const request: OperationCreateRequest = {
        operationCode: 'OPS-2026-001',
        operationName: 'PMIS 운영 서비스',
        operationType: 'APPLICATION_SERVICE',
        managerUserId: 1,
        description: 'PMIS 운영 서비스',
        effectiveFrom: '2026-01-01',
        effectiveTo: '2026-12-31',
      };

      vi.mocked(client.post).mockResolvedValue({
        data: {
          success: true,
          code: 'SUCCESS',
          data: mockOperation,
        } satisfies ApiResponse<OperationResponse>,
      });

      const result = await createOperation(request);

      expect(client.post).toHaveBeenCalledWith(
        '/operations',
        request,
      );
      expect(result).toEqual(mockOperation);
    });
  });

  // ============================================================
  // Operation Update
  // ============================================================

  describe('updateOperation', () => {
    it('운영 서비스 정보를 수정한다', async () => {
      const request: OperationUpdateRequest = {
        operationName: 'PMIS 운영 서비스 수정',
        operationType: 'APPLICATION_SERVICE',
        managerUserId: 1,
        description: '운영 서비스 정보 수정',
        effectiveFrom: '2026-02-01',
        effectiveTo: '2026-12-31',
      };

      vi.mocked(client.put).mockResolvedValue({
        data: {
          success: true,
          code: 'SUCCESS',
          data: mockOperation,
        } satisfies ApiResponse<OperationResponse>,
      });

      const result = await updateOperation(1, request);

      expect(client.put).toHaveBeenCalledWith(
        '/operations/1',
        request,
      );
      expect(result).toEqual(mockOperation);
    });
  });

  // ============================================================
  // Operation Status
  // ============================================================

  describe('updateOperationStatus', () => {
    it('운영 서비스 상태를 변경한다', async () => {
      const request: OperationStatusChangeRequest = {
        status: 'SUSPENDED',
      };

      const updatedOperation: OperationResponse = {
        ...mockOperation,
        status: 'SUSPENDED',
      };

      vi.mocked(client.patch).mockResolvedValue({
        data: {
          success: true,
          code: 'SUCCESS',
          data: updatedOperation,
        } satisfies ApiResponse<OperationResponse>,
      });

      const result = await updateOperationStatus(1, request);

      expect(client.patch).toHaveBeenCalledWith(
        '/operations/1/status',
        request,
      );
      expect(result.status).toBe('SUSPENDED');
    });
  });
});
