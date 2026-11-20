
import { env } from "../../config/env.js";

export interface BackendApiResponse<T> {
  success: boolean;
  code: string;
  message?: string;
  data: T | null;
}

export interface BackendLoginRequest {
  username: string;
  password: string;
}

export interface BackendSessionSelectionRequest {
  loginAttemptId: string;
  sessionIds: string[];
}

export interface BackendRefreshRequest {
  refreshToken: string;
}

export interface BackendActiveSession {
  sessionId: string;
  createdAt: string;
  lastAccessedAt: string;
}

export interface BackendLoginResponse {
  status: "SUCCESS" | "SESSION_SELECTION_REQUIRED";
  userId?: number;
  username?: string;
  name?: string;
  role?: string;
  accessToken?: string;
  refreshToken?: string;
  accessTokenExpiration?: number;
  refreshTokenExpiration?: number;
  loginAttemptId?: string;
  requiredTerminationCount?: number;
  activeSessions?: BackendActiveSession[];
}

export class BackendApiError extends Error {
  constructor(
    message: string,
    public readonly code: string,
    public readonly statusCode: number,
  ) {
    super(message);
    this.name = "BackendApiError";
  }
}

export class BackendAuthService {
  private readonly baseUrl = env.backendApiUrl;

  async login(
    request: BackendLoginRequest,
  ): Promise<BackendLoginResponse> {
    return this.post<BackendLoginResponse>("/api/auth/login", request);
  }

  async completeSessionSelection(
    request: BackendSessionSelectionRequest,
  ): Promise<BackendLoginResponse> {
    return this.post<BackendLoginResponse>(
      "/api/auth/login/session-selection",
      request,
    );
  }

  async refresh(
    request: BackendRefreshRequest,
  ): Promise<BackendLoginResponse> {
    return this.post<BackendLoginResponse>("/api/auth/refresh", request);
  }

  async logout(accessToken: string): Promise<void> {
    const authorization = `Bearer ${accessToken}`;

    console.info("[BackendAuthService.logout] Request", {
      method: "POST",
      path: "/api/auth/logout",
      authorizationPresent: authorization.length > 7,
      bearerScheme: authorization.startsWith("Bearer "),
      tokenLength: accessToken.length,
    });

    let response: Response;

    try {
      response = await fetch(`${this.baseUrl}/api/auth/logout`, {
        method: "POST",
        headers: {
          Authorization: authorization,
          Accept: "application/json",
        },
      });
    } catch {
      throw new BackendApiError(
        "Backend 서버에 연결할 수 없습니다.",
        "BACKEND_UNAVAILABLE",
        503,
      );
    }

    let result: BackendApiResponse<null>;

    try {
      result = (await response.json()) as BackendApiResponse<null>;
    } catch {
      throw new BackendApiError(
        "Backend 응답을 해석할 수 없습니다.",
        "INVALID_BACKEND_RESPONSE",
        502,
      );
    }

    if (!response.ok || !result.success) {
      throw new BackendApiError(
        result.message ?? "Backend 로그아웃에 실패했습니다.",
        result.code || "BACKEND_REQUEST_FAILED",
        response.status,
      );
    }
  }

  private async post<T>(
    path: string,
    body: unknown,
  ): Promise<T> {
    let response: Response;

    try {
      response = await fetch(`${this.baseUrl}${path}`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Accept: "application/json",
        },
        body: JSON.stringify(body),
      });
    } catch {
      throw new BackendApiError(
        "Backend 서버에 연결할 수 없습니다.",
        "BACKEND_UNAVAILABLE",
        503,
      );
    }

    let result: BackendApiResponse<T>;

    try {
      result = (await response.json()) as BackendApiResponse<T>;
    } catch {
      throw new BackendApiError(
        "Backend 응답을 해석할 수 없습니다.",
        "INVALID_BACKEND_RESPONSE",
        502,
      );
    }

    if (!response.ok || !result.success) {
      throw new BackendApiError(
        result.message ?? "Backend 요청에 실패했습니다.",
        result.code || "BACKEND_REQUEST_FAILED",
        response.status,
      );
    }

    if (result.data === null || result.data === undefined) {
      throw new BackendApiError(
        "Backend 응답 데이터가 없습니다.",
        "EMPTY_BACKEND_RESPONSE",
        502,
      );
    }

    return result.data;
  }
}

export const backendAuthService = new BackendAuthService();
