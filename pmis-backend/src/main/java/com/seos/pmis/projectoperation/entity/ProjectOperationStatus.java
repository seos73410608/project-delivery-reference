
package com.seos.pmis.projectoperation.entity;

/**
 * 프로젝트와 운영 서비스 간 관계 상태
 */
public enum ProjectOperationStatus {

    /**
     * 관계 예정
     */
    PLANNED,

    /**
     * 운영 관계 활성
     */
    ACTIVE,

    /**
     * 관계 완료
     */
    COMPLETED,

    /**
     * 관계 취소
     */
    CANCELLED
}
