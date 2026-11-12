package com.seos.cmdb.ci.mapper;

import com.seos.cmdb.ci.dto.request.CreateConfigurationItemRequest;
import com.seos.cmdb.ci.dto.response.ConfigurationItemResponse;
import com.seos.cmdb.ci.entity.ConfigurationItem;
import com.seos.cmdb.ci.entity.CiType;
import com.seos.pmis.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationItemMapper {

    /**
     * Configuration Item 생성
     */
    public ConfigurationItem toEntity(
            CreateConfigurationItemRequest request,
            CiType ciType,
            User manager,
            String ciCode
    ) {

        return ConfigurationItem.builder()
                .ciCode(ciCode)
                .ciName(request.getCiName())
                .ciType(ciType)
                .environment(request.getEnvironment())
                .manager(manager)
                .location(request.getLocation())
                .description(request.getDescription())
                .build();
    }

    /**
     * Entity → Response
     */
    public ConfigurationItemResponse toResponse(
            ConfigurationItem configurationItem
    ) {

        CiType ciType = configurationItem.getCiType();
        User manager = configurationItem.getManager();

        return ConfigurationItemResponse.builder()
                .id(configurationItem.getId())
                .ciCode(configurationItem.getCiCode())
                .ciName(configurationItem.getCiName())
                .ciTypeId(ciType.getId())
                .ciTypeCode(ciType.getCode())
                .ciTypeName(ciType.getName())
                .status(configurationItem.getStatus())
                .environment(configurationItem.getEnvironment())
                .managerId(
                        manager != null
                                ? manager.getId()
                                : null
                )
                .managerName(
                        manager != null
                                ? manager.getName()
                                : null
                )
                .location(configurationItem.getLocation())
                .description(configurationItem.getDescription())
                .createdAt(configurationItem.getCreatedAt())
                .updatedAt(configurationItem.getUpdatedAt())
                .build();
    }
}