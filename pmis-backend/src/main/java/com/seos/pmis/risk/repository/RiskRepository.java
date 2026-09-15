package com.seos.pmis.risk.repository;

import com.seos.pmis.risk.entity.Risk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface RiskRepository
        extends JpaRepository<Risk, Long>,
                JpaSpecificationExecutor<Risk> {

    Optional<Risk> findTopByProjectIdOrderByIdDesc(Long projectId);

    Optional<Risk> findByProjectIdAndRiskKey(
            Long projectId,
            String riskKey
    );
}