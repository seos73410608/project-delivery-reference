package com.seos.cmdb.ci.mapper;

import com.seos.cmdb.ci.dto.request.CreateConfigurationItemRequest;
import com.seos.cmdb.ci.dto.response.ConfigurationItemResponse;
import com.seos.cmdb.ci.entity.ConfigurationItem;
import com.seos.cmdb.ci.entity.CiEnvironment;
import com.seos.cmdb.ci.entity.CiType;
import com.seos.pmis.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationItemMapper {

    /**
     * 기존 Configuration Item 생성
     */
    public ConfigurationItem toEntity(
            CreateConfigurationItemRequest request,
            CiType ciType,
            User manager,
            String ciCode
    ) {
        return toEntity(
                request.getCiName(),
                ciType,
                request.getEnvironment(),
                manager,
                request.getLocation(),
                request.getDescription(),
                ciCode
        );
    }

    /**
     * 공통 Configuration Item 생성
     */
    public ConfigurationItem toEntity(
            String ciName,
            CiType ciType,
            CiEnvironment environment,
            User manager,
            String location,
            String description,
            String ciCode
    ) {
        return ConfigurationItem.builder()
                .ciCode(ciCode)
                .ciName(ciName)
                .ciType(ciType)
                .environment(environment)
                .manager(manager)
                .location(location)
                .description(description)
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
