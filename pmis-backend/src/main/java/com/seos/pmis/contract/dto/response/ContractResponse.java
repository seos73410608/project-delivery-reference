package com.seos.pmis.contract.dto.response;

import com.seos.pmis.contract.entity.ContractStatus;
import com.seos.pmis.contract.entity.ContractType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class ContractResponse {

    private Long id;

    private String contractCode;

    private String contractName;

    private ContractType contractType;

    private ContractStatus status;

    private LocalDate signedDate;

    private LocalDate startDate;

    private LocalDate endDate;

    private String currencyCode;

    private BigDecimal contractAmount;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}