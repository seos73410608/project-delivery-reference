package com.seos.pmis.evidence.repository;

import com.seos.pmis.evidence.entity.EvidenceOcrJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EvidenceOcrJobRepository
        extends JpaRepository<EvidenceOcrJob, Long> {

    /**
     * 증적 ID로 OCR 작업 이력을 조회한다.
     * 최신 요청 순으로 반환한다.
     */
    List<EvidenceOcrJob>
    findAllByEvidenceIdOrderByRequestedAtDesc(Long evidenceId);

    /**
     * PMIS 요청 ID로 OCR 작업을 조회한다.
     */
    Optional<EvidenceOcrJob>
    findByPmisRequestId(String pmisRequestId);

    /**
     * Runtime OCR 요청 ID로 OCR 작업을 조회한다.
     */
    Optional<EvidenceOcrJob>
    findByOcrRequestId(String ocrRequestId);

    /**
     * 특정 증적에 해당 PMIS 요청 ID의 작업이 존재하는지 확인한다.
     */
    boolean existsByEvidenceIdAndPmisRequestId(
            Long evidenceId,
            String pmisRequestId
    );
}
