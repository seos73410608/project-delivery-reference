package com.seos.cmdb.ci.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CiTypeResponse {

    private Long id;

    private String code;

    private String name;

    private Long parentId;

    private String description;

    private Boolean assignable;

    private Boolean enabled;

    private Integer sortOrder;
}