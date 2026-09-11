package com.seos.pmis.issue.entity;

/**
 * Issue Status
 *
 * 프로젝트 수행 중 발생한
 * Issue의 처리 상태를 표현한다.
 */
public enum IssueStatus {

    /**
     * 신규 Issue
     */
    OPEN,

    /**
     * 조치 진행 중
     */
    IN_PROGRESS,

    /**
     * 해결 완료
     */
    RESOLVED,

    /**
     * 최종 종료
     */
    CLOSED,

    /**
     * 보류
     */
    ON_HOLD,

    /**
     * 취소
     */
    CANCELLED
}