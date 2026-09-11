package com.seos.pmis.issue.dto.request;

import com.seos.pmis.issue.entity.IssuePriority;
import com.seos.pmis.issue.entity.IssueStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Issue 수정 요청 DTO
 *
 * Project는 변경하지 않는다.
 */
@Getter
@NoArgsConstructor
public class IssueUpdateRequest {

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