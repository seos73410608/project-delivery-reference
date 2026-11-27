package com.seos.pmis.contract.mapper;

import com.seos.pmis.contract.dto.response.ContractResponse;
import com.seos.pmis.contract.entity.Contract;
import org.springframework.stereotype.Component;

@Component
public class ContractMapper {

    public ContractResponse toResponse(Contract contract) {
        if (contract == null) {
            return null;
        }

        return ContractResponse.builder()
                .id(contract.getId())
                .contractCode(contract.getContractCode())
                .contractName(contract.getContractName())
                .contractType(contract.getContractType())
                .status(contract.getStatus())
                .signedDate(contract.getSignedDate())
                .startDate(contract.getStartDate())
                .endDate(contract.getEndDate())
                .currencyCode(contract.getCurrencyCode())
                .contractAmount(contract.getContractAmount())
                .description(contract.getDescription())
                .createdAt(contract.getCreatedAt())
                .updatedAt(contract.getUpdatedAt())
                .build();
    }
}