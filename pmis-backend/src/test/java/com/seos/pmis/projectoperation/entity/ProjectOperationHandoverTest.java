
package com.seos.pmis.projectoperation.entity;

import com.seos.pmis.user.entity.User;
import com.seos.pmis.user.entity.UserRole;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProjectOperationHandoverTest {

    private User createUser(String username) {
        return User.builder()
                .username(username)
                .password("encoded-password")
                .name(username)
                .email(username + "@test.com")
                .role(UserRole.ROLE_USER)
                .enabled(true)
                .build();
    }

    private ProjectOperationHandover createHandover() {
        return ProjectOperationHandover.builder()
                .handoverType(ProjectOperationHandoverType.INITIAL)
                .sequenceNo(1)
                .build();
    }

    @Test
    void builderUsesDraftAsDefaultStatus() {
        ProjectOperationHandover handover = createHandover();

        assertThat(handover.getStatus())
                .isEqualTo(ProjectOperationHandoverStatus.DRAFT);
    }

    @Test
    void builderCreatesInitialHandover() {
        LocalDate plannedDate = LocalDate.of(2026, 12, 1);
        LocalDateTime requestedAt = LocalDateTime.of(2026, 11, 21, 10, 0);

        ProjectOperationHandover handover = ProjectOperationHandover.builder()
                .handoverType(ProjectOperationHandoverType.INITIAL)
                .status(ProjectOperationHandoverStatus.REQUESTED)
                .sequenceNo(1)
                .plannedHandoverDate(plannedDate)
                .requestedAt(requestedAt)
                .notes("Initial operation handover")
                .build();

        assertThat(handover.getHandoverType())
                .isEqualTo(ProjectOperationHandoverType.INITIAL);
        assertThat(handover.getStatus())
                .isEqualTo(ProjectOperationHandoverStatus.REQUESTED);
        assertThat(handover.getSequenceNo()).isEqualTo(1);
        assertThat(handover.getFromParticipant()).isNull();
        assertThat(handover.getPlannedHandoverDate())
                .isEqualTo(plannedDate);
        assertThat(handover.getRequestedAt())
                .isEqualTo(requestedAt);
        assertThat(handover.getNotes())
                .isEqualTo("Initial operation handover");
    }

    @Test
    void builderCreatesOperatorChangeHandover() {
        ProjectOperationHandover handover = ProjectOperationHandover.builder()
                .handoverType(ProjectOperationHandoverType.OPERATOR_CHANGE)
                .status(ProjectOperationHandoverStatus.IN_PROGRESS)
                .sequenceNo(2)
                .build();

        assertThat(handover.getHandoverType())
                .isEqualTo(ProjectOperationHandoverType.OPERATOR_CHANGE);
        assertThat(handover.getStatus())
                .isEqualTo(ProjectOperationHandoverStatus.IN_PROGRESS);
        assertThat(handover.getSequenceNo()).isEqualTo(2);
    }

    @Test
    void builderCreatesReHandover() {
        ProjectOperationHandover handover = ProjectOperationHandover.builder()
                .handoverType(ProjectOperationHandoverType.RE_HANDOVER)
                .sequenceNo(3)
                .build();

        assertThat(handover.getHandoverType())
                .isEqualTo(ProjectOperationHandoverType.RE_HANDOVER);
        assertThat(handover.getSequenceNo()).isEqualTo(3);
        assertThat(handover.getStatus())
                .isEqualTo(ProjectOperationHandoverStatus.DRAFT);
    }

    @Test
    void requestChangesStatusAndRecordsRequesterAndTime() {
        ProjectOperationHandover handover = createHandover();
        User requester = createUser("requester");
        LocalDateTime requestedAt = LocalDateTime.of(2026, 11, 21, 10, 0);

        handover.request(requester, requestedAt);

        assertThat(handover.getStatus())
                .isEqualTo(ProjectOperationHandoverStatus.REQUESTED);
        assertThat(handover.getHandedOverBy())
                .isEqualTo(requester);
        assertThat(handover.getRequestedAt())
                .isEqualTo(requestedAt);
    }

    @Test
    void requestThrowsExceptionWhenRequesterIsNull() {
        ProjectOperationHandover handover = createHandover();

        assertThatThrownBy(() -> handover.request(
                null,
                LocalDateTime.of(2026, 11, 21, 10, 0)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("인수인계 요청자는 필수입니다.");

        assertThat(handover.getStatus())
                .isEqualTo(ProjectOperationHandoverStatus.DRAFT);
        assertThat(handover.getRequestedAt()).isNull();
    }

    @Test
    void requestThrowsExceptionWhenRequestedAtIsNull() {
        ProjectOperationHandover handover = createHandover();

        assertThatThrownBy(() -> handover.request(
                createUser("requester"),
                null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("인수인계 요청 시각은 필수입니다.");

        assertThat(handover.getStatus())
                .isEqualTo(ProjectOperationHandoverStatus.DRAFT);
        assertThat(handover.getHandedOverBy()).isNull();
    }

    @Test
    void completeHandoverSuccessfullyThroughAllStatuses() {
        ProjectOperationHandover handover = createHandover();

        User requester = createUser("requester");
        User accepter = createUser("accepter");

        LocalDateTime requestedAt = LocalDateTime.of(2026, 11, 21, 10, 0);
        LocalDateTime acceptedAt = LocalDateTime.of(2026, 11, 21, 11, 0);
        LocalDateTime completedAt = LocalDateTime.of(2026, 11, 21, 12, 0);

        handover.request(requester, requestedAt);

        assertThat(handover.getStatus())
                .isEqualTo(ProjectOperationHandoverStatus.REQUESTED);

        handover.start();

        assertThat(handover.getStatus())
                .isEqualTo(ProjectOperationHandoverStatus.IN_PROGRESS);

        handover.submitForReview();

        assertThat(handover.getStatus())
                .isEqualTo(ProjectOperationHandoverStatus.REVIEW);

        handover.accept(accepter, acceptedAt);

        assertThat(handover.getStatus())
                .isEqualTo(ProjectOperationHandoverStatus.ACCEPTED);
        assertThat(handover.getAcceptedBy())
                .isEqualTo(accepter);
        assertThat(handover.getAcceptedAt())
                .isEqualTo(acceptedAt);

        handover.complete(completedAt);

        assertThat(handover.getStatus())
                .isEqualTo(ProjectOperationHandoverStatus.COMPLETED);
        assertThat(handover.getCompletedAt())
                .isEqualTo(completedAt);
    }

    @Test
    void startThrowsExceptionWhenStatusIsNotRequested() {
        ProjectOperationHandover handover = createHandover();

        assertThatThrownBy(handover::start)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("현재 상태에서는 해당 인수인계 처리를 수행할 수 없습니다.");

        assertThat(handover.getStatus())
                .isEqualTo(ProjectOperationHandoverStatus.DRAFT);
    }

    @Test
    void acceptThrowsExceptionWhenAcceptedAtIsBeforeRequestedAt() {
        ProjectOperationHandover handover = createHandover();

        LocalDateTime requestedAt = LocalDateTime.of(2026, 11, 21, 11, 0);
        LocalDateTime acceptedAt = LocalDateTime.of(2026, 11, 21, 10, 0);

        handover.request(createUser("requester"), requestedAt);
        handover.start();
        handover.submitForReview();

        assertThatThrownBy(() -> handover.accept(
                createUser("accepter"),
                acceptedAt))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("승인 시각은 요청 시각과 같거나 이후여야 합니다.");

        assertThat(handover.getStatus())
                .isEqualTo(ProjectOperationHandoverStatus.REVIEW);
        assertThat(handover.getAcceptedBy()).isNull();
        assertThat(handover.getAcceptedAt()).isNull();
    }

    @Test
    void completeThrowsExceptionWhenCompletedAtIsBeforeAcceptedAt() {
        ProjectOperationHandover handover = createHandover();

        LocalDateTime requestedAt = LocalDateTime.of(2026, 11, 21, 10, 0);
        LocalDateTime acceptedAt = LocalDateTime.of(2026, 11, 21, 11, 0);
        LocalDateTime completedAt = LocalDateTime.of(2026, 11, 21, 10, 30);

        handover.request(createUser("requester"), requestedAt);
        handover.start();
        handover.submitForReview();
        handover.accept(createUser("accepter"), acceptedAt);

        assertThatThrownBy(() -> handover.complete(completedAt))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("완료 시각은 승인 시각과 같거나 이후여야 합니다.");

        assertThat(handover.getStatus())
                .isEqualTo(ProjectOperationHandoverStatus.ACCEPTED);
        assertThat(handover.getCompletedAt()).isNull();
    }

    @Test
    void rejectChangesStatusFromDraft() {
        ProjectOperationHandover handover = createHandover();

        handover.reject();

        assertThat(handover.getStatus())
                .isEqualTo(ProjectOperationHandoverStatus.REJECTED);
    }

    @Test
    void cancelChangesStatusFromDraft() {
        ProjectOperationHandover handover = createHandover();

        handover.cancel();

        assertThat(handover.getStatus())
                .isEqualTo(ProjectOperationHandoverStatus.CANCELLED);
    }

    @Test
    void rejectThrowsExceptionWhenStatusIsAccepted() {
        ProjectOperationHandover handover = ProjectOperationHandover.builder()
                .handoverType(ProjectOperationHandoverType.INITIAL)
                .status(ProjectOperationHandoverStatus.ACCEPTED)
                .sequenceNo(1)
                .build();

        assertThatThrownBy(handover::reject)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("현재 상태에서는 인수인계를 반려하거나 취소할 수 없습니다.");

        assertThat(handover.getStatus())
                .isEqualTo(ProjectOperationHandoverStatus.ACCEPTED);
    }
}
