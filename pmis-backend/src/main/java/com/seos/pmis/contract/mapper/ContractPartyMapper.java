package com.seos.pmis.contract.mapper;

import com.seos.pmis.contract.dto.response.ContractPartyResponse;
import com.seos.pmis.contract.entity.ContractParty;
import org.springframework.stereotype.Component;

@Component
public class ContractPartyMapper {

    public ContractPartyResponse toResponse(ContractParty party) {
        return ContractPartyResponse.builder()
                .id(party.getId())
                .contractId(party.getContract().getId())
                .contractCode(party.getContract().getContractCode())
                .contractName(party.getContract().getContractName())
                .companyId(party.getCompany().getId())
                .companyCode(party.getCompany().getCompanyCode())
                .companyName(party.getCompany().getCompanyName())
                .organizationId(
                        party.getOrganization() != null
                                ? party.getOrganization().getId()
                                : null
                )
                .organizationCode(
                        party.getOrganization() != null
                                ? party.getOrganization().getOrganizationCode()
                                : null
                )
                .organizationName(
                        party.getOrganization() != null
                                ? party.getOrganization().getOrganizationName()
                                : null
                )
                .partyRole(party.getPartyRole())
                .primary(party.isPrimary())
                .effectiveFrom(party.getEffectiveFrom())
                .effectiveTo(party.getEffectiveTo())
                .description(party.getDescription())
                .createdAt(party.getCreatedAt())
                .updatedAt(party.getUpdatedAt())
                .build();
    }
}