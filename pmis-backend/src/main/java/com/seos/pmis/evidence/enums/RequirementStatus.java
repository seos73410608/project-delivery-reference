package com.seos.pmis.evidence.enums;

/**
 * Evidence Requirement 상태
 *
 * Lifecycle:
 * PENDING
 *     ↓
 * Evidence 등록
 *     ↓
 * PRESENT
 *
 * PENDING
 *     └── dueDate 경과 + required
 *             ↓
 *          MISSING
 *
 * NOT_REQUIRED
 *     └── 필수 증적이 아닌 경우
 */
public enum RequirementStatus {

    /**
     * 증적 등록 대기
     */
    PENDING,

    /**
     * 증적 등록 완료
     */
    PRESENT,

    /**
     * 필수 증적 미등록
     */
    MISSING,

    /**
     * 증적 제출이 필요하지 않음
     */
    NOT_REQUIRED
}