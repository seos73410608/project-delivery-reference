package com.seos.pmis.evidence.enums;

/**
 * Evidence 검증 상태
 */
public enum VerificationStatus {

    /**
     * 검증 대기
     */
    PENDING,

    /**
     * 검증 승인
     */
    APPROVED,

    /**
     * 검증 반려
     */
    REJECTED
}