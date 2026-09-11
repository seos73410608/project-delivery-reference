package com.seos.pmis.issue.dto.request;

import com.seos.pmis.issue.entity.IssuePriority;
import com.seos.pmis.issue.entity.IssueStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Issue 생성 요청 DTO
 *
 * Project ID는 URL Path Variable로 전달받는다.
 *
 * POST
 * /api/projects/{projectId}/issues
 */
@Getter
@NoArgsConstructor
public class IssueCreateRequest {

    /**
     * Issue 제목
     */
    private String title;

    /**
     * Issue 상세 내용
     */
    private String description;

    /**
     * Issue 상태
     */
    private IssueStatus status;

    /**
     * Issue 중요도
     */
    private IssuePriority priority;

    /**
     * 담당자 ID
     *
     * V1에서는 User Domain과
     * 직접 연관관계를 맺지 않는다.
     */
    private Long assigneeId;

    /**
     * 실제 발생일
     */
    private LocalDate occurredDate;

    /**
     * 조치 목표일
     */
    private LocalDate dueDate;

    /**
     * 표시 순서
     */
    private Integer sortOrder;
}