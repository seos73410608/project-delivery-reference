package com.seos.pmis.change.exception.code;

import com.seos.pmis.common.exception.code.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ChangeErrorCode implements ErrorCode {

    CHANGE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "CHANGE_001",
            "변경 요청을 찾을 수 없습니다."
    ),

    CHANGE_PROJECT_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "CHANGE_002",
            "변경 요청이 속한 프로젝트를 찾을 수 없습니다."
    ),

    CHANGE_INVALID_STATUS(
            HttpStatus.BAD_REQUEST,
            "CHANGE_003",
            "유효하지 않은 변경 요청 상태입니다."
    ),

    CHANGE_INVALID_DATE_RANGE(
            HttpStatus.BAD_REQUEST,
            "CHANGE_004",
            "변경 요청의 날짜 범위가 유효하지 않습니다."
    ),

    CHANGE_DUPLICATE_KEY(
            HttpStatus.CONFLICT,
            "CHANGE_005",
            "이미 존재하는 변경 요청 키입니다."
    ),

    CHANGE_INVALID_APPROVAL(
            HttpStatus.BAD_REQUEST,
            "CHANGE_006",
            "유효하지 않은 변경 승인 요청입니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ChangeErrorCode(
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