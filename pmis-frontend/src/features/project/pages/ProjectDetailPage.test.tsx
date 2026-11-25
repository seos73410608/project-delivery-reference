import { describe, expect, it, vi, beforeEach } from 'vitest';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';

import ProjectDetailPage from './ProjectDetailPage';

import { useAuth } from '@/features/auth/context/AuthProvider';

import {
    deleteProject,
    getProject,
} from '../api/projectApi';

import {
    getOperations,
    getProjectOperationsByProject,
} from '@/features/projectoperation/api/projectOperationApi';

vi.mock('react-router-dom', async (importOriginal) => {
    const actual = await importOriginal<typeof import('react-router-dom')>();

    return {
        ...actual,
        useNavigate: () => vi.fn(),
        useParams: () => ({ projectId: '1' }),
    };
});

vi.mock('@/features/auth/context/AuthProvider', () => ({
    useAuth: vi.fn(),
}));

vi.mock('../api/projectApi', () => ({
    getProject: vi.fn(),
    deleteProject: vi.fn(),
}));

vi.mock('@/features/projectoperation/api/projectOperationApi', () => ({
    createProjectOperation: vi.fn(),
    getOperations: vi.fn(),
    getProjectOperationsByProject: vi.fn(),
    updateProjectOperation: vi.fn(),
    updateProjectOperationStatus: vi.fn(),
}));

vi.mock('../components/ProjectDetail', () => ({
    default: () => <div>프로젝트 상세 컴포넌트</div>,
}));

vi.mock('@/features/projectoperation/components/ProjectOperationList', () => ({
    default: ({
        projectOperations,
        loading,
        error,
        isAdmin,
        onCreate,
    }: {
        projectOperations: { id: number; operationName: string }[];
        loading: boolean;
        error: string | null;
        isAdmin: boolean;
        onCreate: () => void;
    }) => (
        <section>
            <h3>연계 운영 서비스</h3>

            {loading && <div role="status">운영 서비스 로딩 중</div>}
            {error && <div role="alert">{error}</div>}

            {projectOperations.map((item) => (
                <div key={item.id}>{item.operationName}</div>
            ))}

            {isAdmin && (
                <button type="button" onClick={onCreate}>
                    운영 서비스 연결
                </button>
            )}
        </section>
    ),
}));

vi.mock('@/features/projectoperation/components/ProjectOperationForm', () => ({
    default: () => <div>운영 서비스 연결 폼</div>,
}));

const mockProject = {
    id: 1,
    projectCode: 'PRJ-2026-001',
    projectName: 'PMIS 구축',
    customerName: '내부',
    projectManager: '홍길동',
    description: null,
    startDate: '2026-01-01',
    endDate: '2026-12-31',
    status: 'IN_PROGRESS' as const,
    priority: 'HIGH' as const,
    createdAt: '2026-01-01T09:00:00',
    updatedAt: '2026-01-01T09:00:00',
    progressRate: 50,
    totalPlannedManDays: 100,
    weightedCompletedManDays: 50,
    missingEffortTaskCount: 0,
};

const mockProjectOperation = {
    id: 10,
    relationshipType: 'BUILD_RESULT' as const,
    status: 'ACTIVE' as const,
    projectId: 1,
    projectCode: 'PRJ-2026-001',
    projectName: 'PMIS 구축',
    customerName: '내부',
    projectManager: '홍길동',
    operationId: 20,
    operationCode: 'OPS-2026-001',
    operationName: 'PMIS 운영 서비스',
    operationType: 'APPLICATION_SERVICE' as const,
    operationStatus: 'ACTIVE' as const,
    effectiveFrom: '2026-01-01',
    effectiveTo: null,
    description: '운영 서비스 연결',
    createdAt: '2026-01-01T09:00:00',
    updatedAt: '2026-01-01T09:00:00',
};

function renderPage() {
    return render(
        <MemoryRouter initialEntries={['/project/1']}>
            <Routes>
                <Route
                    path="/project/:projectId"
                    element={<ProjectDetailPage />}
                />
            </Routes>
        </MemoryRouter>,
    );
}

describe('ProjectDetailPage', () => {
    beforeEach(() => {
        vi.clearAllMocks();

        vi.mocked(useAuth).mockReturnValue({
            user: {
                role: 'ROLE_USER',
            },
        } as ReturnType<typeof useAuth>);

        vi.mocked(getProject).mockResolvedValue(mockProject);
        vi.mocked(getProjectOperationsByProject).mockResolvedValue([]);
        vi.mocked(getOperations).mockResolvedValue([]);
        vi.mocked(deleteProject).mockResolvedValue(undefined);
    });

    it('프로젝트 상세 정보를 조회하고 연결 운영 서비스 영역을 표시한다', async () => {
        renderPage();

        expect(
            await screen.findByText('프로젝트 상세 컴포넌트'),
        ).toBeInTheDocument();

        expect(
            screen.getByText('연계 운영 서비스'),
        ).toBeInTheDocument();

        expect(getProject).toHaveBeenCalledWith(1);

        await waitFor(() => {
            expect(getProjectOperationsByProject).toHaveBeenCalledWith(1);
        });
    });

    it('프로젝트 상세 조회 실패 시 오류 메시지를 표시한다', async () => {
        vi.mocked(getProject).mockRejectedValue(
            new Error('Backend error'),
        );

        renderPage();

        expect(
            await screen.findByRole('alert'),
        ).toHaveTextContent('프로젝트 정보를 불러오지 못했습니다.');

        expect(
            screen.getByRole('button', { name: '프로젝트 목록' }),
        ).toBeInTheDocument();

        expect(getProjectOperationsByProject).not.toHaveBeenCalled();
    });

    it('연결된 운영 서비스 목록을 표시한다', async () => {
        vi.mocked(getProjectOperationsByProject).mockResolvedValue([
            mockProjectOperation,
        ]);

        renderPage();

        expect(
            await screen.findByText('PMIS 운영 서비스'),
        ).toBeInTheDocument();
    });

    it('일반 사용자에게 운영 서비스 연결 버튼을 표시하지 않는다', async () => {
        renderPage();

        await screen.findByText('연계 운영 서비스');

        expect(
            screen.queryByRole('button', { name: '운영 서비스 연결' }),
        ).not.toBeInTheDocument();
    });

    it('관리자에게 운영 서비스 연결 버튼을 표시한다', async () => {
        vi.mocked(useAuth).mockReturnValue({
            user: {
                role: 'ROLE_ADMIN',
            },
        } as ReturnType<typeof useAuth>);

        renderPage();

        expect(
            await screen.findByRole('button', { name: '운영 서비스 연결' }),
        ).toBeInTheDocument();
    });

    it('관리자가 운영 서비스 연결 버튼을 누르면 등록 폼을 표시한다', async () => {
        vi.mocked(useAuth).mockReturnValue({
            user: {
                role: 'ROLE_ADMIN',
            },
        } as ReturnType<typeof useAuth>);

        renderPage();

        fireEvent.click(
            await screen.findByRole('button', { name: '운영 서비스 연결' }),
        );

        expect(
            await screen.findByText('운영 서비스 연결 폼'),
        ).toBeInTheDocument();

        await waitFor(() => {
            expect(getOperations).toHaveBeenCalledTimes(1);
        });
    });
});