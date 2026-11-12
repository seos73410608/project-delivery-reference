package com.seos.cmdb.ci.exception;

import com.seos.common.exception.code.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CiTypeErrorCode implements ErrorCode {

    CI_TYPE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "CMDB_CI_TYPE_001",
            "CI Type을 찾을 수 없습니다."
    ),

    CI_TYPE_CODE_DUPLICATED(
            HttpStatus.CONFLICT,
            "CMDB_CI_TYPE_002",
            "이미 사용 중인 CI Type 코드입니다."
    ),

    CI_TYPE_PARENT_DISABLED(
            HttpStatus.BAD_REQUEST,
            "CMDB_CI_TYPE_003",
            "비활성화된 CI Type은 상위 Type으로 지정할 수 없습니다."
    ),

    CI_TYPE_SELF_PARENT(
            HttpStatus.BAD_REQUEST,
            "CMDB_CI_TYPE_004",
            "자기 자신을 상위 Type으로 지정할 수 없습니다."
    ),

    CI_TYPE_CIRCULAR_REFERENCE(
            HttpStatus.BAD_REQUEST,
            "CMDB_CI_TYPE_005",
            "하위 계층을 상위 Type으로 지정할 수 없습니다."
    ),

    CI_TYPE_HAS_CHILDREN(
            HttpStatus.CONFLICT,
            "CMDB_CI_TYPE_006",
            "하위 CI Type이 존재하여 삭제할 수 없습니다."
    ),

    CI_TYPE_IN_USE(
            HttpStatus.CONFLICT,
            "CMDB_CI_TYPE_007",
            "Configuration Item에서 사용 중인 CI Type은 삭제할 수 없습니다."
    ),

    CI_TYPE_DISABLED(
            HttpStatus.CONFLICT,
            "CMDB_CI_TYPE_008",
            "비활성화된 CI Type은 사용할 수 없습니다."
    ),
    
    CI_TYPE_HAS_ACTIVE_CHILDREN(
        HttpStatus.CONFLICT,
        "CMDB_CI_TYPE_009",
        "활성화된 하위 CI Type이 존재하여 비활성화할 수 없습니다."
    ),

    CI_TYPE_HAS_CONFIGURATION_ITEMS(
        HttpStatus.CONFLICT,
        "CMDB_CI_TYPE_010",
        "Configuration Item에서 사용 중인 CI Type은 비활성화하거나 일반 분류 Type으로 변경할 수 없습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    CiTypeErrorCode(
            HttpStatus httpStatus,
            String code,
            String message
    ) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}