
package com.seos.pmis.operationscope.entity;

import com.seos.pmis.operation.entity.Operation;
import com.seos.pmis.requirement.entity.OperationalScope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OperationScopeTest {

    private OperationScope operationScope;

    @BeforeEach
    void setUp() {
        Operation operation = Operation.builder()
                .operationCode("OP-001")
                .operationName("통합 운영 서비스")
                .build();

        OperationalScope operationalScope = OperationalScope.builder()
                .scopeCode("SCP-001")
                .scopeName("운영 인프라")
                .build();

        operationScope = OperationScope.builder()
                .operation(operation)
                .operationalScope(operationalScope)
                .status(OperationScopeStatus.PLANNED)
                .effectiveFrom(LocalDate.of(2026, 1, 1))
                .effectiveTo(LocalDate.of(2026, 12, 31))
                .description("초기 운영 범위 관계")
                .build();
    }

    @Test
    @DisplayName("운영 범위 관계 정보를 수정한다")
    void update_success() {
        LocalDate effectiveFrom = LocalDate.of(2026, 2, 1);
        LocalDate effectiveTo = LocalDate.of(2027, 1, 31);

        operationScope.update(
                effectiveFrom,
                effectiveTo,
                "수정된 운영 범위 관계");

        assertThat(operationScope.getEffectiveFrom())
                .isEqualTo(effectiveFrom);
        assertThat(operationScope.getEffectiveTo())
                .isEqualTo(effectiveTo);
        assertThat(operationScope.getDescription())
                .isEqualTo("수정된 운영 범위 관계");
    }

    @Test
    @DisplayName("종료일이 없으면 기간을 수정할 수 있다")
    void update_openEndedPeriod_success() {
        LocalDate effectiveFrom = LocalDate.of(2026, 2, 1);

        operationScope.update(
                effectiveFrom,
                null,
                "종료일 미정");

        assertThat(operationScope.getEffectiveFrom())
                .isEqualTo(effectiveFrom);
        assertThat(operationScope.getEffectiveTo())
                .isNull();
    }

    @Test
    @DisplayName("시작일 없이 종료일을 지정하면 예외가 발생한다")
    void update_withoutStartDate_throwsException() {
        assertThatThrownBy(() -> operationScope.update(
                null,
                LocalDate.of(2026, 12, 31),
                "잘못된 기간"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("운영 범위 관계 종료일은 시작일이 존재해야 하며 시작일보다 빠를 수 없습니다.");

        assertThat(operationScope.getEffectiveFrom())
                .isEqualTo(LocalDate.of(2026, 1, 1));
        assertThat(operationScope.getEffectiveTo())
                .isEqualTo(LocalDate.of(2026, 12, 31));
        assertThat(operationScope.getDescription())
                .isEqualTo("초기 운영 범위 관계");
    }

    @Test
    @DisplayName("시작일보다 빠른 종료일을 지정하면 예외가 발생한다")
    void update_invalidPeriod_throwsException() {
        assertThatThrownBy(() -> operationScope.update(
                LocalDate.of(2026, 12, 31),
                LocalDate.of(2026, 1, 1),
                "잘못된 기간"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("운영 범위 관계 종료일은 시작일이 존재해야 하며 시작일보다 빠를 수 없습니다.");

        assertThat(operationScope.getDescription())
                .isEqualTo("초기 운영 범위 관계");
    }

    @Test
    @DisplayName("PLANNED 상태에서 ACTIVE로 변경한다")
    void changeStatus_plannedToActive_success() {
        operationScope.changeStatus(OperationScopeStatus.ACTIVE);

        assertThat(operationScope.getStatus())
                .isEqualTo(OperationScopeStatus.ACTIVE);
    }

    @Test
    @DisplayName("PLANNED 상태에서 CANCELLED로 변경한다")
    void changeStatus_plannedToCancelled_success() {
        operationScope.changeStatus(OperationScopeStatus.CANCELLED);

        assertThat(operationScope.getStatus())
                .isEqualTo(OperationScopeStatus.CANCELLED);
    }

    @Test
    @DisplayName("ACTIVE 상태에서 COMPLETED로 변경한다")
    void changeStatus_activeToCompleted_success() {
        operationScope.changeStatus(OperationScopeStatus.ACTIVE);
        operationScope.changeStatus(OperationScopeStatus.COMPLETED);

        assertThat(operationScope.getStatus())
                .isEqualTo(OperationScopeStatus.COMPLETED);
    }

    @Test
    @DisplayName("ACTIVE 상태에서 CANCELLED로 변경한다")
    void changeStatus_activeToCancelled_success() {
        operationScope.changeStatus(OperationScopeStatus.ACTIVE);
        operationScope.changeStatus(OperationScopeStatus.CANCELLED);

        assertThat(operationScope.getStatus())
                .isEqualTo(OperationScopeStatus.CANCELLED);
    }

    @Test
    @DisplayName("상태가 null이면 예외가 발생한다")
    void changeStatus_null_throwsException() {
        assertThatThrownBy(() -> operationScope.changeStatus(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("운영 범위 관계 상태는 필수입니다.");

        assertThat(operationScope.getStatus())
                .isEqualTo(OperationScopeStatus.PLANNED);
    }

    @Test
    @DisplayName("PLANNED에서 COMPLETED로 바로 변경할 수 없다")
    void changeStatus_plannedToCompleted_throwsException() {
        assertThatThrownBy(() -> operationScope.changeStatus(OperationScopeStatus.COMPLETED))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("현재 운영 범위 관계 상태에서는 해당 상태로 변경할 수 없습니다.");

        assertThat(operationScope.getStatus())
                .isEqualTo(OperationScopeStatus.PLANNED);
    }

    @Test
    @DisplayName("COMPLETED 상태는 다른 상태로 변경할 수 없다")
    void changeStatus_completedIsTerminal_throwsException() {
        operationScope.changeStatus(OperationScopeStatus.ACTIVE);
        operationScope.changeStatus(OperationScopeStatus.COMPLETED);

        assertThatThrownBy(() -> operationScope.changeStatus(OperationScopeStatus.ACTIVE))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(operationScope.getStatus())
                .isEqualTo(OperationScopeStatus.COMPLETED);
    }

    @Test
    @DisplayName("CANCELLED 상태는 다른 상태로 변경할 수 없다")
    void changeStatus_cancelledIsTerminal_throwsException() {
        operationScope.changeStatus(OperationScopeStatus.CANCELLED);

        assertThatThrownBy(() -> operationScope.changeStatus(OperationScopeStatus.ACTIVE))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(operationScope.getStatus())
                .isEqualTo(OperationScopeStatus.CANCELLED);
    }
}
