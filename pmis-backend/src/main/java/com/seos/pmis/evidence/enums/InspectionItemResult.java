package com.seos.pmis.evidence.enums;

/**
 * Inspection Item 검사 결과
 */
public enum InspectionItemResult {

    /**
     * 검사 결과 대기
     */
    PENDING,

    /**
     * 검사 통과
     */
    PASSED,

    /**
     * 검사 실패
     */
    FAILED,

    /**
     * 검사 대상 아님
     */
    NOT_APPLICABLE
}