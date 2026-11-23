package com.seos.pmis.operation.mapper;

import com.seos.pmis.operation.dto.response.OperationResponse;
import com.seos.pmis.operation.entity.Operation;
import com.seos.pmis.operation.entity.OperationStatus;
import com.seos.pmis.operation.entity.OperationType;
import com.seos.pmis.user.entity.User;
import com.seos.pmis.user.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OperationMapper 테스트")
class OperationMapperTest {

    private OperationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new OperationMapper();
    }

    @Test
    @DisplayName("운영 서비스 Entity를 Response DTO로 변환한다")
    void toResponse_withManager() {
        User manager = User.builder()
                .id(10L)
                .username("manager")
                .name("운영 담당자")
                .email("manager@example.com")
                .password("encoded-password")
                .role(UserRole.ROLE_USER)
                .enabled(true)
                .build();

        Operation operation = Operation.builder()
                .id(1L)
                .operationCode("OP-001")
                .operationName("통합 운영 서비스")
                .operationType(OperationType.INFRASTRUCTURE_SERVICE)
                .status(OperationStatus.ACTIVE)
                .manager(manager)
                .description("운영 서비스 설명")
                .effectiveFrom(LocalDate.of(2026, 1, 1))
                .effectiveTo(LocalDate.of(2026, 12, 31))
                .build();

        OperationResponse response = mapper.toResponse(operation);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getOperationCode()).isEqualTo("OP-001");
        assertThat(response.getOperationName()).isEqualTo("통합 운영 서비스");
        assertThat(response.getOperationType())
                .isEqualTo(OperationType.INFRASTRUCTURE_SERVICE);
        assertThat(response.getStatus()).isEqualTo(OperationStatus.ACTIVE);
        assertThat(response.getManagerUserId()).isEqualTo(10L);
        assertThat(response.getManagerName()).isEqualTo("운영 담당자");
        assertThat(response.getDescription()).isEqualTo("운영 서비스 설명");
        assertThat(response.getEffectiveFrom())
                .isEqualTo(LocalDate.of(2026, 1, 1));
        assertThat(response.getEffectiveTo())
                .isEqualTo(LocalDate.of(2026, 12, 31));
    }

    @Test
    @DisplayName("담당자가 없는 운영 서비스도 Response DTO로 변환한다")
    void toResponse_withoutManager() {
        Operation operation = Operation.builder()
                .id(2L)
                .operationCode("OP-002")
                .operationName("담당자 없는 운영 서비스")
                .operationType(OperationType.OTHER)
                .status(OperationStatus.PLANNED)
                .build();

        OperationResponse response = mapper.toResponse(operation);

        assertThat(response.getId()).isEqualTo(2L);
        assertThat(response.getManagerUserId()).isNull();
        assertThat(response.getManagerName()).isNull();
    }
}