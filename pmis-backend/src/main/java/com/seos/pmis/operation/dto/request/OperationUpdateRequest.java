package com.seos.pmis.operation.dto.request;

import com.seos.pmis.operation.entity.OperationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class OperationUpdateRequest {

    @NotBlank(message = "운영 서비스명은 필수입니다.")
    @Size(max = 200, message = "운영 서비스명은 200자 이하여야 합니다.")
    private String operationName;

    @NotNull(message = "운영 서비스 유형은 필수입니다.")
    private OperationType operationType;

    private Long managerUserId;

    private String description;

    private LocalDate effectiveFrom;

    private LocalDate effectiveTo;
}