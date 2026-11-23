
package com.seos.pmis.operation.exception;

import com.seos.common.exception.code.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum OperationErrorCode implements ErrorCode {

    OPERATION_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "PMIS_OPERATION_001",
            "운영 서비스를 찾을 수 없습니다."),

    OPERATION_DUPLICATE(
            HttpStatus.CONFLICT,
            "PMIS_OPERATION_002",
            "동일한 운영 코드가 이미 존재합니다."),

    OPERATION_MANAGER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "PMIS_OPERATION_003",
            "운영 담당자를 찾을 수 없습니다."),

    OPERATION_PERIOD_INVALID(
            HttpStatus.BAD_REQUEST,
            "PMIS_OPERATION_004",
            "운영 기간이 올바르지 않습니다."),
            
    OPERATION_STATUS_INVALID(
            HttpStatus.BAD_REQUEST,
            "PMIS_OPERATION_005",
            "운영 상태가 올바르지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    OperationErrorCode(
            HttpStatus httpStatus,
            String code,
            String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
