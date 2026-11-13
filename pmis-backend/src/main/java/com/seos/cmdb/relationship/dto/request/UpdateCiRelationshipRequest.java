package com.seos.cmdb.relationship.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateCiRelationshipRequest {

    @Size(max = 1000)
    private String description;
}