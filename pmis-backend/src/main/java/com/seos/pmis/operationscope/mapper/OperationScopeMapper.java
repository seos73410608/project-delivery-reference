package com.seos.pmis.operationscope.mapper;

import com.seos.pmis.operation.entity.Operation;
import com.seos.pmis.operationscope.dto.response.OperationScopeResponse;
import com.seos.pmis.operationscope.entity.OperationScope;
import com.seos.pmis.requirement.entity.OperationalScope;
import org.springframework.stereotype.Component;

@Component
public class OperationScopeMapper {

    public OperationScopeResponse toResponse(OperationScope operationScope) {
        Operation operation = operationScope.getOperation();
        OperationalScope operationalScope = operationScope.getOperationalScope();

        return OperationScopeResponse.builder()
                // 관계 정보
                .id(operationScope.getId())
                .status(operationScope.getStatus())
                .effectiveFrom(operationScope.getEffectiveFrom())
                .effectiveTo(operationScope.getEffectiveTo())
                .description(operationScope.getDescription())
                .createdAt(operationScope.getCreatedAt())
                .updatedAt(operationScope.getUpdatedAt())

                // 운영 서비스 정보
                .operationId(operation.getId())
                .operationCode(operation.getOperationCode())
                .operationName(operation.getOperationName())
                .operationType(operation.getOperationType())
                .operationStatus(operation.getStatus())

                // 운영 범위 정보
                .operationalScopeId(operationalScope.getId())
                .scopeCode(operationalScope.getScopeCode())
                .scopeName(operationalScope.getScopeName())
                .scopeType(operationalScope.getScopeType())
                .operationalScopeStatus(operationalScope.getStatus())

                .build();
    }
}