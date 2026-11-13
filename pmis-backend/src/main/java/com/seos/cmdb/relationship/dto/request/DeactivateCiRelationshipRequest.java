package com.seos.cmdb.relationship.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class DeactivateCiRelationshipRequest {

    @NotBlank
    @Size(max = 500)
    private String reason;
}