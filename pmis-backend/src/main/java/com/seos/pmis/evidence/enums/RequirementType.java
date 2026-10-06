package com.seos.pmis.evidence.enums;

/**
 * Evidence Requirement 유형
 */
public enum RequirementType {

    /**
     * 설치 결과 증적
     */
    INSTALLATION,

    /**
     * 설정 결과 증적
     */
    CONFIGURATION,

    /**
     * 계정 생성/권한 증적
     */
    ACCOUNT,

    /**
     * 테스트 결과 증적
     */
    TEST,

    /**
     * 데이터 이관 증적
     */
    MIGRATION,

    /**
     * 검사 결과 증적
     */
    INSPECTION,

    /**
     * 보고서 증적
     */
    REPORT,

    /**
     * 운영 관련 증적
     */
    OPERATION,

    /**
     * 보안 관련 증적
     */
    SECURITY,

    /**
     * 기타 증적
     */
    OTHER
}