package com.seos.pmis.company.entity;

import com.seos.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@Table(name = "companies")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Company extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_code", nullable = false, unique = true, length = 50)
    private String companyCode;

    @Column(name = "company_name", nullable = false, length = 200)
    private String companyName;

    @Column(name = "legal_name", length = 250)
    private String legalName;

    @Column(name = "business_registration_number", length = 30)
    private String businessRegistrationNumber;

    @Builder.Default
    @Column(name = "country_code", nullable = false, length = 3)
    private String countryCode = "KOR";

    @Column(length = 500)
    private String website;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CompanyStatus status = CompanyStatus.ACTIVE;

    @Column(columnDefinition = "TEXT")
    private String description;

    public void update(
            String companyName,
            String legalName,
            String businessRegistrationNumber,
            String countryCode,
            String website,
            String description) {
        this.companyName = companyName;
        this.legalName = legalName;
        this.businessRegistrationNumber = businessRegistrationNumber;
        this.countryCode = countryCode;
        this.website = website;
        this.description = description;
    }

    public void changeStatus(CompanyStatus status) {
        this.status = status;
    }
}