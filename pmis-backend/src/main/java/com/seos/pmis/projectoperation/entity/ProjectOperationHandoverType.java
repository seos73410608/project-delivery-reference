
package com.seos.pmis.projectoperation.entity;

/**
 * 프로젝트 운영 인수인계 유형
 */
public enum ProjectOperationHandoverType {

    /**
     * 최초 운영 인수인계
     */
    INITIAL,

    /**
     * 기존 운영자에서 신규 운영자로 변경
     */
    OPERATOR_CHANGE,

    /**
     * 동일 운영 관계의 재인수인계
     */
    RE_HANDOVER
}
