package com.seos.pmis.schedule.entity;

import com.seos.pmis.common.entity.BaseEntity;
import com.seos.pmis.project.entity.Project;
import com.seos.pmis.wbs.entity.Wbs;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Schedule Entity
 *
 * 프로젝트의 일정 정보를 표현한다.
 *
 * Schedule은 하나의 Project와 하나의 WBS에 소속된다.
 *
 * Domain 관계:
 *
 * Project
 *   └── WBS
 *        └── Schedule
 *
 * Schedule은 시간 정보를 관리하며,
 * WBS의 업무 구조와는 책임을 분리한다.
 */
@Getter
@Entity
@Builder
@Table(name = "schedules")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Schedule extends BaseEntity {

    /**
     * Schedule PK
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 소속 프로젝트
     *
     * 하나의 Schedule은 반드시 하나의 Project에 속한다.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "project_id",
            nullable = false
    )
    private Project project;

    /**
     * 연결된 WBS
     *
     * 하나의 Schedule은 하나의 WBS에 연결된다.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "wbs_id",
            nullable = false
    )
    private Wbs wbs;

    /**
     * Schedule 명
     */
    @Column(
            name = "schedule_name",
            nullable = false,
            length = 200
    )
    private String scheduleName;

    /**
     * Schedule 시작일
     */
    @Column(
            name = "start_date",
            nullable = false
    )
    private LocalDate startDate;

    /**
     * Schedule 종료일
     */
    @Column(
            name = "end_date",
            nullable = false
    )
    private LocalDate endDate;

    /**
     * Schedule 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private ScheduleStatus status;

    /**
     * 동일 WBS 내 표시 순서
     */
    @Column(
            name = "sort_order",
            nullable = false
    )
    private Integer sortOrder;

    /**
     * Schedule 설명
     */
    @Lob
    private String description;

    /**
     * Schedule 기본 정보 수정
     *
     * Project는 변경하지 않는다.
     *
     * WBS 변경이 필요한 경우
     * Service에서 Project/WBS 소속 관계를 검증한 후
     * changeWbs()를 별도로 수행한다.
     *
     * @param scheduleName Schedule 명
     * @param startDate 시작일
     * @param endDate 종료일
     * @param status 상태
     * @param sortOrder 표시 순서
     * @param description 설명
     */
    public void update(
            String scheduleName,
            LocalDate startDate,
            LocalDate endDate,
            ScheduleStatus status,
            Integer sortOrder,
            String description
    ) {
        this.scheduleName = scheduleName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.sortOrder = sortOrder;
        this.description = description;
    }

    /**
     * Schedule의 WBS 변경
     *
     * WBS 변경에 따른 Project 소속 관계 및
     * 업무 규칙 검증은 Service에서 수행한다.
     *
     * @param wbs 새로운 WBS
     */
    public void changeWbs(Wbs wbs) {
        this.wbs = wbs;
    }

    /**
     * Schedule 상태 변경
     *
     * @param status 새로운 상태
     */
    public void changeStatus(ScheduleStatus status) {
        this.status = status;
    }

    /**
     * Schedule 표시 순서 변경
     *
     * @param sortOrder 새로운 표시 순서
     */
    public void changeSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}