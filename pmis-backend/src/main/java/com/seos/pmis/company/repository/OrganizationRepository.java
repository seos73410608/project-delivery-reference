package com.seos.pmis.company.repository;

import com.seos.pmis.company.entity.Organization;
import com.seos.pmis.company.entity.OrganizationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository
        extends JpaRepository<Organization, Long> {

    Optional<Organization> findByCompanyIdAndOrganizationCode(
            Long companyId,
            String organizationCode
    );

    List<Organization> findByCompanyId(Long companyId);

    List<Organization> findByCompanyIdAndStatus(
            Long companyId,
            OrganizationStatus status
    );

    List<Organization> findByParentOrganizationId(Long parentOrganizationId);

    boolean existsByCompanyIdAndOrganizationCode(
            Long companyId,
            String organizationCode
    );
}