package com.seos.pmis.task.repository;

import com.seos.pmis.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Task Repository
 *
 * Task Entity에 대한 기본 CRUD 및
 * Project / WBS / Schedule / User 기반 조회 기능을 제공한다.
 *
 * JpaSpecificationExecutor를 통해
 * TaskSpecification과 연계한 동적 검색을 지원한다.
 *
 * Task 수정은 Repository의 별도 update 메서드를 사용하지 않고
 * Service에서 Entity의 상태를 변경한 후
 * JPA Dirty Checking을 통해 반영한다.
 */
public interface TaskRepository
        extends JpaRepository<Task, Long>,
                JpaSpecificationExecutor<Task> {

    /**
     * 프로젝트별 Task 조회
     *
     * @param projectId 프로젝트 ID
     * @return 프로젝트 Task 목록
     */
    List<Task> findByProject_IdOrderBySortOrderAsc(
            Long projectId
    );

    /**
     * WBS별 Task 조회
     *
     * @param wbsId WBS ID
     * @return WBS Task 목록
     */
    List<Task> findByWbs_IdOrderBySortOrderAsc(
            Long wbsId
    );

    /**
     * Schedule별 Task 조회
     *
     * @param scheduleId Schedule ID
     * @return Schedule Task 목록
     */
    List<Task> findBySchedule_IdOrderBySortOrderAsc(
            Long scheduleId
    );

    /**
     * 담당자별 Task 조회
     *
     * 시작일 오름차순으로 조회한다.
     *
     * @param assigneeId 담당자 ID
     * @return 담당 Task 목록
     */
    List<Task> findByAssignee_IdOrderByStartDateAsc(
            Long assigneeId
    );

    /**
     * 특정 WBS에 Task가 존재하는지 확인한다.
     *
     * WBS 삭제 가능 여부 검증에 사용한다.
     *
     * @param wbsId WBS ID
     * @return Task 존재 여부
     */
    boolean existsByWbs_Id(
            Long wbsId
    );

    /**
     * 특정 Schedule에 Task가 존재하는지 확인한다.
     *
     * Schedule 삭제 가능 여부 검증에 사용한다.
     *
     * @param scheduleId Schedule ID
     * @return Task 존재 여부
     */
    boolean existsBySchedule_Id(
            Long scheduleId
    );

    /**
     * 특정 프로젝트에 Task가 존재하는지 확인한다.
     *
     * Project 삭제 가능 여부 검증에 사용한다.
     *
     * @param projectId 프로젝트 ID
     * @return Task 존재 여부
     */
    boolean existsByProject_Id(
            Long projectId
    );
}