package com.seos.pmis.schedule.repository;

import com.seos.pmis.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Schedule Repository
 *
 * Schedule Entity에 대한 기본 CRUD 및
 * Project / WBS 기반 조회 기능을 제공한다.
 *
 * JpaSpecificationExecutor를 통해
 * ScheduleSpecification과 연계한
 * 동적 검색을 지원한다.
 *
 * Schedule 수정은 Repository의 별도 update 메서드를 사용하지 않고
 * Service에서 Entity의 상태를 변경한 후
 * JPA Dirty Checking을 통해 반영한다.
 */
public interface ScheduleRepository
        extends JpaRepository<Schedule, Long>,
                JpaSpecificationExecutor<Schedule> {

    /**
     * 프로젝트별 Schedule 조회
     *
     * Schedule Entity의 project 연관관계를 통해
     * 특정 프로젝트에 속한 Schedule을 조회한다.
     *
     * sortOrder 오름차순으로 정렬한다.
     *
     * Spring Data JPA의 Property Traversal을 사용하여
     * project.id 조건을 표현한다.
     *
     * @param projectId 프로젝트 ID
     * @return 프로젝트 Schedule 목록
     */
    List<Schedule> findByProject_IdOrderBySortOrderAsc(
            Long projectId
    );

    /**
     * WBS별 Schedule 조회
     *
     * Schedule Entity의 wbs 연관관계를 통해
     * 특정 WBS에 연결된 Schedule을 조회한다.
     *
     * sortOrder 오름차순으로 정렬한다.
     *
     * @param wbsId WBS ID
     * @return WBS Schedule 목록
     */
    List<Schedule> findByWbs_IdOrderBySortOrderAsc(
            Long wbsId
    );

    /**
     * 프로젝트 + WBS별 Schedule 조회
     *
     * 특정 Project에 속하면서
     * 특정 WBS에 연결된 Schedule을 조회한다.
     *
     * project.id와 wbs.id를 함께 조건으로 사용한다.
     *
     * @param projectId 프로젝트 ID
     * @param wbsId WBS ID
     * @return Schedule 목록
     */
    List<Schedule> findByProject_IdAndWbs_IdOrderBySortOrderAsc(
            Long projectId,
            Long wbsId
    );

    /**
     * 특정 WBS에 연결된 Schedule 존재 여부
     *
     * WBS 삭제 또는 WBS 관련 검증 시
     * Schedule 존재 여부 확인에 사용한다.
     *
     * @param wbsId WBS ID
     * @return Schedule 존재 여부
     */
    boolean existsByWbs_Id(
            Long wbsId
    );

    /**
     * 특정 프로젝트에 Schedule이 존재하는지 확인한다.
     *
     * Project 삭제 또는
     * 프로젝트별 Schedule 존재 여부 확인에 사용한다.
     *
     * @param projectId 프로젝트 ID
     * @return Schedule 존재 여부
     */
    boolean existsByProject_Id(
            Long projectId
    );
}