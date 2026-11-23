
export interface BackendApiResponse<T> {
  success: boolean;
  code: string;
  message?: string;
  data: T | null;
}

export type BackendHttpMethod =
  | "GET"
  | "POST"
  | "PUT"
  | "PATCH"
  | "DELETE";

export interface BackendApiRequest {
  method: BackendHttpMethod;
  path: string;
  accessToken: string;
  query?: Record<
    string,
    string | number | boolean | null | undefined
  >;
  body?: unknown;
}

export interface BackendApiErrorResponse {
  success?: boolean;
  code?: string;
  message?: string;
}
