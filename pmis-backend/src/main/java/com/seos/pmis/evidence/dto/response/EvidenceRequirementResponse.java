package com.seos.pmis.evidence.dto.response;

import com.seos.pmis.evidence.enums.RequirementStatus;
import com.seos.pmis.evidence.enums.RequirementType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Evidence Requirement 응답 DTO
 *
 * Evidence Requirement 조회 결과를
 * Client에게 전달할 때 사용한다.
 *
 * 포함 정보:
 * - Requirement 기본 정보
 * - Project / WBS 정보
 * - Requirement 상태
 * - 생성/수정 일시
 */
@Getter
@Builder
public class EvidenceRequirementResponse {

    /**
     * Requirement ID
     */
    private Long id;

    /**
     * 프로젝트 ID
     */
    private Long projectId;

    /**
     * WBS ID
     *
     * 프로젝트 단위 Requirement인 경우 null일 수 있다.
     */
    private Long wbsId;

    /**
     * Requirement Key
     *
     * 예:
     * EVR-001
     * EVR-002
     */
    private String requirementKey;

    /**
     * Requirement 제목
     */
    private String title;

    /**
     * Requirement 설명
     */
    private String description;

    /**
     * Requirement 유형
     */
    private RequirementType requirementType;

    /**
     * 필수 증적 여부
     */
    private Boolean required;

    /**
     * 증적 확보 예정일
     */
    private LocalDate dueDate;

    /**
     * Requirement 상태
     *
     * PENDING
     * PRESENT
     * MISSING
     * NOT_REQUIRED
     */
    private RequirementStatus status;

    /**
     * 표시 순서
     */
    private Integer sortOrder;

    /**
     * 생성 일시
     */
    private LocalDateTime createdAt;

    /**
     * 수정 일시
     */
    private LocalDateTime updatedAt;
}