
package com.seos.pmis.task.entity;

import com.seos.common.entity.BaseEntity;
import com.seos.pmis.project.entity.Project;
import com.seos.pmis.schedule.entity.Schedule;
import com.seos.pmis.user.entity.User;
import com.seos.pmis.wbs.entity.Wbs;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Entity
@Table(name = "tasks")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Task extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 소속 프로젝트
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    /**
     * 소속 WBS (선택 사항)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wbs_id")
    private Wbs wbs;

    /**
     * 연결 일정
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    /**
     * 담당자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private User assignee;

    /**
     * 작업명
     */
    @Column(nullable = false, length = 200)
    private String taskName;

    /**
     * 작업 설명
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 작업 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TaskStatus status;

    /**
     * 작업 진척률 (0~100)
     */
    @Column(nullable = false)
    private Integer progressRate;

    /**
     * 계획 공수 (M/D)
     */
    @Column(name = "planned_man_days", precision = 10, scale = 2)
    private BigDecimal plannedManDays;

    /**
     * 작업 우선순위
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
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
     * 표시 순서
     */
    @Column(nullable = false)
    private Integer sortOrder;

    @Builder
    private Task(
            Project project,
            Wbs wbs,
            Schedule schedule,
            User assignee,
            String taskName,
            String description,
            TaskStatus status,
            Integer progressRate,
            BigDecimal plannedManDays,
            TaskPriority priority,
            LocalDate startDate,
            LocalDate endDate,
            Integer sortOrder
    ) {
        this.project = project;
        this.wbs = wbs;
        this.schedule = schedule;
        this.assignee = assignee;
        this.taskName = taskName;
        this.description = description;
        this.status = status != null ? status : TaskStatus.PLANNED;
        this.progressRate = progressRate != null ? progressRate : 0;
        this.plannedManDays = plannedManDays;
        this.priority = priority != null ? priority : TaskPriority.MEDIUM;
        this.startDate = startDate;
        this.endDate = endDate;
        this.sortOrder = sortOrder != null ? sortOrder : 0;

        validateProgressRate(this.progressRate);
        validatePlannedManDays(this.plannedManDays);
        validateDateRange(this.startDate, this.endDate);
    }

    /**
     * 작업 정보 수정
     */
    public void update(
            String taskName,
            String description,
            TaskPriority priority,
            LocalDate startDate,
            LocalDate endDate
    ) {
        validateDateRange(startDate, endDate);

        this.taskName = taskName;
        this.description = description;
        this.priority = priority;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * 계획 공수 변경
     */
    public void changePlannedManDays(BigDecimal plannedManDays) {
        validatePlannedManDays(plannedManDays);
        this.plannedManDays = plannedManDays;
    }

    /**
     * WBS 변경
     */
    public void changeWbs(Wbs wbs) {
        this.wbs = wbs;
    }

    /**
     * Schedule 변경
     */
    public void changeSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    /**
     * 담당자 변경
     */
    public void changeAssignee(User assignee) {
        this.assignee = assignee;
    }

    /**
     * 상태 변경
     */
    public void changeStatus(TaskStatus status) {
        this.status = status;

        if (status == TaskStatus.COMPLETED) {
            this.progressRate = 100;
        }
    }

    /**
     * 진척률 변경
     */
    public void changeProgressRate(Integer progressRate) {
        validateProgressRate(progressRate);

        this.progressRate = progressRate;

        if (progressRate == 100) {
            this.status = TaskStatus.COMPLETED;
        } else if (this.status == TaskStatus.COMPLETED) {
            this.status = TaskStatus.IN_PROGRESS;
        }
    }

    /**
     * 표시 순서 변경
     */
    public void changeSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    private void validateProgressRate(Integer progressRate) {
        if (progressRate == null || progressRate < 0 || progressRate > 100) {
            throw new IllegalArgumentException(
                    "진척률은 0부터 100 사이의 값이어야 합니다."
            );
        }
    }

    private void validatePlannedManDays(BigDecimal plannedManDays) {
        if (plannedManDays == null) {
            return;
        }

        if (plannedManDays.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "계획 공수는 0보다 커야 합니다."
            );
        }

        if (plannedManDays.scale() > 2) {
            throw new IllegalArgumentException(
                    "계획 공수는 소수점 둘째 자리까지 입력할 수 있습니다."
            );
        }

        if (plannedManDays.precision() - plannedManDays.scale() > 8) {
            throw new IllegalArgumentException(
                    "계획 공수의 정수부는 최대 8자리까지 입력할 수 있습니다."
            );
        }
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "작업 시작일은 종료일보다 늦을 수 없습니다."
            );
        }
    }
}
