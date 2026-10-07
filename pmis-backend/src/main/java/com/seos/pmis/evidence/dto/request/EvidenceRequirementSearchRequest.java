package com.seos.pmis.evidence.dto.request;

import com.seos.pmis.evidence.enums.RequirementStatus;
import com.seos.pmis.evidence.enums.RequirementType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;

/**
 * Evidence Requirement 검색 요청 DTO
 *
 * Evidence Requirement 목록 조회 시
 * 검색 / 필터 / 페이징 / 정렬 조건을 전달한다.
 *
 * 검색 조건:
 * - projectId
 * - wbsId
 * - keyword
 * - requirementType
 * - required
 * - status
 * - page
 * - size
 * - sortBy
 * - direction
 */
@Getter
@Setter
@NoArgsConstructor
public class EvidenceRequirementSearchRequest {

    /**
     * 프로젝트 ID
     *
     * 전체 Evidence Requirement 검색 시 사용한다.
     *
     * 프로젝트별 API에서는 PathVariable로 전달된
     * projectId를 Service에서 우선 사용할 수 있다.
     */
    private Long projectId;

    /**
     * WBS ID
     */
    private Long wbsId;

    /**
     * 검색어
     *
     * Requirement Key 또는 제목을 대상으로
     * 검색할 수 있도록 Service/Specification에서 처리한다.
     */
    private String keyword;

    /**
     * Requirement 유형
     */
    private RequirementType requirementType;

    /**
     * 필수 증적 여부
     */
    private Boolean required;

    /**
     * Requirement 상태
     */
    private RequirementStatus status;

    /**
     * 페이지 번호
     *
     * 0부터 시작한다.
     */
    private Integer page = 0;

    /**
     * 페이지 크기
     */
    private Integer size = 20;

    /**
     * 정렬 기준
     *
     * 기본값:
     * sortOrder
     */
    private String sortBy = "sortOrder";

    /**
     * 정렬 방향
     *
     * ASC / DESC
     */
    private Sort.Direction direction = Sort.Direction.ASC;
}