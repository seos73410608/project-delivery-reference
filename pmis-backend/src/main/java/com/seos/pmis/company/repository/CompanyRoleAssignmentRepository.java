
package com.seos.pmis.company.repository;

import com.seos.pmis.company.entity.CompanyRoleAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyRoleAssignmentRepository
        extends JpaRepository<CompanyRoleAssignment, Long> {

    List<CompanyRoleAssignment> findByCompanyId(Long companyId);

    List<CompanyRoleAssignment> findByCompanyIdAndEffectiveToIsNull(
            Long companyId);

    boolean existsByCompanyIdAndCompanyRoleIdAndEffectiveToIsNull(
            Long companyId,
            Long companyRoleId);
}
