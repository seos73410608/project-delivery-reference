
package com.seos.pmis.projectoperation.entity;

/**
 * 프로젝트와 운영 서비스의 관계 유형
 */
public enum ProjectOperationRelationshipType {

    /**
     * 프로젝트 구축 결과로 생성된 운영 서비스
     */
    BUILD_RESULT,

    /**
     * 기존 운영 서비스의 기능 개선
     */
    ENHANCEMENT,

    /**
     * 기존 시스템 또는 서비스의 이전
     */
    MIGRATION,

    /**
     * 기타 관계
     */
    OTHER
}
