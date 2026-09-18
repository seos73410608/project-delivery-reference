package com.seos.pmis.change.dto.response;

import com.seos.pmis.change.entity.Change;
import com.seos.pmis.change.entity.ChangeImpactLevel;
import com.seos.pmis.change.entity.ChangePriority;
import com.seos.pmis.change.entity.ChangeStatus;
import com.seos.pmis.change.entity.ChangeType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ChangeResponse {

    /**
     * 변경 요청 ID
     */
    private Long id;

    /**
     * 프로젝트 ID
     */
    private Long projectId;

    /**
     * 변경 요청 Key
     */
    private String changeKey;

    /**
     * 변경 제목
     */
    private String title;

    /**
     * 변경 설명
     */
    private String description;

    /**
     * 변경 상태
     */
    private ChangeStatus status;

    /**
     * 변경 우선순위
     */
    private ChangePriority priority;

    /**
     * 변경 유형
     */
    private ChangeType changeType;

    /**
     * 변경 영향도
     */
    private ChangeImpactLevel impactLevel;

    /**
     * 변경 요청자 ID
     */
    private Long requesterId;

    /**
     * 변경 담당자 ID
     */
    private Long assigneeId;

    /**
     * 변경 식별일
     */
    private LocalDate identifiedDate;

    /**
     * 변경 요청일
     */
    private LocalDate requestedDate;

    /**
     * 변경 완료 목표일
     */
    private LocalDate dueDate;

    /**
     * 승인일
     */
    private LocalDate approvedDate;

    /**
     * 구현 완료일
     */
    private LocalDate implementedDate;

    /**
     * 검증 완료일
     */
    private LocalDate verifiedDate;

    /**
     * 종료일
     */
    private LocalDate closedDate;

    /**
     * 영향도 분석
     */
    private String impactAnalysis;

    /**
     * 구현 계획
     */
    private String implementationPlan;

    /**
     * 검증 결과
     */
    private String verificationResult;

    /**
     * 승인 의견
     */
    private String approvalComment;

    /**
     * 정렬 순서
     */
    private Integer sortOrder;

    /**
     * 마감일 초과 여부
     *
     * dueDate가 오늘보다 이전이고
     * CLOSED / CANCELLED / REJECTED 상태가 아닌 경우 true
     */
    private boolean overdue;

    /**
     * Entity → Response 변환
     */
    public static ChangeResponse from(Change change) {

        return ChangeResponse.builder()
                .id(change.getId())
                .projectId(change.getProjectId())
                .changeKey(change.getChangeKey())
                .title(change.getTitle())
                .description(change.getDescription())
                .status(change.getStatus())
                .priority(change.getPriority())
                .changeType(change.getChangeType())
                .impactLevel(change.getImpactLevel())
                .requesterId(change.getRequesterId())
                .assigneeId(change.getAssigneeId())
                .identifiedDate(change.getIdentifiedDate())
                .requestedDate(change.getRequestedDate())
                .dueDate(change.getDueDate())
                .approvedDate(change.getApprovedDate())
                .implementedDate(change.getImplementedDate())
                .verifiedDate(change.getVerifiedDate())
                .closedDate(change.getClosedDate())
                .impactAnalysis(change.getImpactAnalysis())
                .implementationPlan(change.getImplementationPlan())
                .verificationResult(change.getVerificationResult())
                .approvalComment(change.getApprovalComment())
                .sortOrder(change.getSortOrder())
                .overdue(isOverdue(change))
                .build();
    }

    /**
     * 마감일 초과 여부 계산
     */
    private static boolean isOverdue(Change change) {

        if (change.getDueDate() == null) {
            return false;
        }

        if (change.getStatus() == ChangeStatus.CLOSED
                || change.getStatus() == ChangeStatus.CANCELLED
                || change.getStatus() == ChangeStatus.REJECTED) {
            return false;
        }

        return change.getDueDate().isBefore(LocalDate.now());
    }
}