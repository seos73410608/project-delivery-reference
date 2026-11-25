
import {
    beforeEach,
    afterEach,
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
    Routes,
    Route,
} from "react-router-dom";

import OperationDetailPage from "./OperationDetailPage";

import {
    getOperation,
    updateOperationStatus,
} from "../api/operationApi";


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


vi.mock("../api/operationApi", () => ({
    getOperation: vi.fn(),
    updateOperationStatus: vi.fn(),
}));


vi.mock("../components/OperationDetail", () => ({
    default: ({ operation }: {
        operation: typeof mockOperation;
    }) => (
        <div data-testid="operation-detail">
            {operation.operationName}
        </div>
    ),
}));


const mockOperation = {
    id: 1,
    operationCode: "OPS-2026-001",
    operationName: "PMIS 운영 서비스",
    operationType: "APPLICATION_SERVICE" as const,
    status: "ACTIVE" as const,
    managerUserId: 10,
    managerName: "홍길동",
    description: "PMIS 운영 관리",
    effectiveFrom: "2026-01-01",
    effectiveTo: "2026-12-31",
    createdAt: "2026-01-01T09:00:00",
    updatedAt: "2026-01-02T09:00:00",
};


function renderPage(path = "/operation/1/detail") {

    return render(
        <MemoryRouter initialEntries={[path]}>
            <Routes>
                <Route
                    path="/operation/:operationId/detail"
                    element={<OperationDetailPage />}
                />
            </Routes>
        </MemoryRouter>,
    );
}


