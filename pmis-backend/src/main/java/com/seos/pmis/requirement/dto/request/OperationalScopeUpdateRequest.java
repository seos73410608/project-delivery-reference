package com.seos.pmis.requirement.dto.request;

import com.seos.pmis.requirement.entity.OperationalScopeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class OperationalScopeUpdateRequest {

    /**
     * 수정 대상의 현재 버전
     */
    @NotNull(message = "버전 정보는 필수입니다.")
    private Long version;

    /**
     * Operational Scope 이름
     */
    @NotBlank(message = "Operational Scope 이름은 필수입니다.")
    @Size(max = 200, message = "Operational Scope 이름은 200자 이하입니다.")
    private String scopeName;

    /**
     * Operational Scope 유형
     */
    @NotNull(message = "Operational Scope 유형은 필수입니다.")
    private OperationalScopeType scopeType;

    /**
     * 상위 Operational Scope ID
     */
    private Long parentScopeId;

    /**
     * 담당자 사용자 ID
     */
    private Long ownerId;

    /**
     * 설명
     */
    private String description;

    /**
     * 적용 시작일
     */
    private LocalDate effectiveFrom;

    /**
     * 적용 종료일
     */
    private LocalDate effectiveTo;
}