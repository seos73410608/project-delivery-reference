package com.seos.pmis.risk.dto.request;

import com.seos.pmis.risk.entity.RiskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RiskStatusUpdateRequest {

    @NotNull
    private RiskStatus status;
}