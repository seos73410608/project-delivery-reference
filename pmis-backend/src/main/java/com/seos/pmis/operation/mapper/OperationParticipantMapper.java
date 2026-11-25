package com.seos.pmis.operation.mapper;

import com.seos.pmis.company.entity.Company;
import com.seos.pmis.company.entity.Organization;
import com.seos.pmis.operation.dto.response.OperationParticipantResponse;
import com.seos.pmis.operation.entity.OperationParticipant;
import org.springframework.stereotype.Component;

@Component
public class OperationParticipantMapper {

    public OperationParticipantResponse toResponse(
            OperationParticipant participant
    ) {
        Company company = participant.getCompany();
        Organization organization = participant.getOrganization();

        return OperationParticipantResponse.builder()
                .id(participant.getId())
                .role(participant.getParticipantRole())
                .primary(participant.isPrimary())
                .company(toCompanySummary(company))
                .organization(toOrganizationSummary(organization))
                .build();
    }

    private OperationParticipantResponse.CompanySummary toCompanySummary(
            Company company
    ) {
        if (company == null) {
            return null;
        }

        return OperationParticipantResponse.CompanySummary.builder()
                .id(company.getId())
                .companyCode(company.getCompanyCode())
                .companyName(company.getCompanyName())
                .build();
    }

    private OperationParticipantResponse.OrganizationSummary toOrganizationSummary(
            Organization organization
    ) {
        if (organization == null) {
            return null;
        }

        return OperationParticipantResponse.OrganizationSummary.builder()
                .id(organization.getId())
                .organizationCode(organization.getOrganizationCode())
                .organizationName(organization.getOrganizationName())
                .build();
    }
}