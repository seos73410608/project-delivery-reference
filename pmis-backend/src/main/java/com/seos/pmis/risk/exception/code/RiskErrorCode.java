package com.seos.pmis.risk.exception.code;

import com.seos.pmis.common.exception.code.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum RiskErrorCode implements ErrorCode {

    /**
     * =========================================================================
     * Risk
     * =========================================================================
     */

    RISK_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "RISK_001",
            "리스크를 찾을 수 없습니다."
    ),

    RISK_PROJECT_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "RISK_002",
            "리스크가 속한 프로젝트를 찾을 수 없습니다."
    ),

    RISK_INVALID_STATUS(
            HttpStatus.BAD_REQUEST,
            "RISK_003",
            "유효하지 않은 리스크 상태입니다."
    ),

    RISK_INVALID_DATE_RANGE(
            HttpStatus.BAD_REQUEST,
            "RISK_004",
            "리스크의 날짜 범위가 유효하지 않습니다."
    ),

    RISK_DUPLICATE_KEY(
            HttpStatus.CONFLICT,
            "RISK_005",
            "이미 존재하는 리스크 키입니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    RiskErrorCode(
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