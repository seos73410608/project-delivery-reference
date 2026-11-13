package com.seos.cmdb.server.dto.response;

import com.seos.cmdb.ci.dto.response.ConfigurationItemResponse;
import com.seos.cmdb.ci.entity.CiEnvironment;
import com.seos.cmdb.ci.entity.CiStatus;
import com.seos.cmdb.server.entity.ServerCategory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ServerRegistrationResponse {

    private Long id;

    private String ciCode;

    private String ciName;

    private Long ciTypeId;

    private String ciTypeCode;

    private String ciTypeName;

    private CiStatus status;

    private CiEnvironment environment;

    private Long managerId;

    private String managerName;

    private String location;

    private String description;

    private ServerDetailResponse serverDetail;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Getter
    @Builder
    public static class ServerDetailResponse {

        private String hostname;

        private String managementIp;

        private ServerCategory serverCategory;

        private String osName;

        private String osVersion;

        private Integer cpuCores;

        private Long memorySizeMb;

        private String virtualizationPlatform;
    }

    public static ServerRegistrationResponse from(
            ConfigurationItemResponse ci,
            ServerDetailResponse serverDetail
    ) {
        return ServerRegistrationResponse.builder()
                .id(ci.getId())
                .ciCode(ci.getCiCode())
                .ciName(ci.getCiName())
                .ciTypeId(ci.getCiTypeId())
                .ciTypeCode(ci.getCiTypeCode())
                .ciTypeName(ci.getCiTypeName())
                .status(ci.getStatus())
                .environment(ci.getEnvironment())
                .managerId(ci.getManagerId())
                .managerName(ci.getManagerName())
                .location(ci.getLocation())
                .description(ci.getDescription())
                .serverDetail(serverDetail)
                .createdAt(ci.getCreatedAt())
                .updatedAt(ci.getUpdatedAt())
                .build();
    }
}