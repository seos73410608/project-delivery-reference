package com.seos.pmis.project.entity;

import com.seos.pmis.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Entity
@Builder
@Table(name = "projects")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Project extends BaseEntity {

    /**
     * 프로젝트 PK
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 프로젝트 코드
     * 예) PMIS-2026-001
     */
    @Column(nullable = false, unique = true, length = 30)
    private String projectCode;

    /**
     * 프로젝트명
     */
    @Column(nullable = false, length = 200)
    private String projectName;

    /**
     * 고객사
     */
    @Column(nullable = false, length = 100)
    private String customerName;

    /**
     * 프로젝트 관리자(PM)
     */
    @Column(nullable = false, length = 100)
    private String projectManager;

    /**
     * 프로젝트 설명
     */
    @Lob
    private String description;

    /**
     * 시작일
     */
    @Column(nullable = false)
    private LocalDate startDate;

    /**
     * 종료 예정일
     */
    @Column(nullable = false)
    private LocalDate endDate;

    /**
     * 프로젝트 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProjectStatus status;

    /**
     * 프로젝트 우선순위
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProjectPriority priority;

    /**
     * 프로젝트 수정
     */
    public void update(
            String projectName,
            String customerName,
            String projectManager,
            String description,
            LocalDate startDate,
            LocalDate endDate,
            ProjectStatus status,
            ProjectPriority priority
    ) {
        this.projectName = projectName;
        this.customerName = customerName;
        this.projectManager = projectManager;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.priority = priority;
    }

    /**
     * 프로젝트 상태 변경
     */
    public void changeStatus(ProjectStatus status) {
        this.status = status;
    }

    /**
     * 프로젝트 우선순위 변경
     */
    public void changePriority(ProjectPriority priority) {
        this.priority = priority;
    }
}