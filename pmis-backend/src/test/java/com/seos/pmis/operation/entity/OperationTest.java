package com.seos.pmis.operation.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OperationTest {

    private Operation operation;

    @BeforeEach
    void setUp() {
        operation = Operation.builder()
                .id(1L)
                .operationCode("OPR-000001")
                .operationName("Original Operation")
                .operationType(OperationType.APPLICATION_SERVICE)
                .status(OperationStatus.PLANNED)
                .description("Original description")
                .effectiveFrom(LocalDate.of(2026, 1, 1))
                .effectiveTo(LocalDate.of(2026, 12, 31))
                .build();
    }

    @Test
    void update_updatesOperationSuccessfully() {
        // given
        LocalDate effectiveFrom = LocalDate.of(2027, 1, 1);
        LocalDate effectiveTo = LocalDate.of(2027, 12, 31);

        // when
        operation.update(
                "Updated Operation",
                OperationType.INFRASTRUCTURE_SERVICE,
                null,
                "Updated description",
                effectiveFrom,
                effectiveTo
        );

        // then
        assertThat(operation.getOperationName())
                .isEqualTo("Updated Operation");
        assertThat(operation.getOperationType())
                .isEqualTo(OperationType.INFRASTRUCTURE_SERVICE);
        assertThat(operation.getManager()).isNull();
        assertThat(operation.getDescription())
                .isEqualTo("Updated description");
        assertThat(operation.getEffectiveFrom()).isEqualTo(effectiveFrom);
        assertThat(operation.getEffectiveTo()).isEqualTo(effectiveTo);
        assertThat(operation.getOperationCode()).isEqualTo("OPR-000001");
    }

    @Test
    void updateAllowsOpenEndedEffectivePeriod() {
        // when
        operation.update(
                "Ongoing Operation",
                OperationType.BUSINESS_SERVICE,
                null,
                "Ongoing service",
                LocalDate.of(2027, 1, 1),
                null
        );

        // then
        assertThat(operation.getEffectiveFrom())
                .isEqualTo(LocalDate.of(2027, 1, 1));
        assertThat(operation.getEffectiveTo()).isNull();
    }

    @Test
    void updateThrowsExceptionWhenEffectiveToExistsWithoutEffectiveFrom() {
        // when & then
        assertThatThrownBy(() -> operation.update(
                "Invalid Operation",
                OperationType.OTHER,
                null,
                null,
                null,
                LocalDate.of(2027, 12, 31)
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("운영 종료일은 시작일이 존재해야 하며 시작일보다 빠를 수 없습니다.");

        assertThat(operation.getOperationName())
                .isEqualTo("Original Operation");
    }

    @Test
    void updateThrowsExceptionWhenEffectiveToIsBeforeEffectiveFrom() {
        // when & then
        assertThatThrownBy(() -> operation.update(
                "Invalid Operation",
                OperationType.OTHER,
                null,
                null,
                LocalDate.of(2027, 12, 31),
                LocalDate.of(2027, 1, 1)
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("운영 종료일은 시작일이 존재해야 하며 시작일보다 빠를 수 없습니다.");

        assertThat(operation.getOperationName())
                .isEqualTo("Original Operation");
        assertThat(operation.getEffectiveFrom())
                .isEqualTo(LocalDate.of(2026, 1, 1));
    }

    @Test
    void changeStatus_updatesStatusSuccessfully() {
        // when
        operation.changeStatus(OperationStatus.ACTIVE);

        // then
        assertThat(operation.getStatus()).isEqualTo(OperationStatus.ACTIVE);
    }

    @Test
    void changeStatusThrowsExceptionWhenStatusIsNull() {
        // when & then
        assertThatThrownBy(() -> operation.changeStatus(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("운영 상태는 필수입니다.");

        assertThat(operation.getStatus()).isEqualTo(OperationStatus.PLANNED);
    }
}