import apiClient from "@/api/client";

import type {
    LoginRequest,
    LoginResponse,
} from "@/features/auth/types/auth";


/**
 * Backend 공통 API 응답
 *
 * Backend:
 *
 * {
 *   success: true,
 *   code: "SUCCESS",
 *   message: "로그인에 성공했습니다.",
 *   data: { ... }
 * }
 */
interface ApiResponse<T> {
    success: boolean;
    code: string;
    message: string;
    data: T;
}


/**
 * Login API
 *
 * apiClient의 baseURL:
 *   /api
 *
 * 실제 요청:
 *   POST /api/auth/login
 *
 * 로그인 성공 시 Backend에서
 * accessToken / refreshToken을 반환한다.
 */
export async function login(
    request: LoginRequest,
): Promise<LoginResponse> {
    const response = await apiClient.post<
        ApiResponse<LoginResponse>
    >(
        "/auth/login",
        request,
    );

    return response.data.data;
}