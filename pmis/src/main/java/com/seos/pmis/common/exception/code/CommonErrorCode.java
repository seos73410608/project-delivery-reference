package com.seos.pmis.common.exception.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CommonErrorCode implements ErrorCode {

    /**
     * Common
     */
    INVALID_PARAMETER(
            HttpStatus.BAD_REQUEST,
            "COMMON_001",
            "잘못된 요청입니다."
    ),

    RESOURCE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "COMMON_002",
            "요청한 리소스를 찾을 수 없습니다."
    ),

    INTERNAL_SERVER_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "COMMON_003",
            "서버 내부 오류가 발생했습니다."
    ),

    INVALID_REQUEST(
            HttpStatus.BAD_REQUEST,
            "COMMON_004",
            "유효하지 않은 요청입니다."
    ),

    METHOD_NOT_ALLOWED(
            HttpStatus.METHOD_NOT_ALLOWED,
            "COMMON_005",
            "지원하지 않는 HTTP 메서드입니다."
    );

    private final HttpStatus httpStatus;

    private final String code;

    private final String message;

    CommonErrorCode(
            HttpStatus httpStatus,
            String code,
            String message
    ) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}