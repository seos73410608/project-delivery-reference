package com.seos.cmdb.ci.exception;

import com.seos.common.exception.code.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ConfigurationItemErrorCode implements ErrorCode {

    CI_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "CMDB_CI_001",
            "Configuration Item을 찾을 수 없습니다."
    ),

    CI_TYPE_NOT_ASSIGNABLE(
            HttpStatus.BAD_REQUEST,
            "CMDB_CI_002",
            "Configuration Item을 등록할 수 없는 CI Type입니다."
    ),

    CI_MANAGER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "CMDB_CI_003",
            "담당자를 찾을 수 없습니다."
    ),

    CI_MANAGER_DISABLED(
            HttpStatus.BAD_REQUEST,
            "CMDB_CI_004",
            "비활성화된 사용자는 CI 담당자로 지정할 수 없습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ConfigurationItemErrorCode(
            HttpStatus httpStatus,
            String code,
            String message
    ) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}