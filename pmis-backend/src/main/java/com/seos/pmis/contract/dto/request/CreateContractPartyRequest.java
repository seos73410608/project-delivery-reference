package com.seos.pmis.contract.dto.request;

import com.seos.pmis.contract.entity.ContractPartyRole;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class CreateContractPartyRequest {

    @NotNull(message = "계약 당사자 회사 ID는 필수입니다.")
    private Long companyId;

    private Long organizationId;

    @NotNull(message = "계약 당사자 역할은 필수입니다.")
    private ContractPartyRole partyRole;

    private boolean primary = false;

    @NotNull(message = "계약 당사자 시작일은 필수입니다.")
    private LocalDate effectiveFrom;

    @Size(max = 500, message = "설명은 500자 이하여야 합니다.")
    private String description;
}