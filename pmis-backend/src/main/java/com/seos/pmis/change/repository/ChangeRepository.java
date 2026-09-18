package com.seos.pmis.change.repository;

import com.seos.pmis.change.entity.Change;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ChangeRepository
        extends JpaRepository<Change, Long>,
                JpaSpecificationExecutor<Change> {

    /**
     * 프로젝트별 마지막 Change 조회
     *
     * Change Key 생성 시 사용
     * 예: CHG-001 → CHG-002
     */
    Optional<Change> findTopByProjectIdOrderByIdDesc(Long projectId);

    /**
     * 프로젝트별 Change Key 조회
     *
     * 프로젝트 내 Change Key 중복 검증 시 사용
     */
    Optional<Change> findByProjectIdAndChangeKey(
            Long projectId,
            String changeKey
    );
}