package com.seos.pmis.change.dto.request;

import com.seos.pmis.change.entity.ChangeStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChangeStatusUpdateRequest {

    /**
     * 변경 상태
     */
    @NotNull(message = "변경 상태는 필수입니다.")
    private ChangeStatus status;
}