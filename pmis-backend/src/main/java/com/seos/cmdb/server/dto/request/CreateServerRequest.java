package com.seos.cmdb.server.dto.request;

import com.seos.cmdb.ci.entity.CiEnvironment;
import com.seos.cmdb.server.entity.ServerCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CreateServerRequest {

    @NotBlank(message = "CI 이름은 필수입니다.")
    @Size(max = 200, message = "CI 이름은 200자 이하입니다.")
    private String ciName;

    @NotNull(message = "CI 유형은 필수입니다.")
    private Long ciTypeId;

    @NotBlank(message = "호스트명은 필수입니다.")
    @Size(max = 200, message = "호스트명은 200자 이하입니다.")
    private String hostname;

    @NotNull(message = "서버 구분은 필수입니다.")
    private ServerCategory serverCategory;

    private CiEnvironment environment;

    private Long managerId;

    @Size(max = 200, message = "위치는 200자 이하입니다.")
    private String location;

    @Size(max = 5000, message = "CI 설명은 5000자 이하입니다.")
    private String description;

    @Size(max = 100, message = "관리 IP는 100자 이하입니다.")
    private String managementIp;

    @Size(max = 100, message = "운영체제 이름은 100자 이하입니다.")
    private String osName;

    @Size(max = 100, message = "운영체제 버전은 100자 이하입니다.")
    private String osVersion;

    @Positive(message = "CPU 코어 수는 양수여야 합니다.")
    private Integer cpuCores;

    @Positive(message = "메모리 크기는 양수여야 합니다.")
    private Long memorySizeMb;

    @Size(max = 100, message = "가상화 플랫폼은 100자 이하입니다.")
    private String virtualizationPlatform;
}
