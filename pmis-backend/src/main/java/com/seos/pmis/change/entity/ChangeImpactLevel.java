package com.seos.pmis.change.entity;

/**
 * Change Management 영향도 수준
 */
public enum ChangeImpactLevel {

    /**
     * 낮은 영향도
     */
    LOW,

    /**
     * 보통 영향도
     */
    MEDIUM,

    /**
     * 높은 영향도
     */
    HIGH,

    /**
     * 심각/최상위 영향도
     */
    CRITICAL
}