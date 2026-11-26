
import {
    beforeEach,
    describe,
    expect,
    it,
    vi,
} from "vitest";

import {
    fireEvent,
    render,
    screen,
    waitFor,
} from "@testing-library/react";

import {
    MemoryRouter,
    Route,
    Routes,
} from "react-router-dom";

import ProjectOperationHandoverPage from "./ProjectOperationHandoverPage";

import {
    getProjectOperation,
} from "../api/projectOperationApi";

import {
    getOperationParticipants,
} from "@/features/operation/api/operationApi";

import {
    createOperatorChangeHandover,
    getProjectOperationHandoverHistory,
} from "../api/projectOperationHandoverApi";


const {
    mockNavigate,
    mockUseAuth,
} = vi.hoisted(() => ({
    mockNavigate: vi.fn(),
    mockUseAuth: vi.fn(),
}));


vi.mock("react-router-dom", async (importOriginal) => {

    const actual =
        await importOriginal<typeof import("react-router-dom")>();

    return {
        ...actual,
        useNavigate: () => mockNavigate,
    };
});


vi.mock("@/features/auth/context/AuthProvider", () => ({
    useAuth: mockUseAuth,
}));


vi.mock("../api/projectOperationApi", () => ({
    getProjectOperation: vi.fn(),
}));


vi.mock("@/features/operation/api/operationApi", () => ({
    getOperationParticipants: vi.fn(),
}));


vi.mock("../api/projectOperationHandoverApi", () => ({
    createInitialHandover: vi.fn(),
    createOperatorChangeHandover: vi.fn(),
    createReHandover: vi.fn(),
    getProjectOperationHandover: vi.fn(),
    getProjectOperationHandoverHistory: vi.fn(),
    requestProjectOperationHandover: vi.fn(),
    startProjectOperationHandover: vi.fn(),
    submitProjectOperationHandoverForReview: vi.fn(),
    acceptProjectOperationHandover: vi.fn(),
    completeProjectOperationHandover: vi.fn(),
    rejectProjectOperationHandover: vi.fn(),
    cancelProjectOperationHandover: vi.fn(),
}));


const mockProjectOperation = {
    id: 10,
    relationshipType: "BUILD_RESULT" as const,
    status: "ACTIVE" as const,
    projectId: 1,
    projectCode: "PRJ-2026-001",
    projectName: "차세대 PMIS 구축",
    customerName: "고객사",
    projectManager: "홍길동",
    operationId: 20,
    operationCode: "OPS-2026-001",
    operationName: "PMIS 운영 서비스",
    operationType: "APPLICATION_SERVICE" as const,
    operationStatus: "ACTIVE" as const,
    effectiveFrom: "2026-01-01",
    effectiveTo: null,
    description: null,
    createdAt: "2026-01-01T09:00:00",
    updatedAt: "2026-01-02T09:00:00",
};


const mockParticipant = {
    id: 100,
    role: "OPERATOR" as const,
    primary: true,
    company: {
        id: 1,
        companyCode: "COMP-001",
        companyName: "운영사",
    },
    organization: {
        id: 2,
        organizationCode: "ORG-001",
        organizationName: "운영팀",
    },
};


const mockNextParticipant = {
    id: 101,
    role: "SERVICE_PROVIDER" as const,
    primary: false,
    company: {
        id: 3,
        companyCode: "COMP-002",
        companyName: "신규 운영사",
    },
    organization: {
        id: 4,
        organizationCode: "ORG-002",
        organizationName: "신규 운영팀",
    },
};


const mockHandover = {
    id: 1000,
    handoverType: "INITIAL" as const,
    status: "COMPLETED" as const,
    sequenceNo: 1,
    projectOperation: {
        id: 10,
        relationshipType: "BUILD_RESULT",
        status: "ACTIVE",
    },
    project: {
        id: 1,
        projectCode: "PRJ-2026-001",
        projectName: "차세대 PMIS 구축",
        customerName: "고객사",
        projectManager: "홍길동",
    },
    operation: {
        id: 20,
        operationCode: "OPS-2026-001",
        operationName: "PMIS 운영 서비스",
        operationType: "APPLICATION_SERVICE",
        status: "ACTIVE",
    },
    previousHandoverId: null,
    previousSequenceNo: null,
    previousHandoverType: null,
    fromParticipant: null,
    toParticipant: mockParticipant,
    handedOverByUserId: 1,
    handedOverByName: "관리자",
    acceptedByUserId: 2,
    acceptedByName: "운영 담당자",
    plannedHandoverDate: "2026-11-30",
    requestedAt: "2026-11-20T09:00:00",
    acceptedAt: "2026-11-25T09:00:00",
    completedAt: "2026-11-26T09:00:00",
    notes: "최초 운영 인계",
    createdAt: "2026-11-20T09:00:00",
    updatedAt: "2026-11-26T09:00:00",
};


function renderPage(
    path = "/project/1/operations/10/handovers",
) {

    return render(
        <MemoryRouter initialEntries={[path]}>
            <Routes>
                <Route
                    path="/project/:projectId/operations/:projectOperationId/handovers"
                    element={<ProjectOperationHandoverPage />}
                />
            </Routes>
        </MemoryRouter>,
    );
}


