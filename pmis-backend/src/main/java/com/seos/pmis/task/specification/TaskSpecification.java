
package com.seos.pmis.task.specification;

import com.seos.pmis.task.entity.Task;
import com.seos.pmis.task.entity.TaskPriority;
import com.seos.pmis.task.entity.TaskStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

/**
 * Task Specification
 *
 * Task Entity에 대한 동적 검색 조건을 제공한다.
 *
 * 지원 조건:
 *
 * - projectId
 * - wbsId
 * - scheduleId
 * - assigneeId
 * - status
 * - priority
 * - keyword
 * - startDate
 * - endDate
 * - overlaps
 *
 * JpaSpecificationExecutor와 연계하여
 * Task 검색 및 기간별 조회에 사용한다.
 */
public final class TaskSpecification {

    private TaskSpecification() {
    }

    /**
     * Project ID 조건
     */
    public static Specification<Task> projectId(Long projectId) {

        return (root, query, criteriaBuilder) ->
                projectId == null
                        ? null
                        : criteriaBuilder.equal(
                                root.get("project").get("id"),
                                projectId
                        );
    }

    /**
     * WBS ID 조건
     */
    public static Specification<Task> wbsId(Long wbsId) {

        return (root, query, criteriaBuilder) ->
                wbsId == null
                        ? null
                        : criteriaBuilder.equal(
                                root.get("wbs").get("id"),
                                wbsId
                        );
    }

    /**
     * Schedule ID 조건
     */
    public static Specification<Task> scheduleId(Long scheduleId) {

        return (root, query, criteriaBuilder) ->
                scheduleId == null
                        ? null
                        : criteriaBuilder.equal(
                                root.get("schedule").get("id"),
                                scheduleId
                        );
    }

    /**
     * 담당자 ID 조건
     */
    public static Specification<Task> assigneeId(Long assigneeId) {

        return (root, query, criteriaBuilder) ->
                assigneeId == null
                        ? null
                        : criteriaBuilder.equal(
                                root.get("assignee").get("id"),
                                assigneeId
                        );
    }

    /**
     * Task 상태 조건
     */
    public static Specification<Task> status(TaskStatus status) {

        return (root, query, criteriaBuilder) ->
                status == null
                        ? null
                        : criteriaBuilder.equal(
                                root.get("status"),
                                status
                        );
    }

    /**
     * Task 우선순위 조건
     */
    public static Specification<Task> priority(TaskPriority priority) {

        return (root, query, criteriaBuilder) ->
                priority == null
                        ? null
                        : criteriaBuilder.equal(
                                root.get("priority"),
                                priority
                        );
    }

    /**
     * Task 이름 또는 설명 검색
     *
     * 대소문자를 구분하지 않는 부분 일치 검색을 수행한다.
     */
    public static Specification<Task> keyword(String keyword) {

        return (root, query, criteriaBuilder) -> {

            if (keyword == null || keyword.isBlank()) {
                return null;
            }

            String pattern =
                    "%" + keyword.trim().toLowerCase() + "%";

            return criteriaBuilder.or(

                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("taskName")
                            ),
                            pattern
                    ),

                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("description")
                            ),
                            pattern
                    )
            );
        };
    }

    /**
     * 시작일 이후 조회 조건
     *
     * task.startDate >= startDate
     */
    public static Specification<Task> startDateFrom(LocalDate startDate) {

        return (root, query, criteriaBuilder) ->
                startDate == null
                        ? null
                        : criteriaBuilder.greaterThanOrEqualTo(
                                root.get("startDate"),
                                startDate
                        );
    }

    /**
     * 종료일 이전 조회 조건
     *
     * task.endDate <= endDate
     */
    public static Specification<Task> endDateTo(LocalDate endDate) {

        return (root, query, criteriaBuilder) ->
                endDate == null
                        ? null
                        : criteriaBuilder.lessThanOrEqualTo(
                                root.get("endDate"),
                                endDate
                        );
    }

    /**
     * Task 기간 중복 조건
     *
     * 검색 기간과 하나라도 겹치는 Task를 조회한다.
     *
     * 조건:
     *
     * task.startDate <= searchEnd
     *
     * AND
     *
     * task.endDate >= searchStart
     */
    public static Specification<Task> overlaps(
            LocalDate searchStart,
            LocalDate searchEnd
    ) {

        return (root, query, criteriaBuilder) -> {

            if (searchStart == null || searchEnd == null) {
                return null;
            }

            return criteriaBuilder.and(

                    criteriaBuilder.lessThanOrEqualTo(
                            root.get("startDate"),
                            searchEnd
                    ),

                    criteriaBuilder.greaterThanOrEqualTo(
                            root.get("endDate"),
                            searchStart
                    )
            );
        };
    }
}
