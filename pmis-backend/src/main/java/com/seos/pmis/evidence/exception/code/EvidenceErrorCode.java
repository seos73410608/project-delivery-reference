package com.seos.pmis.evidence.exception.code;

import com.seos.pmis.common.exception.code.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum EvidenceErrorCode implements ErrorCode {

    EVIDENCE_REQUIREMENT_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "EVIDENCE_REQUIREMENT_NOT_FOUND",
            "Evidence Requirement를 찾을 수 없습니다."
    ),

    EVIDENCE_REQUIREMENT_DELETE_NOT_ALLOWED(
            HttpStatus.CONFLICT,
            "EVIDENCE_REQUIREMENT_DELETE_NOT_ALLOWED",
            "Evidence가 등록된 Evidence Requirement는 삭제할 수 없습니다."
    ),

    EVIDENCE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "EVIDENCE_NOT_FOUND",
            "Evidence를 찾을 수 없습니다."
    ),

    EVIDENCE_DELETE_NOT_ALLOWED(
            HttpStatus.CONFLICT,
            "EVIDENCE_DELETE_NOT_ALLOWED",
            "Verification 또는 Inspection에서 사용 중인 Evidence는 삭제할 수 없습니다."
    ),

    INSPECTION_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "INSPECTION_NOT_FOUND",
            "Inspection을 찾을 수 없습니다."
    ),

    INSPECTION_ITEM_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "INSPECTION_ITEM_NOT_FOUND",
            "Inspection Item을 찾을 수 없습니다."
    ),

    INSPECTION_ITEM_DELETE_NOT_ALLOWED(
            HttpStatus.CONFLICT,
            "INSPECTION_ITEM_DELETE_NOT_ALLOWED",
            "Inspection에서 사용 중인 Inspection Item은 삭제할 수 없습니다."
    ),

    PROJECT_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "PROJECT_NOT_FOUND",
            "프로젝트를 찾을 수 없습니다."
    ),

    WBS_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "WBS_NOT_FOUND",
            "WBS를 찾을 수 없습니다."
    ),

    WBS_PROJECT_MISMATCH(
            HttpStatus.BAD_REQUEST,
            "WBS_PROJECT_MISMATCH",
            "WBS가 해당 Project에 속하지 않습니다."
    ),

    EVIDENCE_PROJECT_MISMATCH(
            HttpStatus.BAD_REQUEST,
            "EVIDENCE_PROJECT_MISMATCH",
            "Evidence와 Evidence Requirement의 Project가 일치하지 않습니다."
    ),

    EVIDENCE_WBS_PROJECT_MISMATCH(
            HttpStatus.BAD_REQUEST,
            "EVIDENCE_WBS_PROJECT_MISMATCH",
            "Evidence의 WBS가 해당 Project에 속하지 않습니다."
    ),

    EVIDENCE_VERIFICATION_NOT_ALLOWED(
            HttpStatus.BAD_REQUEST,
            "EVIDENCE_VERIFICATION_NOT_ALLOWED",
            "현재 Evidence 상태에서는 Verification을 수행할 수 없습니다."
    ),

    EVIDENCE_VERIFICATION_REMARK_REQUIRED(
            HttpStatus.BAD_REQUEST,
            "EVIDENCE_VERIFICATION_REMARK_REQUIRED",
            "Evidence Verification 반려 사유는 필수입니다."
    ),

    INSPECTION_REQUIREMENT_PROJECT_MISMATCH(
            HttpStatus.BAD_REQUEST,
            "INSPECTION_REQUIREMENT_PROJECT_MISMATCH",
            "Inspection과 Evidence Requirement의 Project가 일치하지 않습니다."
    ),

    INSPECTION_EVIDENCE_PROJECT_MISMATCH(
            HttpStatus.BAD_REQUEST,
            "INSPECTION_EVIDENCE_PROJECT_MISMATCH",
            "Inspection과 Evidence의 Project가 일치하지 않습니다."
    ),

    INSPECTION_EVIDENCE_REQUIREMENT_MISMATCH(
            HttpStatus.BAD_REQUEST,
            "INSPECTION_EVIDENCE_REQUIREMENT_MISMATCH",
            "Inspection Item의 Evidence가 해당 Evidence Requirement에 속하지 않습니다."
    ),

    INSPECTION_INVALID_STATUS_TRANSITION(
            HttpStatus.BAD_REQUEST,
            "INSPECTION_INVALID_STATUS_TRANSITION",
            "유효하지 않은 Inspection 상태 전이입니다."
    ),

    INSPECTION_ITEM_INVALID_RESULT_TRANSITION(
            HttpStatus.BAD_REQUEST,
            "INSPECTION_ITEM_INVALID_RESULT_TRANSITION",
            "유효하지 않은 Inspection Item 결과 전이입니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}