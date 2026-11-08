package com.seos.pmis.evidence.repository;

import com.seos.pmis.evidence.entity.Evidence;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
         * Requirement별 Evidence 존재 여부
         *
         * Evidence가 등록된 Evidence Requirement는
         * 삭제하지 않도록 검증하는 데 사용한다.
         *
         * @param requirementId Evidence Requirement ID
         * @return Evidence 존재 여부
         */
        boolean existsByRequirementId(Long requirementId);

        /**
         * 프로젝트 + Evidence Key 조회
         *
         * Evidence Key는 프로젝트 내부에서 유일하다.
         *
         * @param projectId   프로젝트 ID
         * @param evidenceKey Evidence Key
         * @return Evidence
         */
        Optional<Evidence> findByProjectIdAndEvidenceKey(
                        Long projectId,
                        String evidenceKey);

        /**
         * 프로젝트 + WBS 기준 조회
         *
         * 특정 WBS에 연결된 Evidence를 조회한다.
         *
         * @param projectId 프로젝트 ID
         * @param wbsId     WBS ID
         * @return Evidence 목록
         */
        List<Evidence> findAllByProjectIdAndWbsId(
                        Long projectId,
                        Long wbsId);

        /**
         * 프로젝트 + Evidence Key 존재 여부
         *
         * Evidence Key 생성 시 중복 여부를 확인하는 데 사용한다.
         *
         * @param projectId   프로젝트 ID
         * @param evidenceKey Evidence Key
         * @return 존재 여부
         */
        boolean existsByProjectIdAndEvidenceKey(
                        Long projectId,
                        String evidenceKey);

        /**
         * Evidence 행 비관적 쓰기 잠금 조회
         *
         * EvidenceVersion 생성, 삭제, 검증 등
         * 버전 상태를 변경하는 작업의 동시 실행을 제어한다.
         *
         * 동일한 Evidence ID에 대한 작업은
         * 트랜잭션이 종료될 때까지 순차적으로 실행된다.
         *
         * 반드시 트랜잭션 내부에서 호출해야 한다.
         *
         * @param id Evidence ID
         * @return 잠금이 적용된 Evidence
         */
        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("select e from Evidence e where e.id = :id")
        Optional<Evidence> findByIdForUpdate(
                        @Param("id") Long id);
}