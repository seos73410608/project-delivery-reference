
package com.seos.pmis.projectoperation.entity;

import com.seos.common.entity.BaseEntity;
import com.seos.pmis.operation.entity.Operation;
import com.seos.pmis.operation.entity.OperationParticipant;
import com.seos.pmis.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@Table(name = "project_operation_handovers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectOperationHandover extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 프로젝트와 운영 서비스의 관계
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_operation_id", nullable = false)
    private ProjectOperation projectOperation;

    /**
     * 인수인계 대상 운영 서비스
     *
     * projectOperation의 Operation과 동일해야 하며,
     * DB 복합 FK로 일치 여부를 검증한다.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "operation_id", nullable = false)
    private Operation operation;

    /**
     * 인수인계 유형
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "handover_type", nullable = false, length = 30)
    private ProjectOperationHandoverType handoverType;

    /**
     * 인수인계 진행 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ProjectOperationHandoverStatus status =
            ProjectOperationHandoverStatus.DRAFT;

    /**
     * 프로젝트 운영 관계 내 인수인계 순번
     */
    @Column(name = "sequence_no", nullable = false)
    private Integer sequenceNo;

    /**
     * 이전 인수인계 이력
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_handover_id")
    private ProjectOperationHandover previousHandover;

    /**
     * 인계하는 기존 운영 참여자
     * 최초 인수인계(INITIAL)는 null
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_participant_id")
    private OperationParticipant fromParticipant;

    /**
     * 인수하는 신규 운영 참여자
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_participant_id", nullable = false)
    private OperationParticipant toParticipant;

    /**
     * 인수인계를 요청하거나 전달하는 담당자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "handed_over_by_user_id")
    private User handedOverBy;

    /**
     * 인수인계를 승인한 담당자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accepted_by_user_id")
    private User acceptedBy;

    /**
     * 예정 인수인계 일자
     */
    @Column(name = "planned_handover_date")
    private LocalDate plannedHandoverDate;

    /**
     * 인수인계 요청 시각
     */
    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    /**
     * 인수인계 승인 시각
     */
    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    /**
     * 인수인계 완료 시각
     */
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /**
     * 인수인계 관련 비고
     */
    @Column(columnDefinition = "TEXT")
    private String notes;

    /**
     * 인수인계 요청
     */
    public void request(User handedOverBy, LocalDateTime requestedAt) {
        validateCurrentStatus(ProjectOperationHandoverStatus.DRAFT);

        if (handedOverBy == null) {
            throw new IllegalArgumentException("인수인계 요청자는 필수입니다.");
        }

        if (requestedAt == null) {
            throw new IllegalArgumentException("인수인계 요청 시각은 필수입니다.");
        }

        this.handedOverBy = handedOverBy;
        this.requestedAt = requestedAt;
        this.status = ProjectOperationHandoverStatus.REQUESTED;
    }

    /**
     * 인수인계 작업 시작
     */
    public void start() {
        validateCurrentStatus(ProjectOperationHandoverStatus.REQUESTED);
        this.status = ProjectOperationHandoverStatus.IN_PROGRESS;
    }

    /**
     * 인수인계 검토 요청
     */
    public void submitForReview() {
        validateCurrentStatus(ProjectOperationHandoverStatus.IN_PROGRESS);
        this.status = ProjectOperationHandoverStatus.REVIEW;
    }

    /**
     * 인수인계 승인
     */
    public void accept(User acceptedBy, LocalDateTime acceptedAt) {
        validateCurrentStatus(ProjectOperationHandoverStatus.REVIEW);

        if (acceptedBy == null) {
            throw new IllegalArgumentException("인수인계 승인자는 필수입니다.");
        }

        if (acceptedAt == null) {
            throw new IllegalArgumentException("인수인계 승인 시각은 필수입니다.");
        }

        if (this.requestedAt == null
                || acceptedAt.isBefore(this.requestedAt)) {
            throw new IllegalArgumentException(
                    "승인 시각은 요청 시각과 같거나 이후여야 합니다.");
        }

        this.acceptedBy = acceptedBy;
        this.acceptedAt = acceptedAt;
        this.status = ProjectOperationHandoverStatus.ACCEPTED;
    }

    /**
     * 인수인계 최종 완료
     */
    public void complete(LocalDateTime completedAt) {
        validateCurrentStatus(ProjectOperationHandoverStatus.ACCEPTED);

        if (completedAt == null) {
            throw new IllegalArgumentException("인수인계 완료 시각은 필수입니다.");
        }

        if (this.acceptedAt == null
                || completedAt.isBefore(this.acceptedAt)) {
            throw new IllegalArgumentException(
                    "완료 시각은 승인 시각과 같거나 이후여야 합니다.");
        }

        this.completedAt = completedAt;
        this.status = ProjectOperationHandoverStatus.COMPLETED;
    }

    /**
     * 인수인계 반려
     */
    public void reject() {
        validateCancellableStatus();
        this.status = ProjectOperationHandoverStatus.REJECTED;
    }

    /**
     * 인수인계 취소
     */
    public void cancel() {
        validateCancellableStatus();
        this.status = ProjectOperationHandoverStatus.CANCELLED;
    }

    /**
     * 현재 상태 검증
     */
    private void validateCurrentStatus(ProjectOperationHandoverStatus expected) {
        if (this.status != expected) {
            throw new IllegalStateException(
                    "현재 상태에서는 해당 인수인계 처리를 수행할 수 없습니다.");
        }
    }

    /**
     * 반려 및 취소 가능 상태 검증
     */
    private void validateCancellableStatus() {
        if (this.status != ProjectOperationHandoverStatus.DRAFT
                && this.status != ProjectOperationHandoverStatus.REQUESTED
                && this.status != ProjectOperationHandoverStatus.IN_PROGRESS
                && this.status != ProjectOperationHandoverStatus.REVIEW) {
            throw new IllegalStateException(
                    "현재 상태에서는 인수인계를 반려하거나 취소할 수 없습니다.");
        }
    }
}
