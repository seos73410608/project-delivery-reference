
package com.seos.pmis.requirement.exception;

import com.seos.common.exception.code.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum OperationalScopeCiErrorCode implements ErrorCode {

    OPERATIONAL_SCOPE_CI_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "PMIS_OPERATIONAL_SCOPE_CI_001",
            "Operational Scope와 연결된 Configuration Item을 찾을 수 없습니다."
    ),

    OPERATIONAL_SCOPE_CI_ALREADY_EXISTS(
            HttpStatus.CONFLICT,
            "PMIS_OPERATIONAL_SCOPE_CI_002",
            "해당 Configuration Item은 이미 Operational Scope에 연결되어 있습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    OperationalScopeCiErrorCode(
            HttpStatus httpStatus,
            String code,
            String message
    ) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
