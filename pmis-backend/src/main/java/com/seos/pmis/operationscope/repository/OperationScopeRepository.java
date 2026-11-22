
package com.seos.pmis.operationscope.repository;

import com.seos.pmis.operationscope.entity.OperationScope;
import com.seos.pmis.operationscope.entity.OperationScopeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OperationScopeRepository
        extends JpaRepository<OperationScope, Long> {

    List<OperationScope> findAllByOperation_Id(Long operationId);

    List<OperationScope> findAllByOperationalScope_Id(
            Long operationalScopeId);

    Optional<OperationScope> findByOperation_IdAndOperationalScope_IdAndStatus(
            Long operationId,
            Long operationalScopeId,
            OperationScopeStatus status);

    boolean existsByOperation_IdAndOperationalScope_IdAndStatus(
            Long operationId,
            Long operationalScopeId,
            OperationScopeStatus status);
}
