package com.seos.cmdb.ci.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CreateCiTypeRequest {

    @NotBlank
    @Size(max = 50)
    private String code;

    @NotBlank
    @Size(max = 100)
    private String name;

    private Long parentId;

    private String description;

    @NotNull
    private Boolean assignable;

    private Integer sortOrder;
}