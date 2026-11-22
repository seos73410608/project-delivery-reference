
package com.seos.pmis.operationscope.exception;

import com.seos.common.exception.code.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum OperationScopeErrorCode implements ErrorCode {

    OPERATION_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "PMIS_OPERATION_SCOPE_001",
            "운영 서비스를 찾을 수 없습니다."),

    OPERATIONAL_SCOPE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "PMIS_OPERATION_SCOPE_002",
            "운영 범위를 찾을 수 없습니다."),

    OPERATION_SCOPE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "PMIS_OPERATION_SCOPE_003",
            "운영 서비스와 운영 범위의 관계를 찾을 수 없습니다."),

    OPERATION_SCOPE_DUPLICATE(
            HttpStatus.CONFLICT,
            "PMIS_OPERATION_SCOPE_004",
            "동일한 운영 서비스와 운영 범위의 활성 관계가 이미 존재합니다."),

    OPERATION_SCOPE_STATUS_INVALID(
            HttpStatus.BAD_REQUEST,
            "PMIS_OPERATION_SCOPE_005",
            "현재 운영 범위 관계 상태에서는 해당 작업을 수행할 수 없습니다."),

    OPERATION_SCOPE_PERIOD_INVALID(
            HttpStatus.BAD_REQUEST,
            "PMIS_OPERATION_SCOPE_006",
            "운영 범위 관계 기간이 올바르지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    OperationScopeErrorCode(
            HttpStatus httpStatus,
            String code,
            String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
