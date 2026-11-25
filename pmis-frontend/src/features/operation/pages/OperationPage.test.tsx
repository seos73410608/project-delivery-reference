
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';

import OperationPage from './OperationPage';

import { useAuth } from '@/features/auth/context/AuthProvider';

import {
  getOperations,
} from '../api/operationApi';

const { mockNavigate } = vi.hoisted(() => ({
  mockNavigate: vi.fn(),
}));

vi.mock('react-router-dom', async (importOriginal) => {
  const actual = await importOriginal<typeof import('react-router-dom')>();

  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

vi.mock('@/features/auth/context/AuthProvider', () => ({
  useAuth: vi.fn(),
}));

vi.mock('../api/operationApi', () => ({
  getOperations: vi.fn(),
}));

const mockOperations = [
  {
    id: 1,
    operationCode: 'OPS-2026-001',
    operationName: 'PMIS 운영 서비스',
    operationType: 'APPLICATION_SERVICE' as const,
    status: 'ACTIVE' as const,
    managerUserId: 1,
    managerName: '홍길동',
    description: 'PMIS 운영',
    effectiveFrom: '2026-01-01',
    effectiveTo: null,
    createdAt: '2026-01-01T09:00:00',
    updatedAt: '2026-01-01T09:00:00',
  },
  {
    id: 2,
    operationCode: 'OPS-2026-002',
    operationName: '인프라 운영 서비스',
    operationType: 'INFRASTRUCTURE_SERVICE' as const,
    status: 'PLANNED' as const,
    managerUserId: null,
    managerName: null,
    description: null,
    effectiveFrom: '2026-02-01',
    effectiveTo: '2026-12-31',
    createdAt: '2026-01-02T09:00:00',
    updatedAt: '2026-01-02T09:00:00',
  },
  {
    id: 3,
    operationCode: 'OPS-2026-003',
    operationName: '데이터베이스 운영 서비스',
    operationType: 'DATABASE_SERVICE' as const,
    status: 'SUSPENDED' as const,
    managerUserId: 2,
    managerName: '김철수',
    description: null,
    effectiveFrom: null,
    effectiveTo: null,
    createdAt: '2026-01-03T09:00:00',
    updatedAt: '2026-01-03T09:00:00',
  },
  {
    id: 4,
    operationCode: 'OPS-2026-004',
    operationName: '종료된 플랫폼 서비스',
    operationType: 'PLATFORM_SERVICE' as const,
    status: 'CLOSED' as const,
    managerUserId: null,
    managerName: null,
    description: null,
    effectiveFrom: null,
    effectiveTo: null,
    createdAt: '2026-01-04T09:00:00',
    updatedAt: '2026-01-04T09:00:00',
  },
];

function renderPage() {
  return render(
    <MemoryRouter>
      <OperationPage />
    </MemoryRouter>,
  );
}

describe('OperationPage', () => {
  beforeEach(() => {
    vi.clearAllMocks();

    vi.mocked(useAuth).mockReturnValue({
      user: {
        role: 'ROLE_USER',
      },
    } as ReturnType<typeof useAuth>);

    vi.mocked(getOperations).mockResolvedValue(mockOperations);
  });

  it('운영 서비스 목록을 조회하고 주요 정보를 표시한다', async () => {
    renderPage();

    expect(
      await screen.findByText('PMIS 운영 서비스'),
    ).toBeInTheDocument();

    expect(screen.getByText('OPS-2026-001')).toBeInTheDocument();
    expect(screen.getByText('홍길동')).toBeInTheDocument();
    expect(screen.getByText('인프라 운영 서비스')).toBeInTheDocument();

    expect(getOperations).toHaveBeenCalledTimes(1);
  });

  it('운영 서비스 상태별 요약 건수를 표시한다', async () => {
    renderPage();

    await screen.findByText('PMIS 운영 서비스');

    const summaryItems = screen
      .getByText('전체 운영 서비스')
      .closest('.operation-summary');

    expect(summaryItems).not.toBeNull();

    expect(summaryItems).toHaveTextContent('전체 운영 서비스4');
    expect(summaryItems).toHaveTextContent('운영 중1');
    expect(summaryItems).toHaveTextContent('계획1');
    expect(summaryItems).toHaveTextContent('중지1');
  });

  it('운영 서비스 코드로 검색한다', async () => {
    renderPage();

    await screen.findByText('PMIS 운영 서비스');

    fireEvent.change(screen.getByLabelText('운영 서비스 코드'), {
      target: { value: 'OPS-2026-002' },
    });

    fireEvent.click(screen.getByRole('button', { name: '검색' }));

    expect(screen.getByText('인프라 운영 서비스')).toBeInTheDocument();
    expect(screen.queryByText('PMIS 운영 서비스')).not.toBeInTheDocument();
    expect(screen.getByText('검색 결과 1건')).toBeInTheDocument();
  });

  it('운영 서비스명으로 검색한다', async () => {
    renderPage();

    await screen.findByText('PMIS 운영 서비스');

    fireEvent.change(screen.getByLabelText('운영 서비스명'), {
      target: { value: '데이터베이스' },
    });

    fireEvent.click(screen.getByRole('button', { name: '검색' }));

    expect(screen.getByText('데이터베이스 운영 서비스')).toBeInTheDocument();
    expect(screen.queryByText('PMIS 운영 서비스')).not.toBeInTheDocument();
  });

  it('서비스 유형으로 검색한다', async () => {
    renderPage();

    await screen.findByText('PMIS 운영 서비스');

    fireEvent.change(screen.getByLabelText('서비스 유형'), {
      target: { value: 'INFRASTRUCTURE_SERVICE' },
    });

    fireEvent.click(screen.getByRole('button', { name: '검색' }));

    expect(screen.getByText('인프라 운영 서비스')).toBeInTheDocument();
    expect(screen.queryByText('PMIS 운영 서비스')).not.toBeInTheDocument();
  });

  it('운영 상태로 검색한다', async () => {
    renderPage();

    await screen.findByText('PMIS 운영 서비스');

    fireEvent.change(screen.getByLabelText('운영 상태'), {
      target: { value: 'ACTIVE' },
    });

    fireEvent.click(screen.getByRole('button', { name: '검색' }));

    expect(screen.getByText('PMIS 운영 서비스')).toBeInTheDocument();
    expect(screen.queryByText('인프라 운영 서비스')).not.toBeInTheDocument();
  });

  it('검색 초기화 시 전체 목록을 다시 표시한다', async () => {
    renderPage();

    await screen.findByText('PMIS 운영 서비스');

    fireEvent.change(screen.getByLabelText('운영 서비스명'), {
      target: { value: '데이터베이스' },
    });

    fireEvent.click(screen.getByRole('button', { name: '검색' }));

    expect(screen.queryByText('PMIS 운영 서비스')).not.toBeInTheDocument();

    fireEvent.click(screen.getByRole('button', { name: '초기화' }));

    expect(screen.getByText('PMIS 운영 서비스')).toBeInTheDocument();
    expect(screen.getByText('인프라 운영 서비스')).toBeInTheDocument();
    expect(screen.getByText('검색 결과 4건')).toBeInTheDocument();
  });

  it('일반 사용자에게 운영 서비스 등록 버튼을 표시하지 않는다', async () => {
    renderPage();

    await screen.findByText('PMIS 운영 서비스');

    expect(
      screen.queryByRole('button', { name: '운영 서비스 등록' }),
    ).not.toBeInTheDocument();
  });

  it('관리자에게 운영 서비스 등록 버튼을 표시한다', async () => {
    vi.mocked(useAuth).mockReturnValue({
      user: {
        role: 'ROLE_ADMIN',
      },
    } as ReturnType<typeof useAuth>);

    renderPage();

    expect(
      await screen.findByRole('button', { name: '운영 서비스 등록' }),
    ).toBeInTheDocument();
  });

  it('관리자가 등록 버튼을 누르면 등록 경로로 이동한다', async () => {
    vi.mocked(useAuth).mockReturnValue({
      user: {
        role: 'ROLE_ADMIN',
      },
    } as ReturnType<typeof useAuth>);

    renderPage();

    fireEvent.click(
      await screen.findByRole('button', { name: '운영 서비스 등록' }),
    );

    expect(mockNavigate).toHaveBeenCalledWith('/operation/create');
  });

  it('운영 서비스명을 누르면 상세 경로로 이동한다', async () => {
    renderPage();

    fireEvent.click(
      await screen.findByRole('button', { name: 'PMIS 운영 서비스' }),
    );

    expect(mockNavigate).toHaveBeenCalledWith('/operation/1/detail');
  });

  it('목록 조회 실패 시 오류 메시지와 재시도 버튼을 표시한다', async () => {
    vi.mocked(getOperations).mockRejectedValueOnce(
      new Error('Backend error'),
    );

    renderPage();

    expect(await screen.findByText(
      '운영 서비스 목록을 불러오지 못했습니다.',
    )).toBeInTheDocument();

    expect(
      screen.getByRole('button', { name: '다시 시도' }),
    ).toBeInTheDocument();
  });

  it('재시도 버튼을 누르면 목록을 다시 조회한다', async () => {
    vi.mocked(getOperations)
      .mockRejectedValueOnce(new Error('Backend error'))
      .mockResolvedValueOnce(mockOperations);

    renderPage();

    fireEvent.click(
      await screen.findByRole('button', { name: '다시 시도' }),
    );

    expect(
      await screen.findByText('PMIS 운영 서비스'),
    ).toBeInTheDocument();

    await waitFor(() => {
      expect(getOperations).toHaveBeenCalledTimes(2);
    });
  });

  it('조회 결과가 없으면 빈 목록 메시지를 표시한다', async () => {
    vi.mocked(getOperations).mockResolvedValue([]);

    renderPage();

    expect(
      await screen.findByText('조회된 운영 서비스가 없습니다.'),
    ).toBeInTheDocument();

    expect(screen.getByText('검색 결과 0건')).toBeInTheDocument();
  });
});
