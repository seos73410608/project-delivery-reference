package com.seos.pmis.requirement.exception;

import com.seos.common.exception.code.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum OperationalScopeErrorCode implements ErrorCode {

    OPERATIONAL_SCOPE_PARENT_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "PMIS_OPERATIONAL_SCOPE_001",
            "상위 Operational Scope를 찾을 수 없습니다."
    ),

    OPERATIONAL_SCOPE_OWNER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "PMIS_OPERATIONAL_SCOPE_002",
            "지정한 담당자를 찾을 수 없습니다."
    ),

    OPERATIONAL_SCOPE_OWNER_DISABLED(
            HttpStatus.BAD_REQUEST,
            "PMIS_OPERATIONAL_SCOPE_003",
            "비활성화된 사용자는 Operational Scope 담당자로 지정할 수 없습니다."
    ),

    OPERATIONAL_SCOPE_EFFECTIVE_PERIOD_INVALID(
            HttpStatus.BAD_REQUEST,
            "PMIS_OPERATIONAL_SCOPE_004",
            "적용 종료일은 적용 시작일보다 빠를 수 없습니다."
    ),
    
    OPERATIONAL_SCOPE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "PMIS_OPERATIONAL_SCOPE_005",
            "Operational Scope를 찾을 수 없습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    OperationalScopeErrorCode(
            HttpStatus httpStatus,
            String code,
            String message
    ) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
