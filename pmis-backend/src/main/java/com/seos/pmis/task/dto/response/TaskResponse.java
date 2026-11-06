
package com.seos.pmis.task.dto.response;

import com.seos.pmis.task.entity.Task;
import com.seos.pmis.task.entity.TaskPriority;
import com.seos.pmis.task.entity.TaskStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Task Response DTO
 *
 * Task Entity의 API 응답을 표현한다.
 *
 * Entity의 Project / Wbs / Schedule / User 연관관계는
 * API에서 직접 노출하지 않고 ID와 표시 정보만 반환한다.
 */
@Getter
@Builder
public class TaskResponse {

    /**
     * Task PK
     */
    private Long id;

    /**
     * 소속 Project ID
     */
    private Long projectId;

    /**
     * 소속 WBS ID
     *
     * WBS 미할당 시 null
     */
    private Long wbsId;

    /**
     * 연결 Schedule ID
     */
    private Long scheduleId;

    /**
     * 담당자 User ID
     */
    private Long assigneeId;

    /**
     * 담당자 이름
     */
    private String assigneeName;

    /**
     * 작업명
     */
    private String taskName;

    /**
     * 작업 설명
     */
    private String description;

    /**
     * 작업 상태
     */
    private TaskStatus status;

    /**
     * 작업 진척률
     */
    private Integer progressRate;

    /**
     * 작업 우선순위
     */
    private TaskPriority priority;

    /**
     * 작업 시작일
     */
    private LocalDate startDate;

    /**
     * 작업 종료일
     */
    private LocalDate endDate;

    /**
     * 동일 WBS 내 표시 순서
     */
    private Integer sortOrder;

    /**
     * 계획 공수 (Man-Days)
     */
    private BigDecimal plannedManDays;

    /**
     * 생성일시
     */
    private LocalDateTime createdAt;

    /**
     * 수정일시
     */
    private LocalDateTime updatedAt;

    /**
     * Task Entity → Response DTO 변환
     *
     * @param task Task Entity
     * @return TaskResponse
     */
    public static TaskResponse from(Task task) {

        return TaskResponse.builder()
                .id(task.getId())
                .projectId(task.getProject().getId())
                .wbsId(
                        task.getWbs() != null
                                ? task.getWbs().getId()
                                : null
                )
                .scheduleId(
                        task.getSchedule() != null
                                ? task.getSchedule().getId()
                                : null
                )
                .assigneeId(
                        task.getAssignee() != null
                                ? task.getAssignee().getId()
                                : null
                )
                .assigneeName(
                        task.getAssignee() != null
                                ? task.getAssignee().getName()
                                : null
                )
                .taskName(task.getTaskName())
                .description(task.getDescription())
                .status(task.getStatus())
                .progressRate(task.getProgressRate())
                .priority(task.getPriority())
                .startDate(task.getStartDate())
                .endDate(task.getEndDate())
                .sortOrder(task.getSortOrder())
                .plannedManDays(task.getPlannedManDays())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
