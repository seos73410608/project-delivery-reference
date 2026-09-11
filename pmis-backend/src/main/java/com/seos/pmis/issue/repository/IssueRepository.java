package com.seos.pmis.issue.repository;

import com.seos.pmis.issue.entity.Issue;
import com.seos.pmis.issue.entity.IssuePriority;
import com.seos.pmis.issue.entity.IssueStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Issue Repository
 *
 * Issue Entity의 데이터 접근을 담당한다.
 *
 * 기본 CRUD 기능은 JpaRepository를 사용하고,
 * 동적 검색은 JpaSpecificationExecutor를 사용한다.
 *
 * 주요 기능:
 * - Issue CRUD
 * - Project별 Issue 조회
 * - Status별 Issue 조회
 * - Priority별 Issue 조회
 * - 동적 검색
 */
public interface IssueRepository extends
        JpaRepository<Issue, Long>,
        JpaSpecificationExecutor<Issue> {

    /**
     * Project별 Issue 조회
     *
     * 동일 Project에 속한 Issue를
     * sortOrder 오름차순으로 조회한다.
     *
     * @param projectId Project ID
     * @return Issue 목록
     */
    List<Issue> findByProject_IdOrderBySortOrderAsc(
            Long projectId
    );


    /**
     * Project와 Issue 상태로 조회
     *
     * @param projectId Project ID
     * @param status Issue 상태
     * @return Issue 목록
     */
    List<Issue> findByProject_IdAndStatusOrderBySortOrderAsc(
            Long projectId,
            IssueStatus status
    );


    /**
     * Project와 Issue 중요도로 조회
     *
     * @param projectId Project ID
     * @param priority Issue 중요도
     * @return Issue 목록
     */
    List<Issue> findByProject_IdAndPriorityOrderBySortOrderAsc(
            Long projectId,
            IssuePriority priority
    );


    /**
     * 담당자별 Issue 조회
     *
     * @param assigneeId 담당자 ID
     * @return Issue 목록
     */
    List<Issue> findByAssigneeIdOrderBySortOrderAsc(
            Long assigneeId
    );


    /**
     * Project와 담당자 기준 Issue 조회
     *
     * @param projectId Project ID
     * @param assigneeId 담당자 ID
     * @return Issue 목록
     */
    List<Issue> findByProject_IdAndAssigneeIdOrderBySortOrderAsc(
            Long projectId,
            Long assigneeId
    );
}