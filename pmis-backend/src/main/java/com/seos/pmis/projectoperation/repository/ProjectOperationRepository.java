
package com.seos.pmis.projectoperation.repository;

import com.seos.pmis.projectoperation.entity.ProjectOperation;
import com.seos.pmis.projectoperation.entity.ProjectOperationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectOperationRepository
        extends JpaRepository<ProjectOperation, Long> {

    /**
     * 프로젝트에 연결된 운영 관계 조회
     */
    List<ProjectOperation> findAllByProject_Id(Long projectId);

    /**
     * 운영 서비스에 연결된 프로젝트 관계 조회
     */
    List<ProjectOperation> findAllByOperation_Id(Long operationId);

    /**
     * 프로젝트와 운영 서비스의 관계 조회
     */
    Optional<ProjectOperation> findByProject_IdAndOperation_Id(
            Long projectId,
            Long operationId
    );

    /**
     * 프로젝트의 특정 상태 관계 조회
     */
    List<ProjectOperation> findAllByProject_IdAndStatus(
            Long projectId,
            ProjectOperationStatus status
    );

    /**
     * 운영 서비스의 특정 상태 관계 조회
     */
    List<ProjectOperation> findAllByOperation_IdAndStatus(
            Long operationId,
            ProjectOperationStatus status
    );

    /**
     * 프로젝트와 운영 서비스 사이에 특정 상태 관계가 존재하는지 확인
     */
    boolean existsByProject_IdAndOperation_IdAndStatus(
            Long projectId,
            Long operationId,
            ProjectOperationStatus status
    );
}
