package com.seos.pmis.operation.dto.request;

import com.seos.pmis.operation.entity.OperationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class OperationStatusChangeRequest {

    @NotNull(message = "운영 상태는 필수입니다.")
    private OperationStatus status;
}