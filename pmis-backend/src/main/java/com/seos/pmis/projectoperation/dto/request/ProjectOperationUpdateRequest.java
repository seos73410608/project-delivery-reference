
package com.seos.pmis.projectoperation.dto.request;

import com.seos.pmis.projectoperation.entity.ProjectOperationRelationshipType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ProjectOperationUpdateRequest {

    @NotNull(message = "관계 유형은 필수입니다.")
    private ProjectOperationRelationshipType relationshipType;

    private LocalDate effectiveFrom;

    private LocalDate effectiveTo;

    private String description;
}
