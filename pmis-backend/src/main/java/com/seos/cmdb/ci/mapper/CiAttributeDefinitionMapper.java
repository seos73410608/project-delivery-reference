
package com.seos.cmdb.ci.mapper;

import com.seos.cmdb.ci.dto.response.CiAttributeDefinitionResponse;
import com.seos.cmdb.ci.entity.CiAttributeDefinition;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CiAttributeDefinitionMapper {

    /**
     * Entity → Response
     */
    public CiAttributeDefinitionResponse toResponse(
            CiAttributeDefinition definition
    ) {

        return toResponse(definition, List.of());
    }

    /**
     * Entity → Response (하위 노드 포함)
     */
    public CiAttributeDefinitionResponse toResponse(
            CiAttributeDefinition definition,
            List<CiAttributeDefinitionResponse> children
    ) {

        return CiAttributeDefinitionResponse.builder()
                .id(definition.getId())
                .parentId(
                        definition.getParent() != null
                                ? definition.getParent().getId()
                                : null
                )
                .ciTypeId(
                        definition.getCiType() != null
                                ? definition.getCiType().getId()
                                : null
                )
                .attributeKey(definition.getAttributeKey())
                .displayName(definition.getDisplayName())
                .nodeType(definition.getNodeType())
                .dataType(definition.getDataType())
                .referenceType(definition.getReferenceType())
                .displayFormat(definition.getDisplayFormat())
                .displayOrder(definition.getDisplayOrder())
                .enabled(definition.getEnabled())
                .description(definition.getDescription())
                .children(children)
                .build();
    }
}
