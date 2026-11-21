
package com.seos.pmis.company.repository;

import com.seos.pmis.company.entity.CompanyRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRoleRepository extends JpaRepository<CompanyRole, Long> {

    Optional<CompanyRole> findByRoleCode(String roleCode);

    boolean existsByRoleCode(String roleCode);
}
