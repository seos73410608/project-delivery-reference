package com.seos.pmis.operationscope.dto.response;

import com.seos.pmis.operation.entity.OperationStatus;
import com.seos.pmis.operation.entity.OperationType;
import com.seos.pmis.operationscope.entity.OperationScopeStatus;
import com.seos.pmis.requirement.entity.OperationalScopeStatus;
import com.seos.pmis.requirement.entity.OperationalScopeType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class OperationScopeResponse {

    /**
     * 운영 서비스-운영 범위 관계 ID
     */
    private Long id;

    /**
     * 관계 상태
     */
    private OperationScopeStatus status;

    /**
     * 운영 서비스 정보
     */
    private Long operationId;
    private String operationCode;
    private String operationName;
    private OperationType operationType;
    private OperationStatus operationStatus;

    /**
     * 운영 범위 정보
     */
    private Long operationalScopeId;
    private String scopeCode;
    private String scopeName;
    private OperationalScopeType scopeType;
    private OperationalScopeStatus operationalScopeStatus;

    /**
     * 관계 적용 기간
     */
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;

    /**
     * 관계 설명
     */
    private String description;

    /**
     * 생성 및 수정 일시
     */
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}