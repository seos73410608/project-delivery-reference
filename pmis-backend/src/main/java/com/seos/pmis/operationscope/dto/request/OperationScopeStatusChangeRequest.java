
package com.seos.pmis.operationscope.dto.request;

import com.seos.pmis.operationscope.entity.OperationScopeStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class OperationScopeStatusChangeRequest {

    /**
     * 변경할 관계 상태
     */
    @NotNull(message = "변경할 운영 범위 관계 상태는 필수입니다.")
    private OperationScopeStatus status;
}
