package com.seos.pmis.evidence.repository;

import com.seos.pmis.evidence.entity.EvidenceVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EvidenceVersionRepository
                extends JpaRepository<EvidenceVersion, Long> {

        /**
         * 전체 버전 조회
         * - 논리 삭제된 버전 포함
         * - 버전 번호 중복 방지 및 관리자 이력 확인 등에 사용
         */
        List<EvidenceVersion> findAllByEvidenceIdOrderByVersionNumberDesc(
                        Long evidenceId);

        /**
         * 특정 버전 조회
         * - 논리 삭제된 버전 포함
         * - 삭제 이력 확인 등 제한된 용도로 사용
         */
        Optional<EvidenceVersion> findByEvidenceIdAndVersionNumber(
                        Long evidenceId,
                        Integer versionNumber);

        /**
         * 가장 높은 버전 번호 조회
         * - 논리 삭제된 버전 포함
         * - 신규 버전 번호 산정에 사용
         */
        Optional<EvidenceVersion> findTopByEvidenceIdOrderByVersionNumberDesc(
                        Long evidenceId);

        /**
         * 전체 버전 존재 여부
         * - 논리 삭제된 버전 포함
         */
        boolean existsByEvidenceId(Long evidenceId);

        /**
         * 삭제되지 않은 버전 목록
         * - 일반적인 버전 목록 화면에 사용
         */
        List<EvidenceVersion> findAllByEvidenceIdAndDeletedFalseOrderByVersionNumberDesc(
                        Long evidenceId);

        /**
         * 삭제되지 않은 특정 버전 조회
         */
        Optional<EvidenceVersion> findByEvidenceIdAndVersionNumberAndDeletedFalse(
                        Long evidenceId,
                        Integer versionNumber);

        /**
         * 가장 높은 활성 버전 조회
         * - 삭제되지 않은 버전만 대상
         */
        Optional<EvidenceVersion> findTopByEvidenceIdAndDeletedFalseOrderByVersionNumberDesc(
                        Long evidenceId);

        /**
         * 활성 버전 존재 여부
         */
        boolean existsByEvidenceIdAndDeletedFalse(Long evidenceId);

        /**
         * 논리 삭제된 버전 목록
         * - 삭제 이력 확인용
         */
        List<EvidenceVersion> findAllByEvidenceIdAndDeletedTrueOrderByVersionNumberDesc(
                        Long evidenceId);
}