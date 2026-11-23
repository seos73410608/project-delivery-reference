
package com.seos.pmis.operationscope.dto.request;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class OperationScopeUpdateRequest {

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
