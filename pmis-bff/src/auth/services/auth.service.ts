import { randomUUID } from "node:crypto";
import {
  backendAuthService,
  type BackendLoginRequest,
  type BackendLoginResponse,
  type BackendSessionSelectionRequest,
  type BackendActiveSession,
} from "./backend-auth.service.js";
import type { SessionStore } from "../session/session-store.js";
import type { AuthUser, BffSession } from "../session/session.types.js";

export interface SessionSelectionRequired {
  status: "SESSION_SELECTION_REQUIRED";
  loginAttemptId: string;
  requiredTerminationCount: number;
  activeSessions: BackendActiveSession[];
}

export interface LoginSuccess {
  status: "SUCCESS";
  sessionId: string;
  user: AuthUser;
}

export type AuthResult = LoginSuccess | SessionSelectionRequired;

export class AuthServiceError extends Error {
  constructor(
    message: string,
    public readonly code: string,
    public readonly statusCode: number,
  ) {
    super(message);
    this.name = "AuthServiceError";
  }
}

export class AuthService {
  private readonly refreshPromises = new Map<
    string,
    Promise<BffSession>
  >();

  constructor(
    private readonly sessionStore: SessionStore,
  ) {}

  async login(request: BackendLoginRequest): Promise<AuthResult> {
    const response = await backendAuthService.login(request);
    return this.processLoginResponse(response);
  }

  async completeSessionSelection(
    request: BackendSessionSelectionRequest,
  ): Promise<AuthResult> {
    const response =
      await backendAuthService.completeSessionSelection(request);

    return this.processLoginResponse(response);
  }

  async refresh(sessionId: string): Promise<BffSession> {
    return this.refreshSession(sessionId);
  }

  async refreshAccessToken(
    sessionId: string,
    rejectedAccessToken: string,
  ): Promise<BffSession> {
    return this.refreshSession(sessionId, rejectedAccessToken);
  }

  async logout(sessionId: string): Promise<void> {
    const session = await this.sessionStore.get(sessionId);

    if (!session) {
      throw new AuthServiceError(
        "세션이 유효하지 않거나 만료되었습니다.",
        "UNAUTHORIZED",
        401,
      );
    }

    await backendAuthService.logout(session.accessToken);

    await this.sessionStore.delete(sessionId);
  }

  private refreshSession(
    sessionId: string,
    rejectedAccessToken?: string,
  ): Promise<BffSession> {
    const existingPromise = this.refreshPromises.get(sessionId);

    if (existingPromise) {
      return existingPromise;
    }

    const refreshPromise = this.performRefresh(
      sessionId,
      rejectedAccessToken,
    );

    this.refreshPromises.set(sessionId, refreshPromise);

    void refreshPromise.then(
      () => {
        if (this.refreshPromises.get(sessionId) === refreshPromise) {
          this.refreshPromises.delete(sessionId);
        }
      },
      () => {
        if (this.refreshPromises.get(sessionId) === refreshPromise) {
          this.refreshPromises.delete(sessionId);
        }
      },
    );

    return refreshPromise;
  }

  private async performRefresh(
    sessionId: string,
    rejectedAccessToken?: string,
  ): Promise<BffSession> {
    const session = await this.sessionStore.get(sessionId);

    if (!session) {
      throw new AuthServiceError(
        "세션이 유효하지 않거나 만료되었습니다.",
        "UNAUTHORIZED",
        401,
      );
    }

    if (
      rejectedAccessToken !== undefined &&
      session.accessToken !== rejectedAccessToken
    ) {
      return session;
    }

    const response = await backendAuthService.refresh({
      refreshToken: session.refreshToken,
    });

    if (response.status !== "SUCCESS") {
      throw new AuthServiceError(
        "Backend Refresh 응답이 올바르지 않습니다.",
        "INVALID_BACKEND_RESPONSE",
        502,
      );
    }

    if (response.userId !== session.user.userId) {
      throw new AuthServiceError(
        "Backend Refresh 응답의 사용자 정보가 기존 세션과 일치하지 않습니다.",
        "INVALID_BACKEND_USER",
        502,
      );
    }

    const accessToken = this.requireToken(
      response.accessToken,
      "Access Token",
    );
    const refreshToken = this.requireToken(
      response.refreshToken,
      "Refresh Token",
    );

    const now = Date.now();
    const expiresAt = this.getJwtExpiration(refreshToken);

    if (expiresAt <= now) {
      throw new AuthServiceError(
        "Backend에서 이미 만료된 Refresh Token을 반환했습니다.",
        "INVALID_REFRESH_TOKEN_EXPIRATION",
        502,
      );
    }

    const refreshedSession: BffSession = {
      ...session,
      accessToken,
      refreshToken,
      expiresAt,
      updatedAt: now,
    };

    const updated = await this.sessionStore.compareAndSet(
      sessionId,
      session.accessToken,
      refreshedSession,
    );

    if (updated) {
      return refreshedSession;
    }

    const currentSession = await this.sessionStore.get(sessionId);

    if (!currentSession) {
      throw new AuthServiceError(
        "세션이 갱신되기 전에 종료되었습니다.",
        "UNAUTHORIZED",
        401,
      );
    }

    if (currentSession.accessToken !== session.accessToken) {
      return currentSession;
    }

    throw new AuthServiceError(
      "세션 갱신 결과를 저장하지 못했습니다.",
      "SESSION_REFRESH_CONFLICT",
      409,
    );
  }

