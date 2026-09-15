package com.seos.pmis.risk.entity;

import com.seos.pmis.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(
        name = "risks",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_risk_project_key",
                        columnNames = {"project_id", "risk_key"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Risk extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Risk가 속한 Project ID
     */
    @Column(name = "project_id", nullable = false)
    private Long projectId;

    /**
     * Project별 Risk Key
     * 예: RISK-001, RISK-002
     */
    @Column(name = "risk_key", length = 50)
    private String riskKey;

    /**
     * Risk 제목
     */
    @Column(nullable = false, length = 200)
    private String title;

    /**
     * Risk 설명
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Risk 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RiskStatus status;

    /**
     * Risk 우선순위
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RiskPriority priority;

    /**
     * 발생 가능성
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RiskProbability probability;

    /**
     * 영향도
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RiskImpact impact;

    /**
     * 담당자 ID
     */
    @Column(name = "assignee_id")
    private Long assigneeId;

    /**
     * 등록자 ID
     */
    @Column(name = "reporter_id")
    private Long reporterId;

    /**
     * Risk 식별일
     */
    @Column(name = "identified_date")
    private LocalDate identifiedDate;

    /**
     * 대응 목표일
     */
    @Column(name = "due_date")
    private LocalDate dueDate;

    /**
     * Risk 완화일
     */
    @Column(name = "mitigated_date")
    private LocalDate mitigatedDate;

    /**
     * Risk 대응 계획
     */
    @Column(name = "response_plan", columnDefinition = "TEXT")
    private String responsePlan;

    /**
     * 화면 정렬 순서
     */
    @Column(name = "sort_order")
    private Integer sortOrder;

    public Risk(
            Long projectId,
            String title,
            String description,
            RiskStatus status,
            RiskPriority priority,
            RiskProbability probability,
            RiskImpact impact,
            Long assigneeId,
            Long reporterId,
            LocalDate identifiedDate,
            LocalDate dueDate,
            LocalDate mitigatedDate,
            String responsePlan,
            Integer sortOrder
    ) {
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.probability = probability;
        this.impact = impact;
        this.assigneeId = assigneeId;
        this.reporterId = reporterId;
        this.identifiedDate = identifiedDate;
        this.dueDate = dueDate;
        this.mitigatedDate = mitigatedDate;
        this.responsePlan = responsePlan;
        this.sortOrder = sortOrder;
    }

    public void update(
            String title,
            String description,
            RiskPriority priority,
            RiskProbability probability,
            RiskImpact impact,
            Long assigneeId,
            LocalDate identifiedDate,
            LocalDate dueDate,
            String responsePlan,
            Integer sortOrder
    ) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.probability = probability;
        this.impact = impact;
        this.assigneeId = assigneeId;
        this.identifiedDate = identifiedDate;
        this.dueDate = dueDate;
        this.responsePlan = responsePlan;
        this.sortOrder = sortOrder;
    }

    public void updateStatus(
            RiskStatus status,
            LocalDate mitigatedDate
    ) {
        this.status = status;
        this.mitigatedDate = mitigatedDate;
    }

    public void assignRiskKey(String riskKey) {
        this.riskKey = riskKey;
    }
}