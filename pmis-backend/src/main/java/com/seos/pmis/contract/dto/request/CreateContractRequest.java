package com.seos.pmis.contract.dto.request;

import com.seos.pmis.contract.entity.ContractType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class CreateContractRequest {

    /**
     * 계약 코드
     */
    @NotBlank(message = "계약 코드는 필수입니다.")
    @Size(max = 50, message = "계약 코드는 50자 이하입니다.")
    private String contractCode;

    /**
     * 계약명
     */
    @NotBlank(message = "계약명은 필수입니다.")
    @Size(max = 200, message = "계약명은 200자 이하입니다.")
    private String contractName;

    /**
     * 계약 유형
     */
    @NotNull(message = "계약 유형은 필수입니다.")
    private ContractType contractType;

    /**
     * 계약 서명일
     */
    private LocalDate signedDate;

    /**
     * 계약 시작일
     */
    @NotNull(message = "계약 시작일은 필수입니다.")
    private LocalDate startDate;

    /**
     * 계약 종료일
     */
    @NotNull(message = "계약 종료일은 필수입니다.")
    private LocalDate endDate;

    /**
     * 통화 코드 (ISO 4217)
     */
    @NotBlank(message = "통화 코드는 필수입니다.")
    @Pattern(
            regexp = "^[A-Z]{3}$",
            message = "통화 코드는 영문 대문자 3자리여야 합니다."
    )
    private String currencyCode = "KRW";

    /**
     * 계약 금액
     */
    @DecimalMin(
            value = "0.0",
            message = "계약 금액은 0 이상이어야 합니다."
    )
    private BigDecimal contractAmount;

    /**
     * 계약 설명
     */
    private String description;
}