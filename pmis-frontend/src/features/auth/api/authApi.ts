
import apiClient from "@/api/client";

import type {
    ActiveSession,
    AuthUser,
    LoginRequest,
    LoginResult,
    SessionSelectionRequest,
} from "@/features/auth/types/auth";

/**
 * BFF 공통 API 응답
 */
interface ApiResponse<T> {
    success: boolean;
    code: string;
    message: string;
    data: T;
}

/**
 * BFF 로그인 API 응답 데이터
 */
interface LoginApiData {
    user?: AuthUser;
    status?: "SESSION_SELECTION_REQUIRED";
    loginAttemptId?: string;
    requiredTerminationCount?: number;
    activeSessions?: ActiveSession[];
}

/**
 * BFF 로그인 API 응답
 */
type LoginApiResponse = ApiResponse<LoginApiData>;

/**
 * BFF 응답을 Frontend LoginResult로 변환
 */
function toLoginResult(response: LoginApiResponse): LoginResult {
    if (response.code === "SESSION_SELECTION_REQUIRED") {
        const {
            loginAttemptId,
            requiredTerminationCount,
            activeSessions,
        } = response.data;

        if (
            !loginAttemptId ||
            typeof requiredTerminationCount !== "number" ||
            !Array.isArray(activeSessions)
        ) {
            throw new Error("세션 선택 응답 형식이 올바르지 않습니다.");
        }

        return {
            status: "SESSION_SELECTION_REQUIRED",
            loginAttemptId,
            requiredTerminationCount,
            activeSessions,
        };
    }

    if (response.code === "SUCCESS") {
        const { user } = response.data;

        if (!user) {
            throw new Error("로그인 사용자 정보가 없습니다.");
        }

        return {
            status: "SUCCESS",
            user,
        };
    }

    throw new Error("지원하지 않는 로그인 응답입니다.");
}

/**
 * Login API
 *
 * POST /api/auth/login
 */
export async function login(
    request: LoginRequest,
): Promise<LoginResult> {
    const response = await apiClient.post<LoginApiResponse>(
        "/auth/login",
        request,
    );

    return toLoginResult(response.data);
}

/**
 * Complete Session Selection API
 *
 * POST /api/auth/login/session-selection
 */
export async function completeSessionSelection(
    request: SessionSelectionRequest,
): Promise<LoginResult> {
    const response = await apiClient.post<LoginApiResponse>(
        "/auth/login/session-selection",
        request,
    );

    return toLoginResult(response.data);
}

/**
 * Get Current Authenticated User API
 *
 * GET /api/auth/me
 */
export async function getMe(): Promise<AuthUser> {
    const response = await apiClient.get<ApiResponse<AuthUser>>(
        "/auth/me",
    );

    return response.data.data;
}

/**
 * Refresh Session API
 *
 * POST /api/auth/refresh
 *
 * 토큰은 BFF가 관리한다.
 * Frontend에는 토큰을 반환하지 않는다.
 */
export async function refresh(): Promise<void> {
    await apiClient.post("/auth/refresh");
}

/**
 * Logout API
 *
 * POST /api/auth/logout
 *
 * BFF 로그아웃 성공 후에만
 * 호출 측에서 인증 상태를 초기화한다.
 */
export async function logout(): Promise<void> {
    await apiClient.post("/auth/logout");
}
