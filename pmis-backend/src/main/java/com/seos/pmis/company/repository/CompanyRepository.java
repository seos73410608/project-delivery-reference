
package com.seos.pmis.company.repository;

import com.seos.pmis.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByCompanyCode(String companyCode);

    boolean existsByCompanyCode(String companyCode);

    boolean existsByBusinessRegistrationNumber(
            String businessRegistrationNumber);
}
