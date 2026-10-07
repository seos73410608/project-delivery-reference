package com.seos.pmis.evidence.repository;

import com.seos.pmis.evidence.entity.EvidenceRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

/**
 * Evidence Requirement Repository
 *
 * Evidence Requirement 데이터 접근을 담당한다.
 *
 * 검색 조건이 복잡한 경우
 * EvidenceRequirementSpecification과 함께
 * JpaSpecificationExecutor를 사용한다.
 */
public interface EvidenceRequirementRepository
        extends JpaRepository<EvidenceRequirement, Long>,
                JpaSpecificationExecutor<EvidenceRequirement> {

    /**
     * 프로젝트별 Evidence Requirement 목록 조회
     *
     * @param projectId 프로젝트 ID
     * @return 해당 프로젝트의 Evidence Requirement 목록
     */
    List<EvidenceRequirement> findAllByProjectId(Long projectId);

    /**
     * 프로젝트 + Requirement Key 조회
     *
     * Requirement Key는 프로젝트 내부에서 유일하다.
     *
     * @param projectId 프로젝트 ID
     * @param requirementKey Requirement Key
     * @return Evidence Requirement
     */
    Optional<EvidenceRequirement> findByProjectIdAndRequirementKey(
            Long projectId,
            String requirementKey
    );

    /**
     * 프로젝트 + WBS 기준 조회
     *
     * 특정 WBS에 연결된 Evidence Requirement를 조회한다.
     *
     * @param projectId 프로젝트 ID
     * @param wbsId WBS ID
     * @return Evidence Requirement 목록
     */
    List<EvidenceRequirement> findAllByProjectIdAndWbsId(
            Long projectId,
            Long wbsId
    );

    /**
     * 프로젝트별 Requirement Key 존재 여부
     *
     * Requirement Key 생성 시 중복 여부를 확인하는 데 사용한다.
     *
     * @param projectId 프로젝트 ID
     * @param requirementKey Requirement Key
     * @return 존재 여부
     */
    boolean existsByProjectIdAndRequirementKey(
            Long projectId,
            String requirementKey
    );
}