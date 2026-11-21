package com.seos.pmis.contract.entity;

import com.seos.common.entity.BaseEntity;
import com.seos.pmis.company.entity.Company;
import com.seos.pmis.company.entity.Organization;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Entity
@Builder
@Table(name = "contract_parties")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ContractParty extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @Enumerated(EnumType.STRING)
    @Column(name = "party_role", nullable = false, length = 30)
    private ContractPartyRole partyRole;

    @Column(name = "is_primary", nullable = false)
    @Builder.Default
    private boolean primary = false;

    @Column(name = "effective_from")
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Column(length = 500)
    private String description;

    public void close(LocalDate effectiveTo) {
        if (effectiveTo == null) {
            throw new IllegalArgumentException(
                "계약 당사자 종료일은 필수입니다."
            );
        }

        if (effectiveFrom == null || effectiveTo.isBefore(effectiveFrom)) {
            throw new IllegalArgumentException(
                "종료일은 시작일 이후여야 합니다."
            );
        }

        this.effectiveTo = effectiveTo;
    }

    public void changePrimary(boolean primary) {
        this.primary = primary;
    }
}