package com.seos.pmis.wbs.entity;

/**
 * WBS 상태
 */
public enum WbsStatus {

    /**
     * 계획
     */
    PLANNED,

    /**
     * 진행 중
     */
    IN_PROGRESS,

    /**
     * 완료
     */
    COMPLETED,

    /**
     * 보류
     */
    ON_HOLD,

    /**
     * 취소
     */
    CANCELLED
}