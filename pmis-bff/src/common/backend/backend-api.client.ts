
import { env } from "../../config/env.js";
import type {
  BackendApiErrorResponse,
  BackendApiRequest,
  BackendApiResponse,
} from "./backend-api.types.js";

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

export class BackendApiClient {
  private readonly baseUrl = env.backendApiUrl;

  async request<T>(
    request: BackendApiRequest,
  ): Promise<T | null> {
    try {
      return await this.requestOnce<T>(
        request,
        request.accessToken,
      );
    } catch (error) {
      if (
        !(error instanceof BackendApiError) ||
        error.statusCode !== 401 ||
        !request.authContext
      ) {
        throw error;
      }

      const refreshedAccessToken =
        await request.authContext.refreshAccessToken(
          request.accessToken,
        );

      return this.requestOnce<T>(
        request,
        refreshedAccessToken,
      );
    }
  }

  private async requestOnce<T>(
    request: BackendApiRequest,
    accessToken: string,
  ): Promise<T | null> {
    const url = new URL(
      request.path.replace(/^\/+/, ""),
      `${this.baseUrl}/`,
    );

    if (request.query) {
      for (const [key, value] of Object.entries(request.query)) {
        if (value === null || value === undefined) {
          continue;
        }

        if (Array.isArray(value)) {
          for (const item of value) {
            url.searchParams.append(key, String(item));
          }

          continue;
        }

        url.searchParams.set(key, String(value));
      }
    }

    const headers: Record<string, string> = {
      Authorization: `Bearer ${accessToken}`,
      Accept: "application/json",
    };

    const options: RequestInit = {
      method: request.method,
      headers,
    };

    if (request.body !== undefined) {
      headers["Content-Type"] = "application/json";
      options.body = JSON.stringify(request.body);
    }

    let response: Response;

    try {
      response = await fetch(url, options);
    } catch {
      throw new BackendApiError(
        "Backend 서버에 연결할 수 없습니다.",
        "BACKEND_UNAVAILABLE",
        503,
      );
    }

    if (response.status === 204) {
      return null;
    }

    let result: BackendApiResponse<T> | BackendApiErrorResponse;

    try {
      const text = await response.text();

      if (!text.trim()) {
        if (response.ok) {
          return null;
        }

        throw new Error("Empty error response");
      }

      result = JSON.parse(text) as
        | BackendApiResponse<T>
        | BackendApiErrorResponse;
    } catch {
      throw new BackendApiError(
        "Backend 응답을 해석할 수 없습니다.",
        "INVALID_BACKEND_RESPONSE",
        502,
      );
    }

    if (
      typeof result !== "object" ||
      result === null ||
      Array.isArray(result)
    ) {
      throw new BackendApiError(
        "Backend 응답 형식이 올바르지 않습니다.",
        "INVALID_BACKEND_RESPONSE",
        502,
      );
    }

    const success =
      "success" in result && result.success === true;

    if (!response.ok || !success) {
      throw new BackendApiError(
        result.message ?? "Backend 요청에 실패했습니다.",
        result.code ?? "BACKEND_REQUEST_FAILED",
        response.status,
      );
    }

    return "data" in result ? result.data as T | null : null;
  }
}

export const backendApiClient = new BackendApiClient();
