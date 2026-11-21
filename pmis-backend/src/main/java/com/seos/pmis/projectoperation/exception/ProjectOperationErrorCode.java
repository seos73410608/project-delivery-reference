
package com.seos.pmis.projectoperation.exception;

import com.seos.common.exception.code.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ProjectOperationErrorCode implements ErrorCode {

    PROJECT_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "PMIS_PROJECT_OPERATION_001",
            "프로젝트를 찾을 수 없습니다."
    ),

    OPERATION_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "PMIS_PROJECT_OPERATION_002",
            "운영 서비스를 찾을 수 없습니다."
    ),

    PROJECT_OPERATION_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "PMIS_PROJECT_OPERATION_003",
            "프로젝트와 운영 서비스의 관계를 찾을 수 없습니다."
    ),

    PROJECT_OPERATION_DUPLICATE(
            HttpStatus.CONFLICT,
            "PMIS_PROJECT_OPERATION_004",
            "동일한 프로젝트와 운영 서비스의 활성 관계가 이미 존재합니다."
    ),

    PROJECT_OPERATION_STATUS_INVALID(
            HttpStatus.BAD_REQUEST,
            "PMIS_PROJECT_OPERATION_005",
            "현재 프로젝트 운영 관계 상태에서는 해당 작업을 수행할 수 없습니다."
    ),

    PROJECT_OPERATION_PERIOD_INVALID(
            HttpStatus.BAD_REQUEST,
            "PMIS_PROJECT_OPERATION_006",
            "프로젝트 운영 관계 기간이 올바르지 않습니다."
    ),

    HANDOVER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "PMIS_PROJECT_OPERATION_007",
            "인수인계 정보를 찾을 수 없습니다."
    ),

    HANDOVER_PARTICIPANT_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "PMIS_PROJECT_OPERATION_008",
            "운영 참여자를 찾을 수 없습니다."
    ),

    HANDOVER_PARTICIPANT_INVALID(
            HttpStatus.BAD_REQUEST,
            "PMIS_PROJECT_OPERATION_009",
            "인수인계 참여자의 소속, 역할 또는 참여 기간이 올바르지 않습니다."
    ),

    HANDOVER_PREVIOUS_INVALID(
            HttpStatus.BAD_REQUEST,
            "PMIS_PROJECT_OPERATION_010",
            "이전 인수인계 이력과 현재 인수인계의 연결이 올바르지 않습니다."
    ),

    HANDOVER_SEQUENCE_INVALID(
            HttpStatus.BAD_REQUEST,
            "PMIS_PROJECT_OPERATION_011",
            "인수인계 순번이 올바르지 않습니다."
    ),

    HANDOVER_ALREADY_ACTIVE(
            HttpStatus.CONFLICT,
            "PMIS_PROJECT_OPERATION_012",
            "해당 프로젝트 운영 관계에 진행 중인 인수인계가 존재합니다."
    ),

    HANDOVER_STATUS_INVALID(
            HttpStatus.BAD_REQUEST,
            "PMIS_PROJECT_OPERATION_013",
            "현재 인수인계 상태에서는 해당 작업을 수행할 수 없습니다."
    ),

    HANDOVER_USER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "PMIS_PROJECT_OPERATION_014",
            "인수인계 담당자를 찾을 수 없습니다."
    ),

    HANDOVER_USER_DISABLED(
            HttpStatus.BAD_REQUEST,
            "PMIS_PROJECT_OPERATION_015",
            "비활성화된 사용자는 인수인계 담당자로 지정할 수 없습니다."
    ),

    PROJECT_OPERATION_RELATIONSHIP_TYPE_INVALID(
            HttpStatus.BAD_REQUEST,
            "PMIS_PROJECT_OPERATION_016",
            "프로젝트 운영 관계 유형은 필수입니다."),

    HANDOVER_DATE_INVALID(
            HttpStatus.BAD_REQUEST,
            "PMIS_PROJECT_OPERATION_017",
            "인수인계 일정 또는 처리 시각이 올바르지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ProjectOperationErrorCode(
            HttpStatus httpStatus,
            String code,
            String message
    ) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
