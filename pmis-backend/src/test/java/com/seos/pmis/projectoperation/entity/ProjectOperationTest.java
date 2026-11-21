
package com.seos.pmis.projectoperation.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProjectOperationTest {

    private ProjectOperation projectOperation;

    @BeforeEach
    void setUp() {
        projectOperation = ProjectOperation.builder()
                .id(1L)
                .relationshipType(ProjectOperationRelationshipType.BUILD_RESULT)
                .status(ProjectOperationStatus.PLANNED)
                .effectiveFrom(LocalDate.of(2026, 1, 1))
                .effectiveTo(LocalDate.of(2026, 12, 31))
                .description("Original relationship")
                .build();
    }

    @Test
    void update_updatesRelationshipSuccessfully() {
        // given
        LocalDate effectiveFrom = LocalDate.of(2027, 1, 1);
        LocalDate effectiveTo = LocalDate.of(2027, 12, 31);

        // when
        projectOperation.update(
                ProjectOperationRelationshipType.ENHANCEMENT,
                effectiveFrom,
                effectiveTo,
                "Updated relationship"
        );

        // then
        assertThat(projectOperation.getRelationshipType())
                .isEqualTo(ProjectOperationRelationshipType.ENHANCEMENT);
        assertThat(projectOperation.getEffectiveFrom())
                .isEqualTo(effectiveFrom);
        assertThat(projectOperation.getEffectiveTo())
                .isEqualTo(effectiveTo);
        assertThat(projectOperation.getDescription())
                .isEqualTo("Updated relationship");
    }

    @Test
    void updateAllowsOpenEndedEffectivePeriod() {
        // when
        projectOperation.update(
                ProjectOperationRelationshipType.BUILD_RESULT,
                LocalDate.of(2027, 1, 1),
                null,
                "Ongoing relationship"
        );

        // then
        assertThat(projectOperation.getEffectiveFrom())
                .isEqualTo(LocalDate.of(2027, 1, 1));
        assertThat(projectOperation.getEffectiveTo()).isNull();
    }

    @Test
    void updateThrowsExceptionWhenEffectiveToExistsWithoutEffectiveFrom() {
        // when & then
        assertThatThrownBy(() -> projectOperation.update(
                ProjectOperationRelationshipType.BUILD_RESULT,
                null,
                LocalDate.of(2027, 12, 31),
                "Invalid relationship"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("관계 종료일은 시작일과 같거나 이후여야 합니다.");

        assertThat(projectOperation.getRelationshipType())
                .isEqualTo(ProjectOperationRelationshipType.BUILD_RESULT);
        assertThat(projectOperation.getEffectiveFrom())
                .isEqualTo(LocalDate.of(2026, 1, 1));
    }

    @Test
    void updateThrowsExceptionWhenEffectiveToIsBeforeEffectiveFrom() {
        // when & then
        assertThatThrownBy(() -> projectOperation.update(
                ProjectOperationRelationshipType.MIGRATION,
                LocalDate.of(2027, 12, 31),
                LocalDate.of(2027, 1, 1),
                "Invalid relationship"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("관계 종료일은 시작일과 같거나 이후여야 합니다.");

        assertThat(projectOperation.getEffectiveFrom())
                .isEqualTo(LocalDate.of(2026, 1, 1));
        assertThat(projectOperation.getEffectiveTo())
                .isEqualTo(LocalDate.of(2026, 12, 31));
    }

    @Test
    void changeStatus_updatesStatusSuccessfully() {
        // when
        projectOperation.changeStatus(ProjectOperationStatus.ACTIVE);

        // then
        assertThat(projectOperation.getStatus())
                .isEqualTo(ProjectOperationStatus.ACTIVE);
    }

    @Test
    void updateThrowsExceptionWhenRelationshipTypeIsNull() {
        // when & then
        assertThatThrownBy(() -> projectOperation.update(
                null,
                LocalDate.of(2027, 1, 1),
                LocalDate.of(2027, 12, 31),
                "Invalid relationship"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("프로젝트 운영 관계 유형은 필수입니다.");

        // 검증 실패 시 기존 데이터 유지
        assertThat(projectOperation.getRelationshipType())
                .isEqualTo(ProjectOperationRelationshipType.BUILD_RESULT);
        assertThat(projectOperation.getEffectiveFrom())
                .isEqualTo(LocalDate.of(2026, 1, 1));
        assertThat(projectOperation.getEffectiveTo())
                .isEqualTo(LocalDate.of(2026, 12, 31));
        assertThat(projectOperation.getDescription())
                .isEqualTo("Original relationship");
    }

    @Test
    void changeStatusThrowsExceptionWhenStatusIsNull() {
        // when & then
        assertThatThrownBy(() -> projectOperation.changeStatus(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("프로젝트 운영 관계 상태는 필수입니다.");

        assertThat(projectOperation.getStatus())
                .isEqualTo(ProjectOperationStatus.PLANNED);
    }
}
