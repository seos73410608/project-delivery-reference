package com.seos.pmis.operationscope.mapper;

import com.seos.pmis.operation.entity.Operation;
import com.seos.pmis.operation.entity.OperationStatus;
import com.seos.pmis.operation.entity.OperationType;
import com.seos.pmis.operationscope.dto.response.OperationScopeResponse;
import com.seos.pmis.operationscope.entity.OperationScope;
import com.seos.pmis.operationscope.entity.OperationScopeStatus;
import com.seos.pmis.requirement.entity.OperationalScope;
import com.seos.pmis.requirement.entity.OperationalScopeStatus;
import com.seos.pmis.requirement.entity.OperationalScopeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class OperationScopeMapperTest {

    private OperationScopeMapper mapper;
    private OperationScope operationScope;

    @BeforeEach
    void setUp() {
        mapper = new OperationScopeMapper();

        Operation operation = Operation.builder()
                .id(100L)
                .operationCode("OP-001")
                .operationName("통합 운영 서비스")
                .operationType(OperationType.INFRASTRUCTURE_SERVICE)
                .status(OperationStatus.ACTIVE)
                .build();

        OperationalScope operationalScope = OperationalScope.builder()
                .id(200L)
                .scopeCode("SCP-001")
                .scopeName("운영 인프라")
                .scopeType(OperationalScopeType.SERVER_GROUP)
                .status(OperationalScopeStatus.ACTIVE)
                .build();

        operationScope = OperationScope.builder()
                .id(1L)
                .operation(operation)
                .operationalScope(operationalScope)
                .status(OperationScopeStatus.ACTIVE)
                .effectiveFrom(LocalDate.of(2026, 1, 1))
                .effectiveTo(LocalDate.of(2026, 12, 31))
                .description("초기 운영 범위 관계")
                .build();
    }

    @Test
    @DisplayName("운영 범위 관계 Entity를 Response DTO로 변환한다")
    void toResponse_success() {
        OperationScopeResponse response = mapper.toResponse(operationScope);

        // 관계 정보
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getStatus())
                .isEqualTo(OperationScopeStatus.ACTIVE);
        assertThat(response.getEffectiveFrom())
                .isEqualTo(LocalDate.of(2026, 1, 1));
        assertThat(response.getEffectiveTo())
                .isEqualTo(LocalDate.of(2026, 12, 31));
        assertThat(response.getDescription())
                .isEqualTo("초기 운영 범위 관계");

        // 운영 서비스 정보
        assertThat(response.getOperationId()).isEqualTo(100L);
        assertThat(response.getOperationCode()).isEqualTo("OP-001");
        assertThat(response.getOperationName())
                .isEqualTo("통합 운영 서비스");
        assertThat(response.getOperationType())
                .isEqualTo(OperationType.INFRASTRUCTURE_SERVICE);
        assertThat(response.getOperationStatus())
                .isEqualTo(OperationStatus.ACTIVE);

        // 운영 범위 정보
        assertThat(response.getOperationalScopeId()).isEqualTo(200L);
        assertThat(response.getScopeCode()).isEqualTo("SCP-001");
        assertThat(response.getScopeName()).isEqualTo("운영 인프라");
        assertThat(response.getScopeType())
                .isEqualTo(OperationalScopeType.SERVER_GROUP);
        assertThat(response.getOperationalScopeStatus())
                .isEqualTo(OperationalScopeStatus.ACTIVE);
    }
}