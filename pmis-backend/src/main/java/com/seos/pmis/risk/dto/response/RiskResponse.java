package com.seos.pmis.risk.dto.response;

import com.seos.pmis.risk.entity.Risk;
import com.seos.pmis.risk.entity.RiskImpact;
import com.seos.pmis.risk.entity.RiskPriority;
import com.seos.pmis.risk.entity.RiskProbability;
import com.seos.pmis.risk.entity.RiskStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class RiskResponse {

    private Long id;

    private Long projectId;

    private String riskKey;

    private String title;

    private String description;

    private RiskStatus status;

    private RiskPriority priority;

    private RiskProbability probability;

    private RiskImpact impact;

    private Integer riskScore;

    private Long assigneeId;

    private Long reporterId;

    private LocalDate identifiedDate;

    private LocalDate dueDate;

    private LocalDate mitigatedDate;

    private String responsePlan;

    private Integer sortOrder;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static RiskResponse from(Risk risk) {
        return RiskResponse.builder()
                .id(risk.getId())
                .projectId(risk.getProjectId())
                .riskKey(risk.getRiskKey())
                .title(risk.getTitle())
                .description(risk.getDescription())
                .status(risk.getStatus())
                .priority(risk.getPriority())
                .probability(risk.getProbability())
                .impact(risk.getImpact())
                .riskScore(calculateRiskScore(
                        risk.getProbability(),
                        risk.getImpact()
                ))
                .assigneeId(risk.getAssigneeId())
                .reporterId(risk.getReporterId())
                .identifiedDate(risk.getIdentifiedDate())
                .dueDate(risk.getDueDate())
                .mitigatedDate(risk.getMitigatedDate())
                .responsePlan(risk.getResponsePlan())
                .sortOrder(risk.getSortOrder())
                .createdAt(risk.getCreatedAt())
                .updatedAt(risk.getUpdatedAt())
                .build();
    }

    private static Integer calculateRiskScore(
            RiskProbability probability,
            RiskImpact impact
    ) {
        if (probability == null || impact == null) {
            return null;
        }

        return probabilityScore(probability)
                * impactScore(impact);
    }

    private static int probabilityScore(RiskProbability probability) {
        return switch (probability) {
            case LOW -> 1;
            case MEDIUM -> 2;
            case HIGH -> 3;
        };
    }

    private static int impactScore(RiskImpact impact) {
        return switch (impact) {
            case LOW -> 1;
            case MEDIUM -> 2;
            case HIGH -> 3;
            case CRITICAL -> 4;
        };
    }
}