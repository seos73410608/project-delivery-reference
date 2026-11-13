package com.seos.cmdb.relationship.mapper;

import com.seos.cmdb.relationship.dto.response.CiRelationshipResponse;
import com.seos.cmdb.relationship.dto.response.CiRelationshipTypeResponse;
import com.seos.cmdb.relationship.entity.CiRelationship;
import com.seos.cmdb.relationship.entity.CiRelationshipType;
import org.springframework.stereotype.Component;

@Component
public class CiRelationshipMapper {

    /**
     * Relationship Entity -> Response
     */
    public CiRelationshipResponse toResponse(
            CiRelationship relationship
    ) {

        return CiRelationshipResponse.builder()
                .id(relationship.getId())
                .sourceCiId(relationship.getSourceCi().getId())
                .sourceCiCode(relationship.getSourceCi().getCiCode())
                .sourceCiName(relationship.getSourceCi().getCiName())
                .targetCiId(relationship.getTargetCi().getId())
                .targetCiCode(relationship.getTargetCi().getCiCode())
                .targetCiName(relationship.getTargetCi().getCiName())
                .relationshipTypeId(
                        relationship.getRelationshipType().getId()
                )
                .relationshipTypeCode(
                        relationship.getRelationshipType().getCode()
                )
                .relationshipTypeName(
                        relationship.getRelationshipType().getName()
                )
                .description(relationship.getDescription())
                .status(relationship.getStatus())
                .deactivatedAt(relationship.getDeactivatedAt())
                .deactivatedBy(
                        relationship.getDeactivatedBy() != null
                                ? relationship.getDeactivatedBy().getId()
                                : null
                )
                .deactivationReason(relationship.getDeactivationReason())
                .build();
    }

    /**
     * Relationship Type Entity -> Response
     */
    public CiRelationshipTypeResponse toTypeResponse(
            CiRelationshipType relationshipType
    ) {

        return CiRelationshipTypeResponse.builder()
                .id(relationshipType.getId())
                .code(relationshipType.getCode())
                .name(relationshipType.getName())
                .description(relationshipType.getDescription())
                .enabled(relationshipType.getEnabled())
                .sortOrder(relationshipType.getSortOrder())
                .build();
    }
}