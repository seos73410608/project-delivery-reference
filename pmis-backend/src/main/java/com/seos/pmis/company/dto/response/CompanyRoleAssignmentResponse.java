package com.seos.pmis.company.dto.response;

import com.seos.pmis.company.entity.CompanyRoleAssignment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class CompanyRoleAssignmentResponse {

    private Long id;

    private Long companyId;
    private String companyCode;
    private String companyName;

    private Long companyRoleId;
    private String roleCode;
    private String roleName;

    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;

    private String description;

    public static CompanyRoleAssignmentResponse from(
            CompanyRoleAssignment assignment
    ) {

        return CompanyRoleAssignmentResponse.builder()
                .id(assignment.getId())
                .companyId(assignment.getCompany().getId())
                .companyCode(assignment.getCompany().getCompanyCode())
                .companyName(assignment.getCompany().getCompanyName())
                .companyRoleId(assignment.getCompanyRole().getId())
                .roleCode(assignment.getCompanyRole().getRoleCode())
                .roleName(assignment.getCompanyRole().getRoleName())
                .effectiveFrom(assignment.getEffectiveFrom())
                .effectiveTo(assignment.getEffectiveTo())
                .description(assignment.getDescription())
                .build();
    }
}