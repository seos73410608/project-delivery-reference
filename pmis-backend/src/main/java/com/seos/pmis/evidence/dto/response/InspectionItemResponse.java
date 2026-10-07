package com.seos.pmis.evidence.dto.response;

import com.seos.pmis.evidence.enums.InspectionItemResult;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Inspection Item 응답 DTO
 *
 * Inspection Item 조회 결과를
 * Client에게 전달할 때 사용한다.
 *
 * 포함 정보:
 * - Inspection 정보
 * - Evidence Requirement 정보
 * - Evidence 정보
 * - 검사 결과
 * - 비고
 * - 생성/수정 일시
 *
 * 연관 Entity 자체는 전달하지 않고
 * 필요한 식별자와 표시 정보만 전달한다.
 */
@Getter
@Builder
public class InspectionItemResponse {

    /**
     * Inspection Item ID
     */
    private Long id;

    /**
     * Inspection ID
     */
    private Long inspectionId;

    /**
     * Evidence Requirement ID
     */
    private Long requirementId;

    /**
     * Evidence Requirement Key
     *
     * 예:
     * EVR-001
     */
    private String requirementKey;

    /**
     * Evidence Requirement 제목
     */
    private String requirementTitle;

    /**
     * 연결된 Evidence ID
     *
     * 연결된 Evidence가 없는 경우 null이다.
     */
    private Long evidenceId;

    /**
     * 연결된 Evidence Key
     *
     * 연결된 Evidence가 없는 경우 null이다.
     */
    private String evidenceKey;

    /**
     * 연결된 Evidence 제목
     *
     * 연결된 Evidence가 없는 경우 null이다.
     */
    private String evidenceTitle;

    /**
     * 검사 결과
     *
     * PENDING
     * PASSED
     * FAILED
     * NOT_APPLICABLE
     */
    private InspectionItemResult result;

    /**
     * 검사 비고
     */
    private String remark;

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