describe("ProjectOperationHandoverPage", () => {

    beforeEach(() => {

        vi.clearAllMocks();

        mockUseAuth.mockReturnValue({
            user: {
                userId: 1,
                username: "admin",
                name: "관리자",
                role: "ROLE_ADMIN",
            },
        });

        vi.mocked(getProjectOperation).mockResolvedValue(
            mockProjectOperation,
        );

        vi.mocked(getProjectOperationHandoverHistory).mockResolvedValue(
            [mockHandover],
        );

        vi.mocked(getOperationParticipants).mockResolvedValue(
            [mockParticipant, mockNextParticipant],
        );

        vi.mocked(createOperatorChangeHandover).mockResolvedValue(
            mockHandover,
        );
    });


    it("프로젝트 운영 관계와 인수인계 이력을 조회한다", async () => {

        renderPage();

        expect(
            await screen.findByRole("heading", {
                name: "운영 인수인계 이력",
            }),
        ).toBeInTheDocument();

        expect(getProjectOperation).toHaveBeenCalledWith(10);

        expect(
            await screen.findByText("차세대 PMIS 구축 (PRJ-2026-001)"),
        ).toBeInTheDocument();

        expect(
            screen.getByText("PMIS 운영 서비스 (OPS-2026-001)"),
        ).toBeInTheDocument();

        expect(
            screen.getAllByText("최초 인계"),
        ).toHaveLength(2);

        expect(
            screen.getByText("운영사 / 운영팀 / 운영자 / 대표", {
                selector: "dd",
            }),
        ).toBeInTheDocument();

        expect(
            getProjectOperationHandoverHistory,
        ).toHaveBeenCalledWith(10);

    });


    it("USER는 인수인계 이력을 조회할 수 있지만 등록 기능은 표시하지 않는다", async () => {

        mockUseAuth.mockReturnValue({
            user: {
                userId: 2,
                username: "user",
                name: "일반 사용자",
                role: "ROLE_USER",
            },
        });

        renderPage();

        expect(
            await screen.findByRole("heading", {
                name: "운영 인수인계 이력",
            }),
        ).toBeInTheDocument();

        expect(
            screen.getByRole("heading", {
                name: "인수인계 이력",
            }),
        ).toBeInTheDocument();

        expect(
            screen.queryByRole("heading", {
                name: "인수인계 등록",
            }),
        ).not.toBeInTheDocument();

        expect(
            screen.queryByRole("button", {
                name: "인수인계 등록",
            }),
        ).not.toBeInTheDocument();

        expect(getOperationParticipants).not.toHaveBeenCalled();

    });


    it("ADMIN은 완료된 최초 인계 이후 운영자 변경을 등록할 수 있다", async () => {

        renderPage();

        expect(
            await screen.findByRole("heading", {
                name: "인수인계 등록",
            }),
        ).toBeInTheDocument();

        expect(
            await screen.findByRole("option", {
                name: "신규 운영사 / 신규 운영팀 / 서비스 제공자",
            }),
        ).toBeInTheDocument();

        fireEvent.change(
            screen.getByLabelText("인계 유형"),
            {
                target: {
                    value: "OPERATOR_CHANGE",
                },
            },
        );

        fireEvent.change(
            screen.getByLabelText("인계하는 참여자"),
            {
                target: {
                    value: "100",
                },
            },
        );

        fireEvent.change(
            screen.getByLabelText("인계받는 참여자"),
            {
                target: {
                    value: "101",
                },
            },
        );

        fireEvent.change(
            screen.getByLabelText("비고"),
            {
                target: {
                    value: "운영자 변경 등록 테스트",
                },
            },
        );

        fireEvent.click(
            screen.getByRole("button", {
                name: "인수인계 등록",
            }),
        );

        await waitFor(() => {

            expect(createOperatorChangeHandover).toHaveBeenCalledWith({
                projectOperationId: 10,
                plannedHandoverDate: null,
                notes: "운영자 변경 등록 테스트",
                fromParticipantId: 100,
                toParticipantId: 101,
            });

        });

    });


    it("프로젝트 ID가 운영 관계의 프로젝트 ID와 다르면 이력을 조회하지 않는다", async () => {

        renderPage("/project/999/operations/10/handovers");

        expect(
            await screen.findByRole("heading", {
                name: "인수인계 정보를 불러올 수 없습니다.",
            }),
        ).toBeInTheDocument();

        expect(
            screen.getByText(
                "요청한 프로젝트에 연결된 운영 서비스가 아닙니다.",
            ),
        ).toBeInTheDocument();

        expect(getProjectOperation).toHaveBeenCalledWith(10);

        expect(
            getProjectOperationHandoverHistory,
        ).not.toHaveBeenCalled();

    });


    it("잘못된 프로젝트 운영 관계 ID이면 API를 호출하지 않는다", async () => {

        renderPage("/project/1/operations/abc/handovers");

        expect(
            await screen.findByRole("heading", {
                name: "인수인계 정보를 불러올 수 없습니다.",
            }),
        ).toBeInTheDocument();

        expect(
            screen.getByText(
                "올바르지 않은 프로젝트 또는 운영 관계 ID입니다.",
            ),
        ).toBeInTheDocument();

        expect(getProjectOperation).not.toHaveBeenCalled();

        expect(
            getProjectOperationHandoverHistory,
        ).not.toHaveBeenCalled();

    });

});
