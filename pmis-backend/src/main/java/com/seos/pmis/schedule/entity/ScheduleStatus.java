package com.seos.pmis.schedule.entity;

/**
 * Schedule 상태
 *
 * Schedule의 현재 진행 상태를 표현한다.
 *
 * 상태 변경은 Service Layer를 통해 수행하며
 * Frontend에서는 정의되지 않은 상태 값을 사용하지 않는다.
 */
public enum ScheduleStatus {

    /**
     * 계획됨
     *
     * 일정이 생성되었으나 아직 시작되지 않은 상태
     */
    PLANNED,

    /**
     * 진행 중
     *
     * 현재 일정이 진행되고 있는 상태
     */
    IN_PROGRESS,

    /**
     * 완료
     *
     * 일정이 정상적으로 완료된 상태
     */
    COMPLETED,

    /**
     * 보류
     *
     * 일정 진행이 일시적으로 중단된 상태
     */
    ON_HOLD,

    /**
     * 취소
     *
     * 해당 일정이 더 이상 진행되지 않는 상태
     */
    CANCELLED
}