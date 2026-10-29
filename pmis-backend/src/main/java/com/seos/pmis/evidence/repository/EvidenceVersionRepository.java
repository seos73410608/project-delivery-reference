
package com.seos.pmis.evidence.repository;

import com.seos.pmis.evidence.entity.EvidenceVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EvidenceVersionRepository
        extends JpaRepository<EvidenceVersion, Long> {

    List<EvidenceVersion> findAllByEvidenceIdOrderByVersionNumberDesc(
            Long evidenceId
    );

    Optional<EvidenceVersion> findByEvidenceIdAndVersionNumber(
            Long evidenceId,
            Integer versionNumber
    );

    Optional<EvidenceVersion> findTopByEvidenceIdOrderByVersionNumberDesc(
            Long evidenceId
    );

    boolean existsByEvidenceId(Long evidenceId);
}
