package com.seos.pmis.task.entity;

/**
 * Task 상태
 */
public enum TaskStatus {

    /**
     * 계획
     */
    PLANNED,

    /**
     * 진행 중
     */
    IN_PROGRESS,

    /**
     * 보류
     */
    ON_HOLD,

    /**
     * 완료
     */
    COMPLETED,

    /**
     * 취소
     */
    CANCELLED
}