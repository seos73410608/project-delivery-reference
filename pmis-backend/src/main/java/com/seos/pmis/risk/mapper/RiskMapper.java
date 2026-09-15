package com.seos.pmis.risk.mapper;

import com.seos.pmis.risk.dto.request.RiskCreateRequest;
import com.seos.pmis.risk.dto.request.RiskUpdateRequest;
import com.seos.pmis.risk.dto.response.RiskResponse;
import com.seos.pmis.risk.entity.Risk;
import org.springframework.stereotype.Component;

@Component
public class RiskMapper {

    public Risk toEntity(
            Long projectId,
            RiskCreateRequest request
    ) {
        return new Risk(
                projectId,
                request.getTitle(),
                request.getDescription(),
                null,
                request.getPriority(),
                request.getProbability(),
                request.getImpact(),
                request.getAssigneeId(),
                request.getReporterId(),
                request.getIdentifiedDate(),
                request.getDueDate(),
                null,
                request.getResponsePlan(),
                request.getSortOrder()
        );
    }

    public void updateEntity(
            Risk risk,
            RiskUpdateRequest request
    ) {
        risk.update(
                request.getTitle(),
                request.getDescription(),
                request.getPriority(),
                request.getProbability(),
                request.getImpact(),
                request.getAssigneeId(),
                request.getIdentifiedDate(),
                request.getDueDate(),
                request.getResponsePlan(),
                request.getSortOrder()
        );
    }

    public RiskResponse toResponse(
            Risk risk
    ) {
        return RiskResponse.from(risk);
    }
}