package com.seos.pmis.company.mapper;

import com.seos.pmis.company.dto.request.CreateOrganizationRequest;
import com.seos.pmis.company.dto.response.OrganizationResponse;
import com.seos.pmis.company.entity.Company;
import com.seos.pmis.company.entity.Organization;
import com.seos.pmis.user.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrganizationMapper {

    /**
     * CreateOrganizationRequest → Organization Entity
     */
    public Organization toEntity(
            CreateOrganizationRequest request,
            Company company,
            Organization parentOrganization,
            User manager
    ) {

        return Organization.builder()
                .company(company)
                .parentOrganization(parentOrganization)
                .organizationCode(request.getOrganizationCode())
                .organizationName(request.getOrganizationName())
                .organizationType(request.getOrganizationType())
                .manager(manager)
                .effectiveFrom(request.getEffectiveFrom())
                .effectiveTo(request.getEffectiveTo())
                .description(request.getDescription())
                .build();
    }

    /**
     * Organization Entity → OrganizationResponse
     */
    public OrganizationResponse toResponse(Organization organization) {

        Company company = organization.getCompany();
        Organization parentOrganization = organization.getParentOrganization();
        User manager = organization.getManager();

        return OrganizationResponse.builder()
                .id(organization.getId())
                .companyId(company.getId())
                .companyCode(company.getCompanyCode())
                .companyName(company.getCompanyName())
                .organizationCode(organization.getOrganizationCode())
                .organizationName(organization.getOrganizationName())
                .organizationType(organization.getOrganizationType())
                .parentOrganizationId(
                        parentOrganization != null
                                ? parentOrganization.getId()
                                : null
                )
                .parentOrganizationName(
                        parentOrganization != null
                                ? parentOrganization.getOrganizationName()
                                : null
                )
                .managerUserId(manager != null ? manager.getId() : null)
                .managerName(manager != null ? manager.getName() : null)
                .status(organization.getStatus())
                .effectiveFrom(organization.getEffectiveFrom())
                .effectiveTo(organization.getEffectiveTo())
                .description(organization.getDescription())
                .createdAt(organization.getCreatedAt())
                .updatedAt(organization.getUpdatedAt())
                .build();
    }
}