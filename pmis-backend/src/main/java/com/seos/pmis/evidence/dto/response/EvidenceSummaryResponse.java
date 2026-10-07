package com.seos.pmis.evidence.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * Evidence Summary 응답 DTO
 *
 * 프로젝트의 Evidence Requirement 및
 * Evidence Verification 현황을 집계하여
 * Dashboard / Report / Summary 화면에 전달한다.
 *
 * 주요 지표:
 * - Requirement 현황
 * - Evidence 확보 현황
 * - Verification 현황
 * - 기한 초과 현황
 * - Completion / Approval 비율
 */
@Getter
@Builder
public class EvidenceSummaryResponse {

    /**
     * 전체 Evidence Requirement 수
     */
    private long totalRequirements;

    /**
     * 필수 Evidence Requirement 수
     */
    private long requiredRequirements;

    /**
     * Evidence가 확보된 Requirement 수
     */
    private long presentRequirements;

    /**
     * Evidence가 누락된 Requirement 수
     */
    private long missingRequirements;

    /**
     * 아직 Evidence가 확보되지 않은 Requirement 수
     */
    private long pendingRequirements;

    /**
     * 증적이 필요하지 않은 Requirement 수
     */
    private long notRequiredRequirements;

    /**
     * Verification 대기 중인 Evidence 수
     */
    private long verificationPending;

    /**
     * Verification 승인 Evidence 수
     */
    private long verificationApproved;

    /**
     * Verification 반려 Evidence 수
     */
    private long verificationRejected;

    /**
     * 기한이 초과된 Requirement 수
     */
    private long overdueRequirements;

    /**
     * Evidence가 확보된 Requirement의 비율
     *
     * 0 ~ 100 범위의 퍼센트 값이다.
     */
    private double evidenceCompletionRate;

    /**
     * Verification 승인 비율
     *
     * Verification 대상 Evidence 중
     * 승인된 Evidence의 비율이다.
     *
     * 0 ~ 100 범위의 퍼센트 값이다.
     */
    private double verificationApprovalRate;
}