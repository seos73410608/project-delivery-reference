package com.seos.cmdb.relationship.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CreateCiRelationshipRequest {

    @NotNull
    @Positive
    private Long targetCiId;

    @NotNull
    @Positive
    private Long relationshipTypeId;

    @Size(max = 1000)
    private String description;
}