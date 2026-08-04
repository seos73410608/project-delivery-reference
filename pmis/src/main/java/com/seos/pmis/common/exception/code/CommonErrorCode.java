package com.seos.pmis.common.exception.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CommonErrorCode implements ErrorCode {

    /**
     * =========================================================================
     * Common
     * =========================================================================
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
    ),

    /**
     * =========================================================================
     * Authentication
     * =========================================================================
     */

    UNAUTHORIZED(
            HttpStatus.UNAUTHORIZED,
            "AUTH_001",
            "인증이 필요합니다."
    ),

    ACCESS_DENIED(
            HttpStatus.FORBIDDEN,
            "AUTH_002",
            "접근 권한이 없습니다."
    ),

    INVALID_TOKEN(
            HttpStatus.UNAUTHORIZED,
            "AUTH_003",
            "유효하지 않은 JWT 토큰입니다."
    ),

    EXPIRED_TOKEN(
            HttpStatus.UNAUTHORIZED,
            "AUTH_004",
            "JWT 토큰이 만료되었습니다."
    ),

    UNSUPPORTED_TOKEN(
            HttpStatus.UNAUTHORIZED,
            "AUTH_005",
            "지원하지 않는 JWT 토큰입니다."
    ),

    MALFORMED_TOKEN(
            HttpStatus.UNAUTHORIZED,
            "AUTH_006",
            "잘못된 형식의 JWT 토큰입니다."
    ),

    USER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "AUTH_007",
            "사용자를 찾을 수 없습니다."
    ),

    INVALID_USERNAME_OR_PASSWORD(
            HttpStatus.UNAUTHORIZED,
            "AUTH_008",
            "아이디 또는 비밀번호가 올바르지 않습니다."
    ),

    ACCOUNT_DISABLED(
            HttpStatus.FORBIDDEN,
            "AUTH_009",
            "비활성화된 계정입니다."
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

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}