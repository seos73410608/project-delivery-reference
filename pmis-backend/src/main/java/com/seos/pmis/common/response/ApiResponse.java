package com.seos.pmis.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.seos.pmis.common.exception.code.ErrorCode;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private static final String SUCCESS_CODE = "SUCCESS";

    private final boolean success;
    private final String code;
    private final String message;
    private final T data;

    private ApiResponse(boolean success, String code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 성공 (데이터만 반환)
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(
                true,
                SUCCESS_CODE,
                null,
                data
        );
    }

    /**
     * 성공 (메시지 + 데이터)
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(
                true,
                SUCCESS_CODE,
                message,
                data
        );
    }

    /**
     * 성공 (메시지만 반환)
     */
    public static ApiResponse<Void> success(String message) {
        return new ApiResponse<>(
                true,
                SUCCESS_CODE,
                message,
                null
        );
    }

    /**
     * 실패 응답
     */
    public static <T> ApiResponse<T> fail(String code, String message) {
        return new ApiResponse<>(
                false,
                code,
                message,
                null
        );
    }

    /**
     * 실패 응답 (ErrorCode 사용)
     */
    public static ApiResponse<Void> fail(ErrorCode errorCode) {
        return new ApiResponse<>(
                false,
                errorCode.getCode(),
                errorCode.getMessage(),
                null
        );
    }
}