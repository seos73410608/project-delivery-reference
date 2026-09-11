package com.seos.pmis.issue.dto.request;

import com.seos.pmis.issue.entity.IssuePriority;
import com.seos.pmis.issue.entity.IssueStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Issue 검색 요청 DTO
 *
 * GET /api/issues
 */
@Getter
@NoArgsConstructor
public class IssueSearchRequest {

    /**
     * 프로젝트 ID
     */
    private Long projectId;

    /**
     * 키워드 검색
     *
     * 제목과 상세 내용을 대상으로 검색한다.
     */
    private String keyword;

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
     * 페이지 번호
     *
     * 0부터 시작한다.
     */
    private Integer page = 0;

    /**
     * 페이지 크기
     */
    private Integer size = 20;

    /**
     * 정렬 필드
     */
    private String sortBy;

    /**
     * 정렬 방향
     *
     * ASC
     * DESC
     */
    private String direction;
}