  private async processLoginResponse(
    response: BackendLoginResponse,
  ): Promise<AuthResult> {
    if (response.status === "SESSION_SELECTION_REQUIRED") {
      if (
        !response.loginAttemptId ||
        typeof response.requiredTerminationCount !== "number" ||
        !Array.isArray(response.activeSessions)
      ) {
        throw new AuthServiceError(
          "Backend 세션 선택 응답이 올바르지 않습니다.",
          "INVALID_BACKEND_RESPONSE",
          502,
        );
      }

      return {
        status: "SESSION_SELECTION_REQUIRED",
        loginAttemptId: response.loginAttemptId,
        requiredTerminationCount: response.requiredTerminationCount,
        activeSessions: response.activeSessions,
      };
    }

    if (response.status !== "SUCCESS") {
      throw new AuthServiceError(
        "Backend 로그인 상태를 확인할 수 없습니다.",
        "INVALID_BACKEND_RESPONSE",
        502,
      );
    }

    const user = this.createAuthUser(response);
    const accessToken = this.requireToken(response.accessToken, "Access Token");
    const refreshToken = this.requireToken(
      response.refreshToken,
      "Refresh Token",
    );

    const now = Date.now();
    const expiresAt = this.getJwtExpiration(refreshToken);

    if (expiresAt <= now) {
      throw new AuthServiceError(
        "Backend에서 이미 만료된 Refresh Token을 반환했습니다.",
        "INVALID_REFRESH_TOKEN_EXPIRATION",
        502,
      );
    }

    const session: BffSession = {
      sessionId: randomUUID(),
      accessToken,
      refreshToken,
      user,
      expiresAt,
      createdAt: now,
      updatedAt: now,
    };

    await this.sessionStore.set(session);

    return {
      status: "SUCCESS",
      sessionId: session.sessionId,
      user: session.user,
    };
  }

  private createAuthUser(response: BackendLoginResponse): AuthUser {
    if (
      typeof response.userId !== "number" ||
      typeof response.username !== "string" ||
      typeof response.name !== "string" ||
      typeof response.role !== "string"
    ) {
      throw new AuthServiceError(
        "Backend 사용자 정보가 올바르지 않습니다.",
        "INVALID_BACKEND_USER",
        502,
      );
    }

    return {
      userId: response.userId,
      username: response.username,
      name: response.name,
      role: response.role,
    };
  }

  private requireToken(
    token: string | undefined,
    tokenName: string,
  ): string {
    if (!token || !token.trim()) {
      throw new AuthServiceError(
        `Backend ${tokenName}이 누락되었습니다.`,
        "MISSING_BACKEND_TOKEN",
        502,
      );
    }

    return token;
  }

  private getJwtExpiration(token: string): number {
    try {
      const parts = token.split(".");

      if (parts.length !== 3 || !parts[1]) {
        throw new Error("Invalid JWT structure");
      }

      const payload = JSON.parse(
        Buffer.from(parts[1], "base64url").toString("utf8"),
      ) as { exp?: unknown };

      if (typeof payload.exp !== "number" || !Number.isFinite(payload.exp)) {
        throw new Error("Invalid JWT expiration");
      }

      return payload.exp * 1000;
    } catch {
      throw new AuthServiceError(
        "Backend Refresh Token의 만료 정보를 확인할 수 없습니다.",
        "INVALID_REFRESH_TOKEN",
        502,
      );
    }
  }
}

export function createAuthService(
  sessionStore: SessionStore,
): AuthService {
  return new AuthService(sessionStore);
}