package com.seos.pmis.project.repository;

import com.seos.pmis.project.entity.Project;
import com.seos.pmis.project.entity.ProjectPriority;
import com.seos.pmis.project.entity.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface ProjectRepository
        extends JpaRepository<Project, Long>,
                JpaSpecificationExecutor<Project> {


    /**
     * 프로젝트 코드 조회
     */
    Optional<Project> findByProjectCode(
            String projectCode
    );


    /**
     * 프로젝트 코드 중복 확인
     */
    boolean existsByProjectCode(
            String projectCode
    );


    /**
     * 프로젝트명 조회
     */
    Optional<Project> findByProjectName(
            String projectName
    );


    /**
     * 상태별 프로젝트 개수
     *
     * Dashboard 통계
     */
    long countByStatus(
            ProjectStatus status
    );


    /**
     * 우선순위별 프로젝트 개수
     *
     * Dashboard 통계
     */
    long countByPriority(
            ProjectPriority priority
    );


    /**
     * 최근 생성 프로젝트 개수
     *
     * 최근 30일 KPI
     */
    long countByCreatedAtAfter(
            LocalDateTime dateTime
    );


    /**
     * 종료 예정 프로젝트 개수
     *
     * 앞으로 30일 KPI
     */
    long countByEndDateBetween(
            LocalDate startDate,
            LocalDate endDate
    );

}