package com.seos.pmis.evidence.repository;

import com.seos.pmis.evidence.entity.Inspection;
import com.seos.pmis.evidence.enums.InspectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Inspection Repository
 *
 * Inspection 데이터 접근을 담당한다.
 *
 * 검색 조건이 복잡해지는 경우
 * InspectionSpecification을 추가하여
 * JpaSpecificationExecutor를 사용할 수 있다.
 */
public interface InspectionRepository
        extends JpaRepository<Inspection, Long>,
                JpaSpecificationExecutor<Inspection> {

    /**
     * 프로젝트별 Inspection 목록 조회
     *
     * @param projectId 프로젝트 ID
     * @return 해당 프로젝트의 Inspection 목록
     */
    List<Inspection> findAllByProjectId(Long projectId);

    /**
     * 프로젝트 + 상태별 Inspection 조회
     *
     * @param projectId 프로젝트 ID
     * @param status Inspection 상태
     * @return 해당 프로젝트의 상태별 Inspection 목록
     */
    List<Inspection> findAllByProjectIdAndStatus(
            Long projectId,
            InspectionStatus status
    );

    /**
     * 프로젝트별 Inspection 존재 여부
     *
     * 특정 프로젝트에 Inspection이 등록되어 있는지
     * 확인할 때 사용한다.
     *
     * @param projectId 프로젝트 ID
     * @return 존재 여부
     */
    boolean existsByProjectId(Long projectId);
}