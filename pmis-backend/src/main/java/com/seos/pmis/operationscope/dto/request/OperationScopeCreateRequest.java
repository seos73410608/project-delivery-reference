
package com.seos.pmis.operationscope.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class OperationScopeCreateRequest {

    /**
     * 운영 서비스 ID
     */
    @NotNull(message = "운영 서비스 ID는 필수입니다.")
    private Long operationId;

    /**
     * 운영 범위 ID
     */
    @NotNull(message = "운영 범위 ID는 필수입니다.")
    private Long operationalScopeId;

    /**
     * 관계 적용 시작일
     */
    private LocalDate effectiveFrom;

    /**
     * 관계 적용 종료일
     */
    private LocalDate effectiveTo;

    /**
     * 관계 설명
     */
    private String description;
}
