package com.seos.pmis.risk.dto.request;

import com.seos.pmis.risk.entity.RiskImpact;
import com.seos.pmis.risk.entity.RiskPriority;
import com.seos.pmis.risk.entity.RiskProbability;
import com.seos.pmis.risk.entity.RiskStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

@Getter
@Setter
@NoArgsConstructor
public class RiskSearchRequest {

    private Long projectId;

    private String keyword;

    private RiskStatus status;

    private RiskPriority priority;

    private RiskProbability probability;

    private RiskImpact impact;

    private Long assigneeId;

    private int page = 0;

    private int size = 20;

    private String sortBy = "id";

    private Sort.Direction direction = Sort.Direction.DESC;
}