
import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";

import LoginForm from "./LoginForm";
import { useAuth } from "@/features/auth/context/AuthProvider";

const { mockNavigate, mockLogin, mockCompleteSessionSelection } = vi.hoisted(() => ({
    mockNavigate: vi.fn(),
    mockLogin: vi.fn(),
    mockCompleteSessionSelection: vi.fn(),
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
    useAuth: vi.fn(),
}));

describe("LoginForm", () => {
    const mockedUseAuth = vi.mocked(useAuth);

    const activeSessions = [
        {
            sessionId: "session-1",
            createdAt: "2026-11-20T09:00:00",
            lastAccessedAt: "2026-11-20T10:00:00",
        },
        {
            sessionId: "session-2",
            createdAt: "2026-11-20T08:00:00",
            lastAccessedAt: "2026-11-20T09:30:00",
        },
    ];

    beforeEach(() => {
        vi.clearAllMocks();

        mockLogin.mockReset();
        mockCompleteSessionSelection.mockReset();

        mockedUseAuth.mockReturnValue({
            isInitializing: false,
            isAuthenticated: false,
            user: null,
            login: mockLogin,
            completeSessionSelection: mockCompleteSessionSelection,
            logout: vi.fn(),
        });
    });

    const renderForm = () =>
        render(
            <MemoryRouter>
                <LoginForm />
            </MemoryRouter>,
        );

    it("아이디를 입력하지 않으면 오류 메시지를 표시한다", async () => {
        const user = userEvent.setup();

        renderForm();

        await user.type(
            screen.getByLabelText("Password"),
            "password123",
        );

        await user.click(
            screen.getByRole("button", { name: "로그인" }),
        );

        expect(screen.getByRole("alert")).toHaveTextContent(
            "아이디를 입력해주세요.",
        );

        expect(mockLogin).not.toHaveBeenCalled();
    });

    it("비밀번호를 입력하지 않으면 오류 메시지를 표시한다", async () => {
        const user = userEvent.setup();

        renderForm();

        await user.type(
            screen.getByLabelText("Username"),
            "testuser",
        );

        await user.click(
            screen.getByRole("button", { name: "로그인" }),
        );

        expect(screen.getByRole("alert")).toHaveTextContent(
            "비밀번호를 입력해주세요.",
        );

        expect(mockLogin).not.toHaveBeenCalled();
    });

    it("로그인 성공 시 메인으로 이동한다", async () => {
        const user = userEvent.setup();

        mockLogin.mockResolvedValue({
            status: "SUCCESS",
            user: {
                userId: 1,
                username: "testuser",
                name: "테스트 사용자",
                role: "ROLE_ADMIN",
            },
        });

        renderForm();

        await user.type(
            screen.getByLabelText("Username"),
            "testuser",
        );

        await user.type(
            screen.getByLabelText("Password"),
            "password123",
        );

        await user.click(
            screen.getByRole("button", { name: "로그인" }),
        );

        expect(mockLogin).toHaveBeenCalledWith({
            username: "testuser",
            password: "password123",
        });

        expect(mockNavigate).toHaveBeenCalledWith("/", {
            replace: true,
        });
    });

    it("세션 선택이 필요하면 활성 세션 목록을 표시한다", async () => {
        const user = userEvent.setup();

        mockLogin.mockResolvedValue({
            status: "SESSION_SELECTION_REQUIRED",
            loginAttemptId: "attempt-1",
            requiredTerminationCount: 1,
            activeSessions,
        });

        renderForm();

        await user.type(
            screen.getByLabelText("Username"),
            "testuser",
        );

        await user.type(
            screen.getByLabelText("Password"),
            "password123",
        );

        await user.click(
            screen.getByRole("button", { name: "로그인" }),
        );

        expect(await screen.findByText("기존 로그인 세션 선택")).toBeInTheDocument();
        expect(screen.getByText(/종료할 세션을 1개 선택/)).toBeInTheDocument();

        expect(screen.getByLabelText(/session-1/)).toBeInTheDocument();
        expect(screen.getByLabelText(/session-2/)).toBeInTheDocument();

        expect(mockNavigate).not.toHaveBeenCalled();
    });

    it("필요한 세션을 선택하면 선택한 세션을 종료하고 로그인한다", async () => {
        const user = userEvent.setup();

        mockLogin.mockResolvedValue({
            status: "SESSION_SELECTION_REQUIRED",
            loginAttemptId: "attempt-1",
            requiredTerminationCount: 1,
            activeSessions,
        });

        mockCompleteSessionSelection.mockResolvedValue({
            status: "SUCCESS",
            user: {
                userId: 1,
                username: "testuser",
                name: "테스트 사용자",
                role: "ROLE_ADMIN",
            },
        });

        renderForm();

        await user.type(
            screen.getByLabelText("Username"),
            "testuser",
        );

        await user.type(
            screen.getByLabelText("Password"),
            "password123",
        );

        await user.click(
            screen.getByRole("button", { name: "로그인" }),
        );

        await user.click(
            await screen.findByLabelText(/session-1/),
        );

        await user.click(
            screen.getByRole("button", { name: "선택한 세션 종료 후 로그인" }),
        );

        expect(mockCompleteSessionSelection).toHaveBeenCalledWith({
            loginAttemptId: "attempt-1",
            sessionIds: ["session-1"],
        });

        expect(mockNavigate).toHaveBeenCalledWith("/", {
            replace: true,
        });
    });

    it("로그인 실패 시 오류 메시지를 표시한다", async () => {
        const user = userEvent.setup();

        mockLogin.mockRejectedValue(
            new Error("Unauthorized"),
        );

        renderForm();

        await user.type(
            screen.getByLabelText("Username"),
            "testuser",
        );

        await user.type(
            screen.getByLabelText("Password"),
            "wrong-password",
        );

        await user.click(
            screen.getByRole("button", { name: "로그인" }),
        );

        expect(await screen.findByRole("alert")).toHaveTextContent(
            "로그인에 실패했습니다. 아이디 또는 비밀번호를 확인해주세요.",
        );

        expect(mockNavigate).not.toHaveBeenCalled();
    });

    it("세션 선택 완료 실패 시 오류 메시지를 표시한다", async () => {
        const user = userEvent.setup();

        mockLogin.mockResolvedValue({
            status: "SESSION_SELECTION_REQUIRED",
            loginAttemptId: "attempt-1",
            requiredTerminationCount: 1,
            activeSessions,
        });

        mockCompleteSessionSelection.mockRejectedValue(
            new Error("Session selection failed"),
        );

        renderForm();

        await user.type(
            screen.getByLabelText("Username"),
            "testuser",
        );

        await user.type(
            screen.getByLabelText("Password"),
            "password123",
        );

        await user.click(
            screen.getByRole("button", { name: "로그인" }),
        );

        await user.click(
            await screen.findByLabelText(/session-1/),
        );

        await user.click(
            screen.getByRole("button", { name: "선택한 세션 종료 후 로그인" }),
        );

        expect(await screen.findByRole("alert")).toHaveTextContent(
            "세션 선택 처리에 실패했습니다. 다시 시도해주세요.",
        );

        expect(mockNavigate).not.toHaveBeenCalled();
    });
});
