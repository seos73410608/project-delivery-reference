package com.seos.pmis.contract.dto.response;

import com.seos.pmis.contract.entity.ContractPartyRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class ContractPartyResponse {

    private Long id;

    private Long contractId;
    private String contractCode;
    private String contractName;

    private Long companyId;
    private String companyCode;
    private String companyName;

    private Long organizationId;
    private String organizationCode;
    private String organizationName;

    private ContractPartyRole partyRole;

    private boolean primary;

    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;

    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}