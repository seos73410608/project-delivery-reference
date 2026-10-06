package com.seos.pmis.evidence.enums;

/**
 * Inspection 상태
 *
 * Lifecycle:
 * PLANNED
 *     ↓
 * IN_PROGRESS
 *     ↓
 * PASSED / FAILED
 *     ↓
 * CLOSED
 */
public enum InspectionStatus {

    /**
     * 검사 예정
     */
    PLANNED,

    /**
     * 검사 진행 중
     */
    IN_PROGRESS,

    /**
     * 검사 통과
     */
    PASSED,

    /**
     * 검사 실패
     */
    FAILED,

    /**
     * 검사 종료
     */
    CLOSED
}