package com.seos.pmis.operation.dto.response;

import com.seos.pmis.operation.entity.OperationStatus;
import com.seos.pmis.operation.entity.OperationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class OperationResponse {

    private Long id;

    private String operationCode;

    private String operationName;

    private OperationType operationType;

    private OperationStatus status;

    private Long managerUserId;

    private String managerName;

    private String description;

    private LocalDate effectiveFrom;

    private LocalDate effectiveTo;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}