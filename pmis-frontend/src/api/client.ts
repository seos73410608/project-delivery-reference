import axios from "axios";

import { getAccessToken } from "@/features/auth/utils/authStorage";


const apiClient = axios.create({
    baseURL: "/api",
    headers: {
        "Content-Type": "application/json",
    },
});


/**
 * Request Interceptor
 *
 * 모든 API 요청 전에 Access Token을 확인하고
 * 존재하는 경우 Authorization Header에 추가한다.
 *
 * Authorization:
 * Bearer {accessToken}
 */
apiClient.interceptors.request.use(
    (config) => {
        const accessToken = getAccessToken();

        if (accessToken) {
            config.headers.Authorization =
                `Bearer ${accessToken}`;
        }

        return config;
    },
    (error) => {
        return Promise.reject(error);
    },
);


/**
 * Response Interceptor
 *
 * Backend API 응답을 공통으로 처리한다.
 *
 * 현재 단계에서는 응답을 그대로 반환한다.
 *
 * 추후 인증 기능 확장 시 다음 처리를 추가한다.
 *
 * - 401 Unauthorized
 * - Access Token 만료
 * - Refresh Token 재발급
 * - 로그인 페이지 이동
 */
apiClient.interceptors.response.use(
    (response) => {
        return response;
    },
    (error) => {
        return Promise.reject(error);
    },
);


export default apiClient;