package com.seos.pmis.schedule.dto.request;

import com.seos.pmis.schedule.entity.ScheduleStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Schedule 수정 요청 DTO
 *
 * Project는 변경하지 않는다.
 *
 * WBS 변경이 필요한 경우
 * Service에서 기존 Project와 WBS의 소속 관계를
 * 검증한 후 Entity의 WBS를 변경한다.
 */
@Getter
@NoArgsConstructor
public class ScheduleUpdateRequest {

    /**
     * 변경할 WBS ID
     */
    private Long wbsId;

    /**
     * Schedule 명
     */
    private String scheduleName;

    /**
     * Schedule 시작일
     */
    private LocalDate startDate;

    /**
     * Schedule 종료일
     */
    private LocalDate endDate;

    /**
     * Schedule 상태
     */
    private ScheduleStatus status;

    /**
     * 동일 WBS 내 표시 순서
     */
    private Integer sortOrder;

    /**
     * Schedule 설명
     */
    private String description;
}