package com.seos.pmis.requirement.dto.response;

import com.seos.pmis.requirement.entity.OperationalScopeStatus;
import com.seos.pmis.requirement.entity.OperationalScopeType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class OperationalScopeResponse {

    /**
     * Operational Scope PK
     */
    private Long id;

    /**
     * Operational Scope 코드
     */
    private String scopeCode;

    /**
     * Operational Scope 이름
     */
    private String scopeName;

    /**
     * Operational Scope 유형
     */
    private OperationalScopeType scopeType;

    /**
     * Operational Scope 상태
     */
    private OperationalScopeStatus status;

    /**
     * 상위 Operational Scope ID
     */
    private Long parentScopeId;

    /**
     * 상위 Operational Scope 코드
     */
    private String parentScopeCode;

    /**
     * 상위 Operational Scope 이름
     */
    private String parentScopeName;

    /**
     * 담당자 사용자 ID
     */
    private Long ownerId;

    /**
     * 담당자 사용자명
     */
    private String ownerUsername;

    /**
     * 담당자 이름
     */
    private String ownerName;

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

    /**
     * 생성일시
     */
    private LocalDateTime createdAt;

    /**
     * 수정일시
     */
    private LocalDateTime updatedAt;

    /**
     * 낙관적 잠금 버전
     */
    private Long version;
}