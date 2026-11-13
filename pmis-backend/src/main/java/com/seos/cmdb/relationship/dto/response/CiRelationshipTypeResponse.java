package com.seos.cmdb.relationship.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CiRelationshipTypeResponse {

    private Long id;

    private String code;

    private String name;

    private String description;

    private Boolean enabled;

    private Integer sortOrder;
}