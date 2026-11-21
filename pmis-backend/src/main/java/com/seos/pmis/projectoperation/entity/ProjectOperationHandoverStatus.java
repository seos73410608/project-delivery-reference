
package com.seos.pmis.projectoperation.entity;

/**
 * 프로젝트 운영 인수인계 진행 상태
 */
public enum ProjectOperationHandoverStatus {

    /**
     * 작성 중
     */
    DRAFT,

    /**
     * 인수인계 요청
     */
    REQUESTED,

    /**
     * 인수인계 진행 중
     */
    IN_PROGRESS,

    /**
     * 인수인계 검토
     */
    REVIEW,

    /**
     * 인수인계 승인 완료
     */
    ACCEPTED,

    /**
     * 인수인계 완료
     */
    COMPLETED,

    /**
     * 인수인계 반려
     */
    REJECTED,

    /**
     * 인수인계 취소
     */
    CANCELLED
}
