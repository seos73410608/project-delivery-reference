package com.seos.pmis.evidence.dto.request;

import com.seos.pmis.evidence.enums.RequirementType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Evidence Requirement 수정 요청 DTO
 *
 * 기존 Evidence Requirement의 정보를 수정할 때 사용한다.
 *
 * Client 수정 가능 필드:
 * - wbsId
 * - title
 * - description
 * - requirementType
 * - required
 * - dueDate
 * - sortOrder
 *
 * Backend 관리 필드:
 * - id
 * - projectId
 * - requirementKey
 * - status
 * - createdAt
 * - updatedAt
 *
 * 위 Backend 관리 필드는 Client에서 전달받지 않는다.
 */
@Getter
@NoArgsConstructor
public class EvidenceRequirementUpdateRequest {

    /**
     * 연결할 WBS ID
     *
     * null이면 WBS 연결을 해제하고
     * 프로젝트 단위 Requirement로 관리한다.
     */
    private Long wbsId;

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
     */
    @NotNull(message = "필수 증적 여부는 필수입니다.")
    private Boolean required;

    /**
     * 증적 확보 예정일
     */
    private LocalDate dueDate;

    /**
     * 표시 순서
     */
    @NotNull(message = "정렬 순서는 필수입니다.")
    private Integer sortOrder;
}