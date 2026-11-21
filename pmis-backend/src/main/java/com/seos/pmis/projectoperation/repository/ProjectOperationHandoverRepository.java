
package com.seos.pmis.projectoperation.repository;

import com.seos.pmis.projectoperation.entity.ProjectOperationHandover;
import com.seos.pmis.projectoperation.entity.ProjectOperationHandoverStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProjectOperationHandoverRepository
        extends JpaRepository<ProjectOperationHandover, Long> {

    /**
     * 프로젝트 운영 관계의 전체 인수인계 이력 조회
     */
    List<ProjectOperationHandover>
    findAllByProjectOperation_IdOrderBySequenceNoAsc(Long projectOperationId);

    /**
     * 프로젝트 운영 관계의 특정 순번 인수인계 조회
     */
    Optional<ProjectOperationHandover>
    findByProjectOperation_IdAndSequenceNo(
            Long projectOperationId,
            Integer sequenceNo
    );

    /**
     * 프로젝트 운영 관계의 마지막 인수인계 이력 조회
     */
    Optional<ProjectOperationHandover>
    findFirstByProjectOperation_IdOrderBySequenceNoDesc(Long projectOperationId);

    /**
     * 프로젝트 운영 관계의 특정 상태 인수인계 조회
     */
    List<ProjectOperationHandover>
    findAllByProjectOperation_IdAndStatus(
            Long projectOperationId,
            ProjectOperationHandoverStatus status
    );

    /**
     * 프로젝트 운영 관계에 진행 중인 인수인계가 존재하는지 확인
     */
    boolean existsByProjectOperation_IdAndStatusIn(
            Long projectOperationId,
            Collection<ProjectOperationHandoverStatus> statuses
    );

    /**
     * 특정 운영 서비스의 인수인계 이력 조회
     */
    List<ProjectOperationHandover>
    findAllByOperation_IdOrderByCreatedAtDesc(Long operationId);
}
