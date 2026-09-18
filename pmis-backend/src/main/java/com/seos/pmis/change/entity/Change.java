package com.seos.pmis.change.entity;

import com.seos.pmis.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(
        name = "changes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_change_project_key",
                        columnNames = {"project_id", "change_key"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Change extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "change_key", length = 50)
    private String changeKey;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ChangeStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChangePriority priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "change_type", nullable = false, length = 30)
    private ChangeType changeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "impact_level", nullable = false, length = 20)
    private ChangeImpactLevel impactLevel;

    @Column(name = "requester_id")
    private Long requesterId;

    @Column(name = "assignee_id")
    private Long assigneeId;

    @Column(name = "identified_date")
    private LocalDate identifiedDate;

    @Column(name = "requested_date")
    private LocalDate requestedDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "approved_date")
    private LocalDate approvedDate;

    @Column(name = "implemented_date")
    private LocalDate implementedDate;

    @Column(name = "verified_date")
    private LocalDate verifiedDate;

    @Column(name = "closed_date")
    private LocalDate closedDate;

    @Column(name = "impact_analysis", columnDefinition = "TEXT")
    private String impactAnalysis;

    @Column(name = "implementation_plan", columnDefinition = "TEXT")
    private String implementationPlan;

    @Column(name = "verification_result", columnDefinition = "TEXT")
    private String verificationResult;

    @Column(name = "approval_comment", columnDefinition = "TEXT")
    private String approvalComment;

    @Column(name = "sort_order")
    private Integer sortOrder;

    public Change(
            Long projectId,
            String title,
            String description,
            ChangeStatus status,
            ChangePriority priority,
            ChangeType changeType,
            ChangeImpactLevel impactLevel,
            Long requesterId,
            Long assigneeId,
            LocalDate identifiedDate,
            LocalDate requestedDate,
            LocalDate dueDate,
            LocalDate approvedDate,
            LocalDate implementedDate,
            LocalDate verifiedDate,
            LocalDate closedDate,
            String impactAnalysis,
            String implementationPlan,
            String verificationResult,
            String approvalComment,
            Integer sortOrder
    ) {
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.changeType = changeType;
        this.impactLevel = impactLevel;
        this.requesterId = requesterId;
        this.assigneeId = assigneeId;
        this.identifiedDate = identifiedDate;
        this.requestedDate = requestedDate;
        this.dueDate = dueDate;
        this.approvedDate = approvedDate;
        this.implementedDate = implementedDate;
        this.verifiedDate = verifiedDate;
        this.closedDate = closedDate;
        this.impactAnalysis = impactAnalysis;
        this.implementationPlan = implementationPlan;
        this.verificationResult = verificationResult;
        this.approvalComment = approvalComment;
        this.sortOrder = sortOrder;
    }

    public void update(
            String title,
            String description,
            ChangePriority priority,
            ChangeType changeType,
            ChangeImpactLevel impactLevel,
            Long requesterId,
            Long assigneeId,
            LocalDate identifiedDate,
            LocalDate requestedDate,
            LocalDate dueDate,
            String impactAnalysis,
            String implementationPlan,
            Integer sortOrder
    ) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.changeType = changeType;
        this.impactLevel = impactLevel;
        this.requesterId = requesterId;
        this.assigneeId = assigneeId;
        this.identifiedDate = identifiedDate;
        this.requestedDate = requestedDate;
        this.dueDate = dueDate;
        this.impactAnalysis = impactAnalysis;
        this.implementationPlan = implementationPlan;
        this.sortOrder = sortOrder;
    }

    public void updateStatus(
            ChangeStatus status,
            LocalDate approvedDate,
            LocalDate implementedDate,
            LocalDate verifiedDate,
            LocalDate closedDate
    ) {
        this.status = status;
        this.approvedDate = approvedDate;
        this.implementedDate = implementedDate;
        this.verifiedDate = verifiedDate;
        this.closedDate = closedDate;
    }

    public void updateApproval(
            ChangeStatus status,
            LocalDate approvedDate,
            String approvalComment
    ) {
        this.status = status;
        this.approvedDate = approvedDate;
        this.approvalComment = approvalComment;
    }

    public void assignChangeKey(String changeKey) {
        this.changeKey = changeKey;
    }

    public void updateVerificationResult(
            String verificationResult
    ) {
        this.verificationResult = verificationResult;
    }
}