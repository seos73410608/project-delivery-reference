package com.seos.pmis.evidence.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Inspection Item 수정 요청 DTO
 *
 * 기존 Inspection Item의 검사 대상 및 기본 정보를 수정할 때 사용한다.
 *
 * Client 수정 가능 필드:
 * - requirementId
 * - evidenceId
 * - remark
 * - sortOrder
 *
 * Backend 관리 필드:
 * - id
 * - inspectionId
 * - result
 * - createdAt
 * - updatedAt
 *
 * Inspection Item의 검사 결과(result)는
 * 별도의 결과 변경 로직을 통해 처리한다.
 */
@Getter
@NoArgsConstructor
public class InspectionItemUpdateRequest {

    /**
     * 검사 대상 Evidence Requirement ID
     */
    @NotNull(message = "Evidence Requirement ID는 필수입니다.")
    private Long requirementId;

    /**
     * 검사 대상 Evidence ID
     *
     * 선택 사항이다.
     */
    private Long evidenceId;

    /**
     * Inspection Item 비고
     */
    private String remark;

    /**
     * Inspection Item 표시 순서
     */
    @NotNull(message = "정렬 순서는 필수입니다.")
    private Integer sortOrder;
}