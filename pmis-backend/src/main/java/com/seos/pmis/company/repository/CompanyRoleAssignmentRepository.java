package com.seos.pmis.company.repository;

import com.seos.pmis.company.entity.CompanyRoleAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyRoleAssignmentRepository
        extends JpaRepository<CompanyRoleAssignment, Long> {

    /**
     * 회사 역할 배정 이력 조회
     * 시작일 내림차순, ID 내림차순
     */
    List<CompanyRoleAssignment> findByCompanyIdOrderByEffectiveFromDescIdDesc(
            Long companyId
    );

    /**
     * 회사의 미종료 역할 배정 조회
     */
    List<CompanyRoleAssignment> findByCompanyIdAndEffectiveToIsNull(
            Long companyId
    );

    /**
     * 동일 회사의 동일 역할 미종료 배정 여부
     */
    boolean existsByCompanyIdAndCompanyRoleIdAndEffectiveToIsNull(
            Long companyId,
            Long companyRoleId
    );
}