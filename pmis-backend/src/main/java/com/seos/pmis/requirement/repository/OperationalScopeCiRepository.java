
package com.seos.pmis.requirement.repository;

import com.seos.pmis.requirement.entity.OperationalScopeCi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OperationalScopeCiRepository
        extends JpaRepository<OperationalScopeCi, Long> {

    List<OperationalScopeCi> findAllByOperationalScopeId(Long operationalScopeId);

    Optional<OperationalScopeCi> findByOperationalScopeIdAndConfigurationItemId(
            Long operationalScopeId,
            Long configurationItemId
    );

    boolean existsByOperationalScopeIdAndConfigurationItemId(
            Long operationalScopeId,
            Long configurationItemId
    );
}
