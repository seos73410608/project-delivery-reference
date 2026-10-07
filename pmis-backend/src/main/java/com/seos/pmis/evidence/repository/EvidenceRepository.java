package com.seos.pmis.evidence.repository;

import com.seos.pmis.evidence.entity.Evidence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

/**
 * Evidence Repository
 *
 * Evidence 데이터 접근을 담당한다.
 *
 * 검색 조건이 복잡한 경우
 * EvidenceSpecification과 함께
 * JpaSpecificationExecutor를 사용한다.
 */
public interface EvidenceRepository
        extends JpaRepository<Evidence, Long>,
                JpaSpecificationExecutor<Evidence> {

    /**
     * 프로젝트별 Evidence 목록 조회
     *
     * @param projectId 프로젝트 ID
     * @return 해당 프로젝트의 Evidence 목록
     */
    List<Evidence> findAllByProjectId(Long projectId);

    /**
     * Requirement별 Evidence 목록 조회
     *
     * 하나의 Evidence Requirement에는
     * 여러 개의 Evidence가 등록될 수 있다.
     *
     * @param requirementId Evidence Requirement ID
     * @return 해당 Requirement의 Evidence 목록
     */
    List<Evidence> findAllByRequirementId(Long requirementId);

    /**
     * 프로젝트 + Evidence Key 조회
     *
     * Evidence Key는 프로젝트 내부에서 유일하다.
     *
     * @param projectId 프로젝트 ID
     * @param evidenceKey Evidence Key
     * @return Evidence
     */
    Optional<Evidence> findByProjectIdAndEvidenceKey(
            Long projectId,
            String evidenceKey
    );

    /**
     * 프로젝트 + WBS 기준 조회
     *
     * 특정 WBS에 연결된 Evidence를 조회한다.
     *
     * @param projectId 프로젝트 ID
     * @param wbsId WBS ID
     * @return Evidence 목록
     */
    List<Evidence> findAllByProjectIdAndWbsId(
            Long projectId,
            Long wbsId
    );

    /**
     * 프로젝트 + Evidence Key 존재 여부
     *
     * Evidence Key 생성 시 중복 여부를 확인하는 데 사용한다.
     *
     * @param projectId 프로젝트 ID
     * @param evidenceKey Evidence Key
     * @return 존재 여부
     */
    boolean existsByProjectIdAndEvidenceKey(
            Long projectId,
            String evidenceKey
    );
}