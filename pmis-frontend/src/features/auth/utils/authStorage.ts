import type { AuthUser } from "@/features/auth/types/auth";


const ACCESS_TOKEN_KEY = "pmis_access_token";
const REFRESH_TOKEN_KEY = "pmis_refresh_token";
const USER_KEY = "pmis_user";


/**
 * Access Token 저장
 */
export function setAccessToken(
    accessToken: string,
): void {
    localStorage.setItem(
        ACCESS_TOKEN_KEY,
        accessToken,
    );
}


/**
 * Access Token 조회
 */
export function getAccessToken(): string | null {
    return localStorage.getItem(
        ACCESS_TOKEN_KEY,
    );
}


/**
 * Access Token 삭제
 */
export function removeAccessToken(): void {
    localStorage.removeItem(
        ACCESS_TOKEN_KEY,
    );
}


/**
 * Refresh Token 저장
 */
export function setRefreshToken(
    refreshToken: string,
): void {
    localStorage.setItem(
        REFRESH_TOKEN_KEY,
        refreshToken,
    );
}


/**
 * Refresh Token 조회
 */
export function getRefreshToken(): string | null {
    return localStorage.getItem(
        REFRESH_TOKEN_KEY,
    );
}


/**
 * Refresh Token 삭제
 */
export function removeRefreshToken(): void {
    localStorage.removeItem(
        REFRESH_TOKEN_KEY,
    );
}


/**
 * 로그인 사용자 정보 저장
 */
export function setUser(
    user: AuthUser,
): void {
    localStorage.setItem(
        USER_KEY,
        JSON.stringify(user),
    );
}


/**
 * 로그인 사용자 정보 조회
 */
export function getUser(): AuthUser | null {
    const value = localStorage.getItem(
        USER_KEY,
    );

    if (!value) {
        return null;
    }

    try {
        return JSON.parse(value) as AuthUser;
    } catch {
        return null;
    }
}


/**
 * 로그인 사용자 정보 삭제
 */
export function removeUser(): void {
    localStorage.removeItem(
        USER_KEY,
    );
}


/**
 * 인증 정보 전체 삭제
 *
 * 로그아웃 시 사용한다.
 */
export function clearAuthStorage(): void {
    removeAccessToken();
    removeRefreshToken();
    removeUser();
}