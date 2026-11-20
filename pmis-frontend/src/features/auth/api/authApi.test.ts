
import { describe, it, expect, vi, beforeEach } from "vitest";

import apiClient from "@/api/client";

import {
    login,
    completeSessionSelection,
    getMe,
    refresh,
    logout,
} from "./authApi";

vi.mock("@/api/client", () => ({
    default: {
        post: vi.fn(),
        get: vi.fn(),
    },
}));

describe("authApi", () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });

    it("로그인 성공 응답을 Frontend LoginResult로 변환한다", async () => {
        const user = {
            userId: 1,
            username: "testuser",
            name: "테스트 사용자",
            role: "ROLE_ADMIN",
        };

        const result = {
            status: "SUCCESS",
            user,
        };

        vi.mocked(apiClient.post).mockResolvedValue({
            data: {
                success: true,
                code: "SUCCESS",
                message: "로그인에 성공했습니다.",
                data: {
                    user,
                },
            },
        });

        await expect(
            login({
                username: "testuser",
                password: "password123",
            }),
        ).resolves.toEqual(result);

        expect(apiClient.post).toHaveBeenCalledWith(
            "/auth/login",
            {
                username: "testuser",
                password: "password123",
            },
        );
    });

    it("기존 세션 선택이 필요한 로그인 응답을 변환한다", async () => {
        const result = {
            status: "SESSION_SELECTION_REQUIRED",
            loginAttemptId: "attempt-id",
            requiredTerminationCount: 1,
            activeSessions: [
                {
                    sessionId: "session-id",
                    createdAt: "2026-11-21T00:00:00",
                    lastAccessedAt: "2026-11-21T00:10:00",
                },
            ],
        };

        vi.mocked(apiClient.post).mockResolvedValue({
            data: {
                success: true,
                code: "SESSION_SELECTION_REQUIRED",
                message: "로그인할 세션을 선택해야 합니다.",
                data: {
                    status: "SESSION_SELECTION_REQUIRED",
                    loginAttemptId: "attempt-id",
                    requiredTerminationCount: 1,
                    activeSessions: [
                        {
                            sessionId: "session-id",
                            createdAt: "2026-11-21T00:00:00",
                            lastAccessedAt: "2026-11-21T00:10:00",
                        },
                    ],
                },
            },
        });

        await expect(
            login({
                username: "testuser",
                password: "password123",
            }),
        ).resolves.toEqual(result);

        expect(apiClient.post).toHaveBeenCalledWith(
            "/auth/login",
            {
                username: "testuser",
                password: "password123",
            },
        );
    });

    it("기존 세션 선택 후 로그인 성공 응답을 변환한다", async () => {
        const user = {
            userId: 1,
            username: "testuser",
            name: "테스트 사용자",
            role: "ROLE_ADMIN",
        };

        const result = {
            status: "SUCCESS",
            user,
        };

        const request = {
            loginAttemptId: "attempt-id",
            sessionIds: ["session-id"],
        };

        vi.mocked(apiClient.post).mockResolvedValue({
            data: {
                success: true,
                code: "SUCCESS",
                message: "세션 선택 및 로그인이 완료되었습니다.",
                data: {
                    user,
                },
            },
        });

        await expect(
            completeSessionSelection(request),
        ).resolves.toEqual(result);

        expect(apiClient.post).toHaveBeenCalledWith(
            "/auth/login/session-selection",
            request,
        );
    });

    it("세션 선택 응답 데이터가 누락되면 오류를 반환한다", async () => {
        vi.mocked(apiClient.post).mockResolvedValue({
            data: {
                success: true,
                code: "SESSION_SELECTION_REQUIRED",
                message: "로그인할 세션을 선택해야 합니다.",
                data: {
                    status: "SESSION_SELECTION_REQUIRED",
                    loginAttemptId: "attempt-id",
                },
            },
        });

        await expect(
            login({
                username: "testuser",
                password: "password123",
            }),
        ).rejects.toThrow(
            "세션 선택 응답 형식이 올바르지 않습니다.",
        );
    });

    it("로그인 성공 응답에 사용자 정보가 없으면 오류를 반환한다", async () => {
        vi.mocked(apiClient.post).mockResolvedValue({
            data: {
                success: true,
                code: "SUCCESS",
                message: "로그인에 성공했습니다.",
                data: {},
            },
        });

        await expect(
            login({
                username: "testuser",
                password: "password123",
            }),
        ).rejects.toThrow(
            "로그인 사용자 정보가 없습니다.",
        );
    });

    it("지원하지 않는 로그인 응답 코드는 오류를 반환한다", async () => {
        vi.mocked(apiClient.post).mockResolvedValue({
            data: {
                success: true,
                code: "UNKNOWN_CODE",
                message: "알 수 없는 응답입니다.",
                data: {},
            },
        });

        await expect(
            login({
                username: "testuser",
                password: "password123",
            }),
        ).rejects.toThrow(
            "지원하지 않는 로그인 응답입니다.",
        );
    });

    it("현재 인증 사용자 정보를 조회한다", async () => {
        const user = {
            userId: 1,
            username: "testuser",
            name: "테스트 사용자",
            role: "ROLE_ADMIN",
        };

        vi.mocked(apiClient.get).mockResolvedValue({
            data: {
                success: true,
                code: "SUCCESS",
                message: "인증 사용자 조회에 성공했습니다.",
                data: user,
            },
        });

        await expect(getMe()).resolves.toEqual(user);

        expect(apiClient.get).toHaveBeenCalledWith("/auth/me");
    });

    it("세션 갱신 API를 호출한다", async () => {
        vi.mocked(apiClient.post).mockResolvedValue({
            data: {
                success: true,
                code: "SUCCESS",
                message: "토큰이 갱신되었습니다.",
            },
        });

        await expect(refresh()).resolves.toBeUndefined();

        expect(apiClient.post).toHaveBeenCalledWith(
            "/auth/refresh",
        );
    });

    it("로그아웃 API를 호출한다", async () => {
        vi.mocked(apiClient.post).mockResolvedValue({
            data: {
                success: true,
                code: "SUCCESS",
                message: "로그아웃에 성공했습니다.",
            },
        });

        await expect(logout()).resolves.toBeUndefined();

        expect(apiClient.post).toHaveBeenCalledWith(
            "/auth/logout",
        );
    });
});
