
import { describe, it, expect, vi, beforeEach } from "vitest";
import { renderHook, act, waitFor } from "@testing-library/react";
import type { PropsWithChildren } from "react";

import { AuthProvider, useAuth } from "./AuthProvider";

import {
    login,
    completeSessionSelection,
    getMe,
    logout,
} from "@/features/auth/api/authApi";

vi.mock("@/features/auth/api/authApi", () => ({
    login: vi.fn(),
    completeSessionSelection: vi.fn(),
    getMe: vi.fn(),
    refresh: vi.fn(),
    logout: vi.fn(),
}));

const testUser = {
    userId: 1,
    username: "testuser",
    name: "테스트 사용자",
    role: "ROLE_ADMIN",
};

const wrapper = ({ children }: PropsWithChildren) => (
    <AuthProvider>{children}</AuthProvider>
);

describe("AuthProvider", () => {
    beforeEach(() => {
        vi.resetAllMocks();

        vi.mocked(getMe).mockResolvedValue(testUser);
    });

    it("앱 시작 시 사용자 인증 정보를 복원한다", async () => {
        const { result } = renderHook(() => useAuth(), { wrapper });

        await waitFor(() => {
            expect(result.current.isInitializing).toBe(false);
        });

        expect(getMe).toHaveBeenCalledOnce();
        expect(result.current.user).toEqual(testUser);
        expect(result.current.isAuthenticated).toBe(true);
    });

    it("인증 복원 실패 시 비인증 상태가 된다", async () => {
        vi.mocked(getMe).mockRejectedValue(new Error("Unauthorized"));

        const { result } = renderHook(() => useAuth(), { wrapper });

        await waitFor(() => {
            expect(result.current.isInitializing).toBe(false);
        });

        expect(result.current.user).toBeNull();
        expect(result.current.isAuthenticated).toBe(false);
    });

    it("로그인 성공 시 인증 사용자 정보를 설정한다", async () => {
        vi.mocked(login).mockResolvedValue({
            status: "SUCCESS",
            user: testUser,
        });

        const { result } = renderHook(() => useAuth(), { wrapper });

        await waitFor(() => {
            expect(result.current.isInitializing).toBe(false);
        });

        await act(async () => {
            await result.current.login({
                username: "testuser",
                password: "password123",
            });
        });

        expect(result.current.user).toEqual(testUser);
        expect(result.current.isAuthenticated).toBe(true);
    });

    it("세션 선택이 필요하면 인증 상태로 전환하지 않는다", async () => {
        vi.mocked(login).mockResolvedValue({
            status: "SESSION_SELECTION_REQUIRED",
            loginAttemptId: "attempt-1",
            requiredTerminationCount: 1,
            activeSessions: [
                {
                    sessionId: "session-1",
                    createdAt: "2026-11-21T00:00:00Z",
                    lastAccessedAt: "2026-11-21T01:00:00Z",
                },
            ],
        });

        const { result } = renderHook(() => useAuth(), { wrapper });

        await waitFor(() => {
            expect(result.current.isInitializing).toBe(false);
        });

        let loginResult;

        await act(async () => {
            loginResult = await result.current.login({
                username: "testuser",
                password: "password123",
            });
        });

        expect(loginResult).toMatchObject({
            status: "SESSION_SELECTION_REQUIRED",
        });

        expect(result.current.user).toBeNull();
        expect(result.current.isAuthenticated).toBe(false);
    });

    it("세션 선택 완료 후 사용자 인증 정보를 설정한다", async () => {
        vi.mocked(completeSessionSelection).mockResolvedValue({
            status: "SUCCESS",
            user: testUser,
        });

        const { result } = renderHook(() => useAuth(), { wrapper });

        await waitFor(() => {
            expect(result.current.isInitializing).toBe(false);
        });

        await act(async () => {
            await result.current.completeSessionSelection({
                loginAttemptId: "attempt-1",
                sessionIds: ["session-1"],
            });
        });

        expect(result.current.user).toEqual(testUser);
        expect(result.current.isAuthenticated).toBe(true);
    });

    it("로그아웃 성공 시 인증 상태를 초기화한다", async () => {
        const { result } = renderHook(() => useAuth(), { wrapper });

        await waitFor(() => {
            expect(result.current.isInitializing).toBe(false);
        });

        await act(async () => {
            await result.current.logout();
        });

        expect(logout).toHaveBeenCalledOnce();
        expect(result.current.user).toBeNull();
        expect(result.current.isAuthenticated).toBe(false);
    });

    it("로그아웃 실패 시 기존 인증 상태를 유지한다", async () => {
        vi.mocked(logout).mockRejectedValue(new Error("Backend unavailable"));

        const { result } = renderHook(() => useAuth(), { wrapper });

        await waitFor(() => {
            expect(result.current.isInitializing).toBe(false);
        });

        await expect(
            act(async () => {
                await result.current.logout();
            }),
        ).rejects.toThrow("Backend unavailable");

        expect(result.current.user).toEqual(testUser);
        expect(result.current.isAuthenticated).toBe(true);
    });
});
