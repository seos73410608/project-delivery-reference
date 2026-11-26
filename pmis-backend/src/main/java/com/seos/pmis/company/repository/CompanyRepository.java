package com.seos.pmis.company.repository;

import com.seos.pmis.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CompanyRepository extends
        JpaRepository<Company, Long>,
        JpaSpecificationExecutor<Company> {

    Optional<Company> findByCompanyCode(String companyCode);

    boolean existsByCompanyCode(String companyCode);

    boolean existsByBusinessRegistrationNumber(
            String businessRegistrationNumber);
}