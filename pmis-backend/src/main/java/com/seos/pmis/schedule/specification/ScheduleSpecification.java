package com.seos.pmis.schedule.specification;

import com.seos.pmis.schedule.entity.Schedule;
import com.seos.pmis.schedule.entity.ScheduleStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

/**
 * Schedule Specification
 *
 * Schedule Entity에 대한 동적 검색 조건을 제공한다.
 *
 * 검색 조건은 다음과 같이 조합할 수 있다.
 *
 * - projectId
 * - wbsId
 * - status
 * - keyword
 * - startDate
 * - endDate
 * - overlaps
 *
 * JpaSpecificationExecutor와 연계하여
 * Schedule 검색 API에서 사용한다.
 */
public final class ScheduleSpecification {

    private ScheduleSpecification() {
    }

    /**
     * 프로젝트 ID 조건
     *
     * Schedule의 Project 연관관계를 통해
     * Project ID를 조회한다.
     *
     * 조건:
     *
     * schedule.project.id = projectId
     *
     * @param projectId 프로젝트 ID
     * @return Specification
     */
    public static Specification<Schedule> projectId(
            Long projectId
    ) {
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
     *
     * Schedule의 Wbs 연관관계를 통해
     * WBS ID를 조회한다.
     *
     * 조건:
     *
     * schedule.wbs.id = wbsId
     *
     * @param wbsId WBS ID
     * @return Specification
     */
    public static Specification<Schedule> wbsId(
            Long wbsId
    ) {
        return (root, query, criteriaBuilder) ->
                wbsId == null
                        ? null
                        : criteriaBuilder.equal(
                                root.get("wbs").get("id"),
                                wbsId
                        );
    }

    /**
     * Schedule Status 조건
     *
     * 조건:
     *
     * schedule.status = status
     *
     * @param status Schedule 상태
     * @return Specification
     */
    public static Specification<Schedule> status(
            ScheduleStatus status
    ) {
        return (root, query, criteriaBuilder) ->
                status == null
                        ? null
                        : criteriaBuilder.equal(
                                root.get("status"),
                                status
                        );
    }

    /**
     * Schedule 이름 또는 설명 검색
     *
     * keyword가 존재하면
     * scheduleName 또는 description에 대해
     * 대소문자를 구분하지 않는 부분 일치 검색을 수행한다.
     *
     * 조건:
     *
     * scheduleName LIKE %keyword%
     * OR
     * description LIKE %keyword%
     *
     * @param keyword 검색어
     * @return Specification
     */
    public static Specification<Schedule> keyword(
            String keyword
    ) {
        return (root, query, criteriaBuilder) -> {

            if (keyword == null || keyword.isBlank()) {
                return null;
            }

            String pattern =
                    "%" + keyword.trim().toLowerCase() + "%";

            return criteriaBuilder.or(
                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("scheduleName")
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
     * 시작일 조건
     *
     * 지정한 날짜 이후에 시작하는 Schedule을 조회한다.
     *
     * 조건:
     *
     * schedule.startDate >= startDate
     *
     * @param startDate 조회 시작일
     * @return Specification
     */
    public static Specification<Schedule> startDateFrom(
            LocalDate startDate
    ) {
        return (root, query, criteriaBuilder) ->
                startDate == null
                        ? null
                        : criteriaBuilder.greaterThanOrEqualTo(
                                root.get("startDate"),
                                startDate
                        );
    }

    /**
     * 종료일 조건
     *
     * 지정한 날짜 이전에 종료하는 Schedule을 조회한다.
     *
     * 조건:
     *
     * schedule.endDate <= endDate
     *
     * @param endDate 조회 종료일
     * @return Specification
     */
    public static Specification<Schedule> endDateTo(
            LocalDate endDate
    ) {
        return (root, query, criteriaBuilder) ->
                endDate == null
                        ? null
                        : criteriaBuilder.lessThanOrEqualTo(
                                root.get("endDate"),
                                endDate
                        );
    }

    /**
     * Schedule 기간 중복 조건
     *
     * 특정 검색 기간과 겹치는 Schedule을 조회한다.
     *
     * Schedule:
     *
     * startDate -------- endDate
     *
     * 검색기간:
     *
     *       searchStart -------- searchEnd
     *
     * 두 기간이 겹치는 조건:
     *
     * schedule.startDate <= searchEnd
     * AND
     * schedule.endDate >= searchStart
     *
     * @param searchStart 검색 시작일
     * @param searchEnd 검색 종료일
     * @return Specification
     */
    public static Specification<Schedule> overlaps(
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