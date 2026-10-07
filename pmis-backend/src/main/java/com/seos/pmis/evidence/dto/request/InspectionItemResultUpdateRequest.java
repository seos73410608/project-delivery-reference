package com.seos.pmis.evidence.dto.request;

import com.seos.pmis.evidence.enums.InspectionItemResult;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Inspection Item 결과 변경 요청 DTO
 *
 * Inspection Item의 점검 결과를 변경할 때 사용한다.
 *
 * Client 수정 가능 필드:
 * - result
 * - remark
 *
 * Backend 관리 필드:
 * - id
 * - inspectionId
 * - requirementId
 * - evidenceId
 * - createdAt
 * - updatedAt
 *
 * Inspection Item의 기본 정보 수정과
 * 점검 결과 변경을 분리하여 관리한다.
 */
@Getter
@NoArgsConstructor
public class InspectionItemResultUpdateRequest {

    /**
     * Inspection Item 점검 결과
     *
     * PENDING
     * PASSED
     * FAILED
     * NOT_APPLICABLE
     */
    @NotNull(message = "Inspection Item 결과는 필수입니다.")
    private InspectionItemResult result;

    /**
     * 점검 결과에 대한 의견
     */
    private String remark;
}