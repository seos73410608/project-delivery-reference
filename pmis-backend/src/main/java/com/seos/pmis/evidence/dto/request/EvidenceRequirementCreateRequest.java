package com.seos.pmis.evidence.dto.request;

import com.seos.pmis.evidence.enums.RequirementType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Evidence Requirement 생성 요청 DTO
 *
 * 프로젝트에서 확보해야 하는
 * 증적/산출물 요구사항을 생성할 때 사용한다.
 *
 * Backend 관리 필드:
 * - id
 * - projectId
 * - requirementKey
 * - status
 * - createdAt
 * - updatedAt
 *
 * 위 필드는 Client에서 전달받지 않는다.
 */
@Getter
@NoArgsConstructor
public class EvidenceRequirementCreateRequest {

    /**
     * Requirement 제목
     */
    @NotBlank(message = "Evidence Requirement 제목은 필수입니다.")
    @Size(
            max = 200,
            message = "Evidence Requirement 제목은 200자 이하여야 합니다."
    )
    private String title;

    /**
     * Requirement 설명
     */
    private String description;

    /**
     * Requirement 유형
     */
    @NotNull(message = "Evidence Requirement 유형은 필수입니다.")
    private RequirementType requirementType;

    /**
     * 필수 증적 여부
     *
     * true:
     * 반드시 확보해야 하는 증적
     *
     * false:
     * 선택적으로 관리하는 증적
     */
    @NotNull(message = "필수 증적 여부는 필수입니다.")
    private Boolean required;

    /**
     * 증적 확보 예정일
     */
    private java.time.LocalDate dueDate;

    /**
     * 연결할 WBS ID
     *
     * 선택 사항이다.
     *
     * null이면 프로젝트 단위 Requirement로 관리한다.
     */
    private Long wbsId;

    /**
     * 표시 순서
     */
    @NotNull(message = "정렬 순서는 필수입니다.")
    private Integer sortOrder;
}