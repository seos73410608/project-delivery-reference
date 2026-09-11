package com.seos.pmis.issue.entity;

/**
 * Issue Priority
 *
 * Issue의 중요도 및
 * 프로젝트 영향도를 표현한다.
 */
public enum IssuePriority {

    /**
     * 낮음
     */
    LOW,

    /**
     * 보통
     */
    MEDIUM,

    /**
     * 높음
     */
    HIGH,

    /**
     * 긴급
     */
    CRITICAL
}