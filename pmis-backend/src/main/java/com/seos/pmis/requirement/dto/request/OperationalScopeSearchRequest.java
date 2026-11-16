package com.seos.pmis.requirement.dto.request;

import com.seos.pmis.requirement.entity.OperationalScopeStatus;
import com.seos.pmis.requirement.entity.OperationalScopeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Operational Scope 검색 조건")
public class OperationalScopeSearchRequest {

    @Schema(description = "운영 범위 코드", example = "OPS-000001")
    private String scopeCode;

    @Schema(description = "운영 범위명", example = "금융결제 서비스")
    private String scopeName;

    @Schema(description = "운영 범위 유형")
    private OperationalScopeType scopeType;

    @Schema(description = "운영 범위 상태")
    private OperationalScopeStatus status;

    @Schema(description = "상위 Operational Scope ID", example = "1")
    private Long parentScopeId;

    @Schema(description = "담당자 사용자 ID", example = "10")
    private Long ownerId;

    @Schema(description = "담당자 로그인 ID 또는 이름", example = "manager")
    private String ownerKeyword;
}