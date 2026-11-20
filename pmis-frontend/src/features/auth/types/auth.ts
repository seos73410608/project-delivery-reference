
/**
 * BFF Login Request
 *
 * POST /api/auth/login
 */
export interface LoginRequest {
    username: string;
    password: string;
}

/**
 * Authenticated User
 *
 * BFF가 인증된 사용자에게 제공하는 정보.
 * Access Token / Refresh Token은 포함하지 않는다.
 */
export interface AuthUser {
    userId: number;
    username: string;
    name: string;
    role: string;
}

/**
 * Active Backend Session
 */
export interface ActiveSession {
    sessionId: string;
    createdAt: string;
    lastAccessedAt: string;
}

/**
 * Login Success
 */
export interface LoginSuccess {
    status: "SUCCESS";
    user: AuthUser;
}

/**
 * Login Session Selection Required
 */
export interface SessionSelectionRequired {
    status: "SESSION_SELECTION_REQUIRED";
    loginAttemptId: string;
    requiredTerminationCount: number;
    activeSessions: ActiveSession[];
}

/**
 * Login Result
 *
 * 로그인 성공 또는 기존 세션 선택 필요 상태.
 */
export type LoginResult =
    | LoginSuccess
    | SessionSelectionRequired;

/**
 * Session Selection Request
 *
 * POST /api/auth/login/session-selection
 */
export interface SessionSelectionRequest {
    loginAttemptId: string;
    sessionIds: string[];
}

/**
 * Authentication State
 *
 * 인증 토큰은 브라우저에서 관리하지 않는다.
 */
export interface AuthState {
    isInitializing: boolean;
    isAuthenticated: boolean;
    user: AuthUser | null;
}
