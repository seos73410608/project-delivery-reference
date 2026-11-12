package com.seos.cmdb.ci.dto.response;

import com.seos.cmdb.ci.entity.CiEnvironment;
import com.seos.cmdb.ci.entity.CiStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ConfigurationItemResponse {

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

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}