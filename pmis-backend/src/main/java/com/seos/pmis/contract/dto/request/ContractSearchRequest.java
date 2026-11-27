package com.seos.pmis.contract.dto.request;

import com.seos.pmis.contract.entity.ContractStatus;
import com.seos.pmis.contract.entity.ContractType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContractSearchRequest {

    /**
     * 계약 코드
     */
    private String contractCode;

    /**
     * 계약명
     */
    private String contractName;

    /**
     * 계약 유형
     */
    private ContractType contractType;

    /**
     * 계약 상태
     */
    private ContractStatus status;
}