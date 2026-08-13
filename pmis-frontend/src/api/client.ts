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


export default apiClient;