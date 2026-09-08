package com.seos.pmis.schedule.dto.response;

import com.seos.pmis.schedule.entity.Schedule;
import com.seos.pmis.schedule.entity.ScheduleStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

/**
 * Schedule Calendar Response
 *
 * Calendar 화면에서 사용하는
 * Schedule 일정 정보이다.
 *
 * Calendar 조회 기간과 겹치는
 * Schedule 정보를 반환한다.
 *
 * Calendar는 Schedule Domain 데이터를
 * 시각화하기 위한 기능이며,
 * 별도의 Calendar Entity를 생성하지 않는다.
 */
@Getter
@Builder
public class ScheduleCalendarResponse {

    /**
     * Schedule PK
     */
    private final Long id;

    /**
     * Project ID
     */
    private final Long projectId;

    /**
     * WBS ID
     */
    private final Long wbsId;

    /**
     * Schedule 명
     */
    private final String scheduleName;

    /**
     * Schedule 시작일
     */
    private final LocalDate startDate;

    /**
     * Schedule 종료일
     */
    private final LocalDate endDate;

    /**
     * Schedule 상태
     */
    private final ScheduleStatus status;

    /**
     * Schedule Entity를
     * ScheduleCalendarResponse로 변환한다.
     *
     * @param schedule Schedule Entity
     * @return ScheduleCalendarResponse
     */
    public static ScheduleCalendarResponse from(
            Schedule schedule
    ) {

        return ScheduleCalendarResponse.builder()
                .id(schedule.getId())
                .projectId(
                        schedule.getProject().getId()
                )
                .wbsId(
                        schedule.getWbs().getId()
                )
                .scheduleName(
                        schedule.getScheduleName()
                )
                .startDate(
                        schedule.getStartDate()
                )
                .endDate(
                        schedule.getEndDate()
                )
                .status(
                        schedule.getStatus()
                )
                .build();
    }
}