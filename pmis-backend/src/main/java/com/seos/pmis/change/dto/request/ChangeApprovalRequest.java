package com.seos.pmis.change.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChangeApprovalRequest {

    /**
     * 승인 여부
     *
     * true  : 승인
     * false : 반려
     */
    @NotNull(message = "승인 여부는 필수입니다.")
    private Boolean approved;

    /**
     * 승인/반려 의견
     */
    private String approvalComment;
}