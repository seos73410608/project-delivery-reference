
import axios from "axios";

const apiClient = axios.create({
    baseURL: "/api",
    withCredentials: true,
    headers: {
        "Content-Type": "application/json",
    },
});

apiClient.interceptors.request.use(
    (config) => {
        // FormData 요청에서는 브라우저/Axios가
        // multipart boundary를 설정하도록 JSON 헤더 제거
        if (config.data instanceof FormData) {
            config.headers.delete("Content-Type");
        }

        return config;
    },
    (error) => Promise.reject(error),
);

apiClient.interceptors.response.use(
    (response) => response,
    (error) => Promise.reject(error),
);

export default apiClient;
