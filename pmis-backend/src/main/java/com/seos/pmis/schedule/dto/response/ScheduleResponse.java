package com.seos.pmis.schedule.dto.response;

import com.seos.pmis.schedule.entity.Schedule;
import com.seos.pmis.schedule.entity.ScheduleStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Schedule Response DTO
 *
 * Schedule Entity의 API 응답을 표현한다.
 *
 * Entity의 Project / Wbs 연관관계는
 * API에서 직접 노출하지 않고 ID만 반환한다.
 */
@Getter
@Builder
public class ScheduleResponse {

    /**
     * Schedule PK
     */
    private Long id;

    /**
     * 소속 Project ID
     */
    private Long projectId;

    /**
     * 연결된 WBS ID
     */
    private Long wbsId;

    /**
     * Schedule 명
     */
    private String scheduleName;

    /**
     * 시작일
     */
    private LocalDate startDate;

    /**
     * 종료일
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

    /**
     * 생성일시
     */
    private LocalDateTime createdAt;

    /**
     * 수정일시
     */
    private LocalDateTime updatedAt;

    /**
     * Schedule Entity → Response DTO 변환
     *
     * @param schedule Schedule Entity
     * @return ScheduleResponse
     */
    public static ScheduleResponse from(Schedule schedule) {

        return ScheduleResponse.builder()
                .id(schedule.getId())
                .projectId(schedule.getProject().getId())
                .wbsId(schedule.getWbs().getId())
                .scheduleName(schedule.getScheduleName())
                .startDate(schedule.getStartDate())
                .endDate(schedule.getEndDate())
                .status(schedule.getStatus())
                .sortOrder(schedule.getSortOrder())
                .description(schedule.getDescription())
                .createdAt(schedule.getCreatedAt())
                .updatedAt(schedule.getUpdatedAt())
                .build();
    }
}