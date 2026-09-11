package com.seos.pmis.issue.dto.response;

import com.seos.pmis.issue.entity.Issue;
import com.seos.pmis.issue.entity.IssuePriority;
import com.seos.pmis.issue.entity.IssueStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Issue Response
 *
 * Issue 조회 결과를 반환한다.
 *
 * Entity를 직접 외부에 노출하지 않고
 * IssueResponse DTO를 통해 데이터를 전달한다.
 */
@Getter
@Builder
public class IssueResponse {

    /**
     * Issue PK
     */
    private final Long id;


    /**
     * 소속 Project ID
     */
    private final Long projectId;


    /**
     * Issue 식별 Key
     */
    private final String issueKey;


    /**
     * Issue 제목
     */
    private final String title;


    /**
     * Issue 상세 내용
     */
    private final String description;


    /**
     * Issue 상태
     */
    private final IssueStatus status;


    /**
     * Issue 중요도
     */
    private final IssuePriority priority;


    /**
     * 담당자 ID
     */
    private final Long assigneeId;


    /**
     * 등록자 ID
     */
    private final Long reporterId;


    /**
     * 실제 Issue 발생일
     */
    private final LocalDate occurredDate;


    /**
     * 조치 목표일
     */
    private final LocalDate dueDate;


    /**
     * Issue 해결일
     */
    private final LocalDate resolvedDate;


    /**
     * 표시 순서
     */
    private final Integer sortOrder;


    /**
     * 생성일시
     */
    private final LocalDateTime createdAt;


    /**
     * 수정일시
     */
    private final LocalDateTime updatedAt;


    /**
     * Issue Entity를
     * IssueResponse로 변환한다.
     *
     * @param issue Issue Entity
     * @return Issue Response
     */
    public static IssueResponse from(
            Issue issue
    ) {

        if (issue == null) {
            return null;
        }

        return IssueResponse.builder()
                .id(issue.getId())
                .projectId(
                        issue.getProject().getId()
                )
                .issueKey(issue.getIssueKey())
                .title(issue.getTitle())
                .description(issue.getDescription())
                .status(issue.getStatus())
                .priority(issue.getPriority())
                .assigneeId(issue.getAssigneeId())
                .reporterId(issue.getReporterId())
                .occurredDate(issue.getOccurredDate())
                .dueDate(issue.getDueDate())
                .resolvedDate(issue.getResolvedDate())
                .sortOrder(issue.getSortOrder())
                .createdAt(issue.getCreatedAt())
                .updatedAt(issue.getUpdatedAt())
                .build();
    }
}