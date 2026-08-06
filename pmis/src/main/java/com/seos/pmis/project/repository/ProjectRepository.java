package com.seos.pmis.project.repository;

import com.seos.pmis.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ProjectRepository
        extends JpaRepository<Project, Long>,
                JpaSpecificationExecutor<Project> {

    /**
     * 프로젝트 코드 조회
     */
    Optional<Project> findByProjectCode(String projectCode);

    /**
     * 프로젝트 코드 중복 확인
     */
    boolean existsByProjectCode(String projectCode);

    /**
     * 프로젝트명 조회
     */
    Optional<Project> findByProjectName(String projectName);

}