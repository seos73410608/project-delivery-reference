package com.seos.pmis.company.service;

import com.seos.common.exception.BusinessException;
import com.seos.common.exception.code.CommonErrorCode;
import com.seos.pmis.company.dto.request.CloseCompanyRoleAssignmentRequest;
import com.seos.pmis.company.dto.request.CreateCompanyRoleAssignmentRequest;
import com.seos.pmis.company.dto.response.CompanyRoleAssignmentResponse;
import com.seos.pmis.company.entity.Company;
import com.seos.pmis.company.entity.CompanyRole;
import com.seos.pmis.company.entity.CompanyRoleAssignment;
import com.seos.pmis.company.entity.CompanyStatus;
import com.seos.pmis.company.repository.CompanyRepository;
import com.seos.pmis.company.repository.CompanyRoleAssignmentRepository;
import com.seos.pmis.company.repository.CompanyRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyRoleAssignmentService {

    private final CompanyRepository companyRepository;
    private final CompanyRoleRepository companyRoleRepository;
    private final CompanyRoleAssignmentRepository companyRoleAssignmentRepository;

    /**
     * 회사 역할 배정 이력 조회
     */
    public List<CompanyRoleAssignmentResponse> getAssignments(
            Long companyId
    ) {

        findCompany(companyId);

        return companyRoleAssignmentRepository
                .findByCompanyIdOrderByEffectiveFromDescIdDesc(companyId)
                .stream()
                .map(CompanyRoleAssignmentResponse::from)
                .toList();
    }

    /**
     * 회사 역할 부여
     */
    @Transactional
    public CompanyRoleAssignmentResponse assign(
            Long companyId,
            CreateCompanyRoleAssignmentRequest request
    ) {

        Company company = findCompany(companyId);

        if (company.getStatus() != CompanyStatus.ACTIVE) {
            throw new BusinessException(CommonErrorCode.INVALID_REQUEST);
        }

        CompanyRole companyRole = companyRoleRepository
                .findByRoleCode(request.getRoleCode())
                .orElseThrow(() ->
                        new BusinessException(CommonErrorCode.RESOURCE_NOT_FOUND)
                );

        if (companyRoleAssignmentRepository
                .existsByCompanyIdAndCompanyRoleIdAndEffectiveToIsNull(
                        companyId,
                        companyRole.getId()
                )) {
            throw new BusinessException(CommonErrorCode.INVALID_REQUEST);
        }

        CompanyRoleAssignment assignment = CompanyRoleAssignment.builder()
                .company(company)
                .companyRole(companyRole)
                .effectiveFrom(request.getEffectiveFrom())
                .description(request.getDescription())
                .build();

        CompanyRoleAssignment savedAssignment =
                companyRoleAssignmentRepository.save(assignment);

        return CompanyRoleAssignmentResponse.from(savedAssignment);
    }

    /**
     * 회사 역할 종료
     */
    @Transactional
    public CompanyRoleAssignmentResponse close(
            Long companyId,
            Long assignmentId,
            CloseCompanyRoleAssignmentRequest request
    ) {

        findCompany(companyId);

        CompanyRoleAssignment assignment =
                companyRoleAssignmentRepository.findById(assignmentId)
                        .orElseThrow(() ->
                                new BusinessException(
                                        CommonErrorCode.RESOURCE_NOT_FOUND
                                )
                        );

        if (!assignment.getCompany().getId().equals(companyId)) {
            throw new BusinessException(CommonErrorCode.RESOURCE_NOT_FOUND);
        }

        if (assignment.getEffectiveTo() != null) {
            throw new BusinessException(CommonErrorCode.INVALID_REQUEST);
        }

        if (request.getEffectiveTo().isBefore(
                assignment.getEffectiveFrom()
        )) {
            throw new BusinessException(CommonErrorCode.INVALID_REQUEST);
        }

        assignment.close(request.getEffectiveTo());

        return CompanyRoleAssignmentResponse.from(assignment);
    }

    /**
     * 회사 조회 공통 메서드
     */
    private Company findCompany(Long companyId) {

        return companyRepository.findById(companyId)
                .orElseThrow(() ->
                        new BusinessException(CommonErrorCode.RESOURCE_NOT_FOUND)
                );
    }
}