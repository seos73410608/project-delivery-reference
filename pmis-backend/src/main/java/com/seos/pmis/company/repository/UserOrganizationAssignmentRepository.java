package com.seos.pmis.company.repository;

import com.seos.pmis.company.entity.UserOrganizationAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserOrganizationAssignmentRepository
        extends JpaRepository<UserOrganizationAssignment, Long> {

    List<UserOrganizationAssignment> findByUserId(Long userId);

    List<UserOrganizationAssignment> findByOrganizationId(
            Long organizationId);

    List<UserOrganizationAssignment> findByUserIdAndEffectiveToIsNull(
            Long userId);

    List<UserOrganizationAssignment> findByUserIdAndPrimaryTrueAndEffectiveToIsNull(
            Long userId);

    boolean existsByUserIdAndOrganizationIdAndEffectiveToIsNull(
            Long userId,
            Long organizationId);

    boolean existsByUserIdAndPrimaryTrueAndEffectiveToIsNull(
            Long userId);
}