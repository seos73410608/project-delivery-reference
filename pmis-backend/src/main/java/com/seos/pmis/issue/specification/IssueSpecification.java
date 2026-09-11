package com.seos.pmis.issue.specification;

import com.seos.pmis.issue.dto.request.IssueSearchRequest;
import com.seos.pmis.issue.entity.Issue;
import com.seos.pmis.issue.entity.IssuePriority;
import com.seos.pmis.issue.entity.IssueStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Issue Specification
 *
 * Issue 검색을 위한 동적 Query 조건을 정의한다.
 *
 * 지원 검색 조건:
 *
 * - projectId
 * - keyword
 * - status
 * - priority
 * - assigneeId
 *
 * Service에서 Specification을 조합하여
 * 동적 검색을 수행한다.
 *
 * Spring Data JPA Specification 조합 시
 * deprecated 된 Specification.where() 사용을 피하기 위해
 * Specification.allOf() 기반으로 조건을 조합한다.
 */
public final class IssueSpecification {

    /**
     * Utility Class
     *
     * Instance 생성을 방지한다.
     */
    private IssueSpecification() {
    }


    /**
     * Issue 검색 조건을 조합한다.
     *
     * 지원 조건:
     *
     * - projectId
     * - keyword
     * - status
     * - priority
     * - assigneeId
     *
     * 조건이 존재하는 Specification만
     * List에 추가한다.
     *
     * 최종적으로 Specification.allOf()를 사용하여
     * AND 조건으로 조합한다.
     *
     * @param request Issue 검색 요청
     * @return 조합된 Issue Specification
     */
    public static Specification<Issue> search(
            IssueSearchRequest request
    ) {

        List<Specification<Issue>> specifications =
                new ArrayList<>();


        Specification<Issue> projectIdSpecification =
                projectId(
                        request.getProjectId()
                );

        if (projectIdSpecification != null) {

            specifications.add(
                    projectIdSpecification
            );
        }


        Specification<Issue> keywordSpecification =
                keyword(
                        request.getKeyword()
                );

        if (keywordSpecification != null) {

            specifications.add(
                    keywordSpecification
            );
        }


        Specification<Issue> statusSpecification =
                status(
                        request.getStatus()
                );

        if (statusSpecification != null) {

            specifications.add(
                    statusSpecification
            );
        }


        Specification<Issue> prioritySpecification =
                priority(
                        request.getPriority()
                );

        if (prioritySpecification != null) {

            specifications.add(
                    prioritySpecification
            );
        }


        Specification<Issue> assigneeSpecification =
                assigneeId(
                        request.getAssigneeId()
                );

        if (assigneeSpecification != null) {

            specifications.add(
                    assigneeSpecification
            );
        }


        return Specification.allOf(
                specifications
        );
    }


    /**
     * Project ID 조건
     *
     * @param projectId Project ID
     * @return Project ID Specification
     */
    public static Specification<Issue> projectId(
            Long projectId
    ) {

        if (projectId == null) {

            return null;
        }

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("project").get("id"),
                        projectId
                );
    }


    /**
     * Keyword 검색 조건
     *
     * Issue 제목과 상세 내용을 대상으로 검색한다.
     *
     * 검색 방식:
     *
     * title LIKE %keyword%
     * OR
     * description LIKE %keyword%
     *
     * 대소문자를 구분하지 않는다.
     *
     * @param keyword 검색 Keyword
     * @return Keyword Specification
     */
    public static Specification<Issue> keyword(
            String keyword
    ) {

        if (keyword == null ||
                keyword.isBlank()) {

            return null;
        }


        String searchKeyword =
                "%" +
                keyword.trim().toLowerCase() +
                "%";


        return (root, query, criteriaBuilder) ->
                criteriaBuilder.or(

                        criteriaBuilder.like(

                                criteriaBuilder.lower(
                                        root.get("title")
                                ),

                                searchKeyword
                        ),

                        criteriaBuilder.like(

                                criteriaBuilder.lower(
                                        root.get("description")
                                ),

                                searchKeyword
                        )
                );
    }


    /**
     * Issue Status 조건
     *
     * @param status Issue 상태
     * @return Status Specification
     */
    public static Specification<Issue> status(
            IssueStatus status
    ) {

        if (status == null) {

            return null;
        }

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("status"),
                        status
                );
    }


    /**
     * Issue Priority 조건
     *
     * @param priority Issue 중요도
     * @return Priority Specification
     */
    public static Specification<Issue> priority(
            IssuePriority priority
    ) {

        if (priority == null) {

            return null;
        }

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("priority"),
                        priority
                );
    }


    /**
     * Issue 담당자 조건
     *
     * @param assigneeId 담당자 ID
     * @return Assignee Specification
     */
    public static Specification<Issue> assigneeId(
            Long assigneeId
    ) {

        if (assigneeId == null) {

            return null;
        }

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("assigneeId"),
                        assigneeId
                );
    }
}