describe("OperationDetailPage", () => {

    beforeEach(() => {

        vi.clearAllMocks();

        mockUseAuth.mockReturnValue({
            user: {
                id: 1,
                role: "ROLE_ADMIN",
            },
        });

        vi.mocked(getOperation).mockResolvedValue(
            mockOperation,
        );

        vi.mocked(updateOperationStatus).mockResolvedValue(
            undefined as never,
        );
    });


    afterEach(() => {

        vi.restoreAllMocks();
    });


    it("운영 서비스 상세 정보를 조회한다", async () => {

        renderPage();

        expect(
            await screen.findByRole("heading", {
                name: "운영 서비스 상세",
            }),
        ).toBeInTheDocument();

        expect(getOperation).toHaveBeenCalledWith(1);

        expect(
            screen.getByTestId("operation-detail"),
        ).toHaveTextContent("PMIS 운영 서비스");
    });


    it("상세 정보를 조회하는 동안 로딩 메시지를 표시한다", () => {

        vi.mocked(getOperation).mockReturnValue(
            new Promise(() => {}),
        );

        renderPage();

        expect(
            screen.getByText(
                "운영 서비스 정보를 불러오는 중입니다.",
            ),
        ).toBeInTheDocument();
    });


    it("조회 실패 시 오류 메시지와 목록 이동 버튼을 표시한다", async () => {

        vi.mocked(getOperation).mockRejectedValueOnce(
            new Error("Backend error"),
        );

        renderPage();

        expect(
            await screen.findByRole("heading", {
                name: "운영 서비스 정보를 불러올 수 없습니다.",
            }),
        ).toBeInTheDocument();

        expect(
            screen.getByText(
                "운영 서비스 정보를 불러오는 중 오류가 발생했습니다.",
            ),
        ).toBeInTheDocument();

        expect(
            screen.getByRole("button", {
                name: "목록으로 돌아가기",
            }),
        ).toBeInTheDocument();

        expect(
            screen.queryByTestId("operation-detail"),
        ).not.toBeInTheDocument();
    });


    it("잘못된 운영 서비스 ID이면 API를 호출하지 않는다", async () => {

        renderPage("/operation/abc/detail");

        expect(
            await screen.findByRole("heading", {
                name: "운영 서비스 정보를 불러올 수 없습니다.",
            }),
        ).toBeInTheDocument();

        expect(
            screen.getByText(
                "올바르지 않은 운영 서비스 ID입니다.",
            ),
        ).toBeInTheDocument();

        expect(getOperation).not.toHaveBeenCalled();
    });


    it("조회 결과가 없으면 존재하지 않는 운영 서비스 메시지를 표시한다", async () => {

        vi.mocked(getOperation).mockResolvedValueOnce(
            null as never,
        );

        renderPage();

        expect(
            await screen.findByText(
                "운영 서비스 정보가 존재하지 않습니다.",
            ),
        ).toBeInTheDocument();

        expect(
            screen.queryByTestId("operation-detail"),
        ).not.toBeInTheDocument();
    });


    it("ADMIN에게 수정 버튼과 운영 상태 관리 기능을 표시한다", async () => {

        renderPage();

        await screen.findByTestId("operation-detail");

        expect(
            screen.getByRole("button", {
                name: "수정",
            }),
        ).toBeInTheDocument();

        expect(
            screen.getByRole("heading", {
                name: "운영 상태 관리",
            }),
        ).toBeInTheDocument();

        expect(
            screen.getByLabelText("운영 상태"),
        ).toBeInTheDocument();
    });


    it("USER에게 수정 버튼과 운영 상태 관리 기능을 표시하지 않는다", async () => {

        mockUseAuth.mockReturnValue({
            user: {
                id: 2,
                role: "ROLE_USER",
            },
        });

        renderPage();

        await screen.findByTestId("operation-detail");

        expect(
            screen.queryByRole("button", {
                name: "수정",
            }),
        ).not.toBeInTheDocument();

        expect(
            screen.queryByRole("heading", {
                name: "운영 상태 관리",
            }),
        ).not.toBeInTheDocument();

        expect(
            screen.getByRole("button", {
                name: "목록으로",
            }),
        ).toBeInTheDocument();
    });


    it("수정 버튼을 누르면 해당 운영 서비스 수정 화면으로 이동한다", async () => {

        renderPage();

        await screen.findByTestId("operation-detail");

        fireEvent.click(
            screen.getByRole("button", {
                name: "수정",
            }),
        );

        expect(mockNavigate).toHaveBeenCalledWith(
            "/operation/1/edit",
        );
    });


    it("목록으로 버튼을 누르면 운영 서비스 목록으로 이동한다", async () => {

        renderPage();

        await screen.findByTestId("operation-detail");

        fireEvent.click(
            screen.getByRole("button", {
                name: "목록으로",
            }),
        );

        expect(mockNavigate).toHaveBeenCalledWith(
            "/operation",
        );
    });


    it("운영 상태 변경을 확인하면 상태 변경 API를 호출하고 상세 정보를 다시 조회한다", async () => {

        const updatedOperation = {
            ...mockOperation,
            status: "SUSPENDED" as const,
        };

        vi.mocked(getOperation)
            .mockResolvedValueOnce(mockOperation)
            .mockResolvedValueOnce(updatedOperation);

        vi.spyOn(window, "confirm").mockReturnValue(true);

        renderPage();

        await screen.findByTestId("operation-detail");

        fireEvent.change(
            screen.getByLabelText("운영 상태"),
            {
                target: {
                    value: "SUSPENDED",
                },
            },
        );

        await waitFor(() => {
            expect(updateOperationStatus).toHaveBeenCalledWith(
                1,
                {
                    status: "SUSPENDED",
                },
            );
        });

        await waitFor(() => {
            expect(getOperation).toHaveBeenCalledTimes(2);
        });

        expect(
            screen.getByLabelText("운영 상태"),
        ).toHaveValue("SUSPENDED");
    });


    it("운영 상태 변경을 취소하면 API를 호출하지 않는다", async () => {

        vi.spyOn(window, "confirm").mockReturnValue(false);

        renderPage();

        await screen.findByTestId("operation-detail");

        fireEvent.change(
            screen.getByLabelText("운영 상태"),
            {
                target: {
                    value: "CLOSED",
                },
            },
        );

        expect(updateOperationStatus).not.toHaveBeenCalled();

        expect(getOperation).toHaveBeenCalledTimes(1);
    });


    it("운영 상태 변경 API 실패 시 오류 메시지를 표시한다", async () => {

        vi.spyOn(window, "confirm").mockReturnValue(true);

        vi.mocked(updateOperationStatus).mockRejectedValueOnce(
            new Error("Backend error"),
        );

        renderPage();

        await screen.findByTestId("operation-detail");

        fireEvent.change(
            screen.getByLabelText("운영 상태"),
            {
                target: {
                    value: "CLOSED",
                },
            },
        );

        expect(
            await screen.findByText(
                "운영 상태 변경 중 오류가 발생했습니다.",
            ),
        ).toBeInTheDocument();

        expect(updateOperationStatus).toHaveBeenCalledWith(
            1,
            {
                status: "CLOSED",
            },
        );
    });

});
