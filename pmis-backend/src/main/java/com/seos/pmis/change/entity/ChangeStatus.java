package com.seos.pmis.change.entity;

/**
 * Change Management 상태
 *
 * Lifecycle:
 * REQUESTED
 *     ↓
 * ANALYZING
 *     ↓
 * PENDING_APPROVAL
 *     ├── APPROVED
 *     │     ↓
 *     │  IMPLEMENTING
 *     │     ↓
 *     │  VERIFIED
 *     │     ↓
 *     │  CLOSED
 *     │
 *     └── REJECTED
 *
 * REQUESTED / ANALYZING / PENDING_APPROVAL
 *     └── CANCELLED
 */
public enum ChangeStatus {

    /**
     * 변경 요청 등록
     */
    REQUESTED,

    /**
     * 영향도 및 변경 내용 분석 중
     */
    ANALYZING,

    /**
     * 승인 대기
     */
    PENDING_APPROVAL,

    /**
     * 변경 승인
     */
    APPROVED,

    /**
     * 변경 반려
     */
    REJECTED,

    /**
     * 변경 구현 진행 중
     */
    IMPLEMENTING,

    /**
     * 변경 구현 완료 및 검증 완료
     */
    VERIFIED,

    /**
     * 변경 작업 종료
     */
    CLOSED,

    /**
     * 변경 요청 취소
     */
    CANCELLED
}