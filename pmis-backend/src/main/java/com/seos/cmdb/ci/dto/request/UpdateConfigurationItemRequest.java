package com.seos.cmdb.ci.dto.request;

import com.seos.cmdb.ci.entity.CiEnvironment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateConfigurationItemRequest {

    @NotBlank(message = "CI 이름은 필수입니다.")
    @Size(max = 200, message = "CI 이름은 200자 이하입니다.")
    private String ciName;

    @NotNull(message = "CI 유형은 필수입니다.")
    private Long ciTypeId;

    private CiEnvironment environment;

    private Long managerId;

    @Size(max = 200, message = "위치는 200자 이하입니다.")
    private String location;

    @Size(max = 5000, message = "CI 설명은 5000자 이하입니다.")
    private String description;
}