/**
 * Login Request
 *
 * POST /api/auth/login
 */
export interface LoginRequest {
    username: string;
    password: string;
}


/**
 * Login Response Data
 *
 * POST /api/auth/login
 *
 * Backend Response:
 *
 * {
 *   userId: 1,
 *   username: "admin",
 *   name: "관리자",
 *   role: "ROLE_ADMIN",
 *   accessToken: "...",
 *   refreshToken: "...",
 *   accessTokenExpiration: 3600000,
 *   refreshTokenExpiration: 1209600000
 * }
 */
export interface LoginResponse {
    userId: number;
    username: string;
    name: string;
    role: string;
    accessToken: string;
    refreshToken: string;
    accessTokenExpiration: number;
    refreshTokenExpiration: number;
}


/**
 * Authenticated User
 *
 * Frontend에서 로그인된 사용자 정보를 관리하기 위한 타입이다.
 *
 * Token 자체는 AuthUser에 포함하지 않는다.
 * Token은 별도의 storage에서 관리한다.
 */
export interface AuthUser {
    userId: number;
    username: string;
    name: string;
    role: string;
}


/**
 * Authentication State
 *
 * Frontend 인증 상태를 표현한다.
 */
export interface AuthState {
    isAuthenticated: boolean;
    user: AuthUser | null;
    accessToken: string | null;
    refreshToken: string | null;
}