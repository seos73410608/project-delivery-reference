
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
    Routes,
    Route,
} from "react-router-dom";

import OperationFormPage from "./OperationFormPage";

import {
    createOperation,
    getOperation,
    updateOperation,
} from "../api/operationApi";


const {
    mockNavigate,
    mockOperationForm,
} = vi.hoisted(() => ({
    mockNavigate: vi.fn(),
    mockOperationForm: vi.fn(),
}));


vi.mock("react-router-dom", async (importOriginal) => {

    const actual =
        await importOriginal<typeof import("react-router-dom")>();

    return {
        ...actual,
        useNavigate: () => mockNavigate,
    };
});


vi.mock("../api/operationApi", () => ({
    createOperation: vi.fn(),
    getOperation: vi.fn(),
    updateOperation: vi.fn(),
}));


vi.mock("../components/OperationForm", () => ({
    default: (props: Record<string, unknown>) => {

        mockOperationForm(props);

        return (
            <div data-testid="operation-form">
                OperationForm Mock
            </div>
        );
    },
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


function renderPage(path = "/operation/create") {

    return render(
        <MemoryRouter initialEntries={[path]}>
            <Routes>
                <Route
                    path="/operation/create"
                    element={<OperationFormPage />}
                />

                <Route
                    path="/operation/:operationId/edit"
                    element={<OperationFormPage />}
                />
            </Routes>
        </MemoryRouter>,
    );
}


function getFormProps() {

    const calls = mockOperationForm.mock.calls;

    return calls[calls.length - 1][0] as {
        mode: "create" | "edit";
        operation: typeof mockOperation | null;
        onCreate: (request: {
            operationCode: string;
            operationName: string;
            operationType: "APPLICATION_SERVICE";
        }) => Promise<void>;
        onUpdate: (request: {
            operationName: string;
            operationType: "APPLICATION_SERVICE";
        }) => Promise<void>;
        onCancel: () => void;
    };
}


describe("OperationFormPage", () => {

    beforeEach(() => {

        vi.clearAllMocks();

        vi.mocked(getOperation).mockResolvedValue(
            mockOperation,
        );

        vi.mocked(createOperation).mockResolvedValue({
            ...mockOperation,
            id: 2,
        });

        vi.mocked(updateOperation).mockResolvedValue(
            mockOperation,
        );
    });


    it("신규 등록 화면을 표시한다", async () => {

        renderPage();

        expect(
            screen.getByRole("heading", {
                name: "운영 서비스 등록",
            }),
        ).toBeInTheDocument();

        expect(
            screen.getByTestId("operation-form"),
        ).toBeInTheDocument();

        expect(getFormProps().mode).toBe("create");

        expect(getFormProps().operation).toBeNull();

        expect(getOperation).not.toHaveBeenCalled();
    });


    it("수정 화면에서 기존 운영 서비스 정보를 조회한다", async () => {

        renderPage("/operation/1/edit");

        expect(
            await screen.findByRole("heading", {
                name: "운영 서비스 수정",
            }),
        ).toBeInTheDocument();

        await waitFor(() => {
            expect(getOperation).toHaveBeenCalledWith(1);
        });

        expect(getFormProps().mode).toBe("edit");

        expect(getFormProps().operation).toEqual(
            mockOperation,
        );
    });


    it("신규 등록 성공 시 상세 화면으로 이동한다", async () => {

        renderPage();

        await screen.findByTestId("operation-form");

        await getFormProps().onCreate({
            operationCode: "OPS-2026-002",
            operationName: "신규 운영 서비스",
            operationType: "APPLICATION_SERVICE",
        });

        expect(createOperation).toHaveBeenCalledWith({
            operationCode: "OPS-2026-002",
            operationName: "신규 운영 서비스",
            operationType: "APPLICATION_SERVICE",
        });

        expect(mockNavigate).toHaveBeenCalledWith(
            "/operation/2/detail",
        );
    });


    it("운영 서비스 수정 성공 시 상세 화면으로 이동한다", async () => {

        renderPage("/operation/1/edit");

        await screen.findByRole("heading", {
            name: "운영 서비스 수정",
        });

        await waitFor(() => {
            expect(getOperation).toHaveBeenCalledWith(1);
        });

        await getFormProps().onUpdate({
            operationName: "수정된 PMIS 운영 서비스",
            operationType: "APPLICATION_SERVICE",
        });

        expect(updateOperation).toHaveBeenCalledWith(
            1,
            {
                operationName: "수정된 PMIS 운영 서비스",
                operationType: "APPLICATION_SERVICE",
            },
        );

        expect(mockNavigate).toHaveBeenCalledWith(
            "/operation/1/detail",
        );
    });


    it("신규 등록 화면에서 취소하면 목록으로 이동한다", async () => {

        renderPage();

        await screen.findByTestId("operation-form");

        getFormProps().onCancel();

        expect(mockNavigate).toHaveBeenCalledWith(
            "/operation",
        );
    });


    it("수정 화면에서 취소하면 해당 상세 화면으로 이동한다", async () => {

        renderPage("/operation/1/edit");

        await screen.findByRole("heading", {
            name: "운영 서비스 수정",
        });

        await waitFor(() => {
            expect(getOperation).toHaveBeenCalledWith(1);
        });

        getFormProps().onCancel();

        expect(mockNavigate).toHaveBeenCalledWith(
            "/operation/1/detail",
        );
    });


    it("수정 대상 조회 실패 시 오류 메시지를 표시한다", async () => {

        vi.mocked(getOperation).mockRejectedValueOnce(
            new Error("Backend error"),
        );

        renderPage("/operation/1/edit");

        expect(
            await screen.findByText(
                "운영 서비스 정보를 불러오지 못했습니다.",
            ),
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
            screen.queryByTestId("operation-form"),
        ).not.toBeInTheDocument();
    });


    it("조회 실패 후 목록으로 돌아가기 버튼을 누르면 목록으로 이동한다", async () => {

        vi.mocked(getOperation).mockRejectedValueOnce(
            new Error("Backend error"),
        );

        renderPage("/operation/1/edit");

        fireEvent.click(
            await screen.findByRole("button", {
                name: "목록으로 돌아가기",
            }),
        );

        expect(mockNavigate).toHaveBeenCalledWith(
            "/operation",
        );
    });


    it("조회 결과가 없으면 운영 서비스 정보를 찾을 수 없다고 표시한다", async () => {

        vi.mocked(getOperation).mockResolvedValueOnce(
            null as never,
        );

        renderPage("/operation/1/edit");

        expect(
            await screen.findByText(
                "운영 서비스 정보를 찾을 수 없습니다.",
            ),
        ).toBeInTheDocument();

        expect(
            screen.queryByTestId("operation-form"),
        ).not.toBeInTheDocument();
    });

});
