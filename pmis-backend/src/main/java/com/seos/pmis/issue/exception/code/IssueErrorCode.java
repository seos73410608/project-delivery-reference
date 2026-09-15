package com.seos.pmis.issue.exception.code;

import com.seos.pmis.common.exception.code.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum IssueErrorCode implements ErrorCode {

    /**
     * =========================================================================
     * Issue
     * =========================================================================
     */

    ISSUE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "ISSUE_001",
            "이슈를 찾을 수 없습니다."
    ),

    ISSUE_PROJECT_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "ISSUE_002",
            "이슈가 속한 프로젝트를 찾을 수 없습니다."
    ),

    ISSUE_INVALID_STATUS(
            HttpStatus.BAD_REQUEST,
            "ISSUE_003",
            "유효하지 않은 이슈 상태입니다."
    ),

    ISSUE_INVALID_DATE_RANGE(
            HttpStatus.BAD_REQUEST,
            "ISSUE_004",
            "이슈의 날짜 범위가 유효하지 않습니다."
    ),

    ISSUE_DUPLICATE_KEY(
            HttpStatus.CONFLICT,
            "ISSUE_005",
            "이미 존재하는 이슈 키입니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    IssueErrorCode(
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