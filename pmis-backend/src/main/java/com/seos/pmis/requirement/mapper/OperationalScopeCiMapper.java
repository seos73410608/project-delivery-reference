
package com.seos.pmis.requirement.mapper;

import com.seos.cmdb.ci.entity.CiType;
import com.seos.cmdb.ci.entity.ConfigurationItem;
import com.seos.pmis.requirement.dto.response.OperationalScopeCiResponse;
import com.seos.pmis.requirement.entity.OperationalScopeCi;
import org.springframework.stereotype.Component;

@Component
public class OperationalScopeCiMapper {

    /**
     * Entity → Response
     */
    public OperationalScopeCiResponse toResponse(
            OperationalScopeCi operationalScopeCi
    ) {
        ConfigurationItem configurationItem =
                operationalScopeCi.getConfigurationItem();

        CiType ciType = configurationItem.getCiType();

        return OperationalScopeCiResponse.builder()
                .id(operationalScopeCi.getId())
                .scopeId(
                        operationalScopeCi.getOperationalScope().getId()
                )
                .ciId(configurationItem.getId())
                .ciCode(configurationItem.getCiCode())
                .ciName(configurationItem.getCiName())
                .ciTypeId(ciType != null ? ciType.getId() : null)
                .ciTypeCode(ciType != null ? ciType.getCode() : null)
                .ciTypeName(ciType != null ? ciType.getName() : null)
                .status(configurationItem.getStatus())
                .environment(configurationItem.getEnvironment())
                .createdAt(operationalScopeCi.getCreatedAt())
                .updatedAt(operationalScopeCi.getUpdatedAt())
                .build();
    }
}
