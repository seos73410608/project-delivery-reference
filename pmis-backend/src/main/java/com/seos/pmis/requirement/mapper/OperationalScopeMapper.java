package com.seos.pmis.requirement.mapper;

import com.seos.pmis.requirement.dto.response.OperationalScopeResponse;
import com.seos.pmis.requirement.entity.OperationalScope;
import com.seos.pmis.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class OperationalScopeMapper {

    /**
     * Entity → Response
     */
    public OperationalScopeResponse toResponse(OperationalScope operationalScope) {

        OperationalScope parentScope = operationalScope.getParentScope();
        User owner = operationalScope.getOwner();

        return OperationalScopeResponse.builder()
                .id(operationalScope.getId())
                .scopeCode(operationalScope.getScopeCode())
                .scopeName(operationalScope.getScopeName())
                .scopeType(operationalScope.getScopeType())
                .status(operationalScope.getStatus())
                .parentScopeId(
                        parentScope != null ? parentScope.getId() : null
                )
                .parentScopeCode(
                        parentScope != null ? parentScope.getScopeCode() : null
                )
                .parentScopeName(
                        parentScope != null ? parentScope.getScopeName() : null
                )
                .ownerId(owner != null ? owner.getId() : null)
                .ownerUsername(owner != null ? owner.getUsername() : null)
                .ownerName(owner != null ? owner.getName() : null)
                .description(operationalScope.getDescription())
                .effectiveFrom(operationalScope.getEffectiveFrom())
                .effectiveTo(operationalScope.getEffectiveTo())
                .createdAt(operationalScope.getCreatedAt())
                .updatedAt(operationalScope.getUpdatedAt())
                .build();
    }
}