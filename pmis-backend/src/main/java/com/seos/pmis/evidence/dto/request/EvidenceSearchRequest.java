package com.seos.pmis.evidence.dto.request;

import com.seos.pmis.evidence.enums.EvidenceType;
import com.seos.pmis.evidence.enums.VerificationStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;

/**
 * Evidence 검색 요청 DTO
 *
 * Evidence 목록 조회 시
 * 검색 / 필터 / 페이징 / 정렬 조건을 전달한다.
 *
 * 검색 조건:
 * - projectId
 * - requirementId
 * - wbsId
 * - keyword
 * - evidenceType
 * - verificationStatus
 * - page
 * - size
 * - sortBy
 * - direction
 */
@Getter
@Setter
@NoArgsConstructor
public class EvidenceSearchRequest {

    /**
     * 프로젝트 ID
     *
     * Global Evidence 검색 API에서 사용한다.
     */
    private Long projectId;

    /**
     * Evidence Requirement ID
     *
     * 특정 Requirement에 연결된 Evidence를 검색할 때 사용한다.
     */
    private Long requirementId;

    /**
     * WBS ID
     *
     * 특정 WBS에 연결된 Evidence를 검색할 때 사용한다.
     */
    private Long wbsId;

    /**
     * 검색어
     *
     * Evidence Key 또는 제목을 기준으로 검색한다.
     */
    private String keyword;

    /**
     * Evidence 유형
     */
    private EvidenceType evidenceType;

    /**
     * 검증 상태
     *
     * PENDING
     * APPROVED
     * REJECTED
     */
    private VerificationStatus verificationStatus;

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
     * submittedAt
     */
    private String sortBy = "submittedAt";

    /**
     * 정렬 방향
     *
     * 기본값:
     * DESC
     */
    private Sort.Direction direction = Sort.Direction.DESC;
}