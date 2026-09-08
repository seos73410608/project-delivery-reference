package com.seos.pmis.schedule.dto.request;

import com.seos.pmis.schedule.entity.ScheduleStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class ScheduleCalendarRequest {

    private LocalDate startDate;

    private LocalDate endDate;

    private Long wbsId;

    private ScheduleStatus status;
}