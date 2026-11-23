
package com.seos.pmis.projectoperation.dto.request;

import com.seos.pmis.projectoperation.entity.ProjectOperationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ProjectOperationStatusChangeRequest {

    @NotNull(message = "프로젝트 운영 관계 상태는 필수입니다.")
    private ProjectOperationStatus status;
}
