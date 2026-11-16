
package com.seos.pmis.requirement.repository;

import com.seos.pmis.requirement.entity.OperationalScope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface OperationalScopeRepository
        extends JpaRepository<OperationalScope, Long>,
                JpaSpecificationExecutor<OperationalScope> {

    boolean existsByScopeCode(String scopeCode);

    @Query(
            value = "SELECT nextval('public.operational_scope_code_seq')",
            nativeQuery = true
    )
    long nextScopeCodeSequenceValue();
}
