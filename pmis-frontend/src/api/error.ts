import axios from "axios";

/**
 * Backend API Error Response
 */
interface ApiErrorResponse {
    code?: unknown;
    message?: unknown;
}

/**
 * API 오류 메시지 추출
 *
 * Backend 응답에 message가 존재하면
 * 해당 메시지를 사용자에게 표시한다.
 *
 * Backend 메시지를 확인할 수 없는 경우
 * 호출 측에서 지정한 기본 메시지를 반환한다.
 *
 * @param error 발생한 오류
 * @param fallbackMessage 기본 오류 메시지
 * @returns 사용자에게 표시할 오류 메시지
 */
export function getApiErrorMessage(
    error: unknown,
    fallbackMessage: string,
): string {

    /**
     * Axios 오류인 경우
     */
    if (axios.isAxiosError<ApiErrorResponse>(error)) {

        const message =
            error.response?.data?.message;

        /**
         * Backend 오류 메시지가 유효한 경우
         */
        if (
            typeof message === "string" &&
            message.trim().length > 0
        ) {
            return message;
        }

    }

    /**
     * Backend 메시지가 없거나
     * Axios 오류가 아닌 경우
     */
    return fallbackMessage;

}