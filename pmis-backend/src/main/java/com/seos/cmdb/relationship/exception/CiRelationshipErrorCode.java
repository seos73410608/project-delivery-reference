package com.seos.cmdb.relationship.exception;

import com.seos.common.exception.code.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CiRelationshipErrorCode implements ErrorCode {

    RELATIONSHIP_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "CMDB_CI_RELATIONSHIP_001",
            "CI Relationship을 찾을 수 없습니다."
    ),

    RELATIONSHIP_TYPE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "CMDB_CI_RELATIONSHIP_002",
            "CI Relationship Type을 찾을 수 없습니다."
    ),

    RELATIONSHIP_TYPE_DISABLED(
            HttpStatus.CONFLICT,
            "CMDB_CI_RELATIONSHIP_003",
            "비활성화된 Relationship Type은 사용할 수 없습니다."
    ),

    RELATIONSHIP_CI_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "CMDB_CI_RELATIONSHIP_004",
            "Configuration Item을 찾을 수 없습니다."
    ),

    RELATIONSHIP_SELF_REFERENCE(
            HttpStatus.BAD_REQUEST,
            "CMDB_CI_RELATIONSHIP_005",
            "CI는 자기 자신과 관계를 맺을 수 없습니다."
    ),

    RELATIONSHIP_INVALID_DIRECTION(
            HttpStatus.BAD_REQUEST,
            "CMDB_CI_RELATIONSHIP_006",
            "유효하지 않은 Relationship 조회 방향입니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    CiRelationshipErrorCode(
            HttpStatus httpStatus,
            String code,
            String message
    ) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}