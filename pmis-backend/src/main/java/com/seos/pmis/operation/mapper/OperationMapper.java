package com.seos.pmis.operation.mapper;

import com.seos.pmis.operation.dto.response.OperationResponse;
import com.seos.pmis.operation.entity.Operation;
import com.seos.pmis.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class OperationMapper {

    public OperationResponse toResponse(Operation operation) {
        User manager = operation.getManager();

        return OperationResponse.builder()
                .id(operation.getId())
                .operationCode(operation.getOperationCode())
                .operationName(operation.getOperationName())
                .operationType(operation.getOperationType())
                .status(operation.getStatus())
                .managerUserId(manager != null ? manager.getId() : null)
                .managerName(manager != null ? manager.getName() : null)
                .description(operation.getDescription())
                .effectiveFrom(operation.getEffectiveFrom())
                .effectiveTo(operation.getEffectiveTo())
                .createdAt(operation.getCreatedAt())
                .updatedAt(operation.getUpdatedAt())
                .build();
    }
}