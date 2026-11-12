package com.seos.cmdb.ci.dto.request;

import com.seos.cmdb.ci.entity.CiStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ChangeConfigurationItemStatusRequest {

    @NotNull(message = "CI 상태는 필수입니다.")
    private CiStatus status;
}