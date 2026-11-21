package com.seos.pmis.operation.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OperationParticipantTest {

    private OperationParticipant participant;

    @BeforeEach
    void setUp() {
        participant = OperationParticipant.builder()
                .id(1L)
                .participantRole(OperationParticipantRole.OPERATOR)
                .primary(true)
                .effectiveFrom(LocalDate.of(2026, 1, 1))
                .description("Operation participant")
                .build();
    }

    @Test
    void close_updatesEffectiveToSuccessfully() {
        // given
        LocalDate effectiveTo = LocalDate.of(2026, 12, 31);

        // when
        participant.close(effectiveTo);

        // then
        assertThat(participant.getEffectiveTo()).isEqualTo(effectiveTo);
        assertThat(participant.getEffectiveFrom())
                .isEqualTo(LocalDate.of(2026, 1, 1));
    }

    @Test
    void closeAllowsSameDateAsEffectiveFrom() {
        // given
        LocalDate effectiveTo = LocalDate.of(2026, 1, 1);

        // when
        participant.close(effectiveTo);

        // then
        assertThat(participant.getEffectiveTo()).isEqualTo(effectiveTo);
    }

    @Test
    void closeThrowsExceptionWhenEffectiveToIsNull() {
        // when & then
        assertThatThrownBy(() -> participant.close(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("참여 종료일은 필수입니다.");

        assertThat(participant.getEffectiveTo()).isNull();
    }

    @Test
    void closeThrowsExceptionWhenEffectiveFromIsNull() {
        // given
        OperationParticipant participantWithoutStartDate =
                OperationParticipant.builder()
                        .id(2L)
                        .participantRole(OperationParticipantRole.SERVICE_PROVIDER)
                        .build();

        // when & then
        assertThatThrownBy(
                () -> participantWithoutStartDate.close(
                        LocalDate.of(2026, 12, 31)
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("참여 종료일은 시작일이 존재해야 하며 시작일보다 빠를 수 없습니다.");

        assertThat(participantWithoutStartDate.getEffectiveTo()).isNull();
    }

    @Test
    void closeThrowsExceptionWhenEffectiveToIsBeforeEffectiveFrom() {
        // when & then
        assertThatThrownBy(
                () -> participant.close(LocalDate.of(2025, 12, 31))
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("참여 종료일은 시작일이 존재해야 하며 시작일보다 빠를 수 없습니다.");

        assertThat(participant.getEffectiveTo()).isNull();
    }

    @Test
    void closeThrowsExceptionWhenAlreadyClosed() {
        // given
        participant.close(LocalDate.of(2026, 6, 30));

        // when & then
        assertThatThrownBy(
                () -> participant.close(LocalDate.of(2026, 12, 31))
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 종료된 참여 정보입니다.");

        assertThat(participant.getEffectiveTo())
                .isEqualTo(LocalDate.of(2026, 6, 30));
    }

    @Test
    void changePrimary_updatesPrimarySuccessfully() {
        // when
        participant.changePrimary(false);

        // then
        assertThat(participant.isPrimary()).isFalse();
    }
}