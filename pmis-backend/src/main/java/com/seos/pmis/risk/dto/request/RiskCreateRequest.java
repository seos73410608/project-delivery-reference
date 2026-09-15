package com.seos.pmis.risk.dto.request;

import com.seos.pmis.risk.entity.RiskImpact;
import com.seos.pmis.risk.entity.RiskPriority;
import com.seos.pmis.risk.entity.RiskProbability;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class RiskCreateRequest {

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private RiskPriority priority;

    @NotNull
    private RiskProbability probability;

    @NotNull
    private RiskImpact impact;

    private Long assigneeId;

    private Long reporterId;

    private LocalDate identifiedDate;

    private LocalDate dueDate;

    private String responsePlan;

    private Integer sortOrder;
}