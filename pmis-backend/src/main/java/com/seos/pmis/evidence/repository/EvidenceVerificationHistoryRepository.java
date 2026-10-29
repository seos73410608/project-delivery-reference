
package com.seos.pmis.evidence.repository;

import com.seos.pmis.evidence.entity.EvidenceVerificationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvidenceVerificationHistoryRepository
        extends JpaRepository<EvidenceVerificationHistory, Long> {

    List<EvidenceVerificationHistory>
    findAllByEvidenceVersionIdOrderByVerifiedAtDescIdDesc(
            Long evidenceVersionId
    );
}
