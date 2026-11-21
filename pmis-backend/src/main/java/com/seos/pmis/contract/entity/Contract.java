package com.seos.pmis.contract.entity;

import com.seos.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Entity
@Builder
@Table(name = "contracts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Contract extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contract_code", nullable = false, unique = true, length = 50)
    private String contractCode;

    @Column(name = "contract_name", nullable = false, length = 200)
    private String contractName;

    @Enumerated(EnumType.STRING)
    @Column(name = "contract_type", nullable = false, length = 30)
    private ContractType contractType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ContractStatus status = ContractStatus.DRAFT;

    @Column(name = "signed_date")
    private LocalDate signedDate;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "currency_code", nullable = false, length = 3)
    @Builder.Default
    private String currencyCode = "KRW";

    @Column(name = "contract_amount", precision = 19, scale = 4)
    private BigDecimal contractAmount;

    @Column(columnDefinition = "TEXT")
    private String description;

    public void update(
            String contractName,
            ContractType contractType,
            LocalDate signedDate,
            LocalDate startDate,
            LocalDate endDate,
            String currencyCode,
            BigDecimal contractAmount,
            String description
    ) {
        validateContractPeriod(startDate, endDate);
        validateContractAmount(contractAmount);
        validateCurrencyCode(currencyCode);

        this.contractName = contractName;
        this.contractType = contractType;
        this.signedDate = signedDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.currencyCode = currencyCode;
        this.contractAmount = contractAmount;
        this.description = description;
    }

    public void changeStatus(ContractStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("계약 상태는 필수입니다.");
        }
        this.status = status;
    }

    private static void validateContractPeriod(
            LocalDate startDate,
            LocalDate endDate
    ) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException(
                    "계약 시작일과 종료일은 필수입니다.");
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "계약 종료일은 시작일보다 빠를 수 없습니다.");
        }
    }

    private static void validateContractAmount(BigDecimal amount) {
        if (amount != null && amount.signum() < 0) {
            throw new IllegalArgumentException(
                "계약 금액은 음수일 수 없습니다."
            );
        }
    }

    private static void validateCurrencyCode(String currencyCode) {
        if (currencyCode == null
                || !currencyCode.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException(
                "통화 코드는 영문 대문자 3자리여야 합니다."
            );
        }
    }
}