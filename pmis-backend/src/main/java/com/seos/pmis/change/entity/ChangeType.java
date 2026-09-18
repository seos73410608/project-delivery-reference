package com.seos.pmis.change.entity;

/**
 * Change Management 변경 유형
 */
public enum ChangeType {

    /**
     * 범위 변경
     */
    SCOPE,

    /**
     * 일정 변경
     */
    SCHEDULE,

    /**
     * 비용 변경
     */
    COST,

    /**
     * 품질 변경
     */
    QUALITY,

    /**
     * 기술 변경
     */
    TECHNICAL,

    /**
     * 인프라 변경
     */
    INFRASTRUCTURE,

    /**
     * 보안 변경
     */
    SECURITY,

    /**
     * 데이터 변경
     */
    DATA,

    /**
     * 인터페이스 변경
     */
    INTERFACE,

    /**
     * 운영 변경
     */
    OPERATION,

    /**
     * 요구사항 변경
     */
    REQUIREMENT,

    /**
     * 기타 변경
     */
    OTHER
}