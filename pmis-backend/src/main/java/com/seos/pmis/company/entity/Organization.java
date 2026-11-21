package com.seos.pmis.company.entity;

import com.seos.common.entity.BaseEntity;
import com.seos.pmis.user.entity.User;
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
@Table(
    name = "organizations",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_organizations_company_code",
            columnNames = {"company_id", "organization_code"}
        ),
        @UniqueConstraint(
            name = "uk_organizations_company_id_id",
            columnNames = { "company_id", "id" })
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Organization extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_organization_id")
    private Organization parentOrganization;

    @Column(name = "organization_code", nullable = false, length = 50)
    private String organizationCode;

    @Column(name = "organization_name", nullable = false, length = 200)
    private String organizationName;

    @Enumerated(EnumType.STRING)
    @Column(name = "organization_type", nullable = false, length = 30)
    private OrganizationType organizationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_user_id")
    private User manager;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private OrganizationStatus status = OrganizationStatus.ACTIVE;

    @Column(name = "effective_from")
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Column(columnDefinition = "TEXT")
    private String description;

    public void update(
            String organizationName,
            OrganizationType organizationType,
            Organization parentOrganization,
            User manager,
            LocalDate effectiveFrom,
            LocalDate effectiveTo,
            String description
    ) {
        validatePeriod(effectiveFrom, effectiveTo);

        this.organizationName = organizationName;
        this.organizationType = organizationType;
        this.parentOrganization = parentOrganization;
        this.manager = manager;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
        this.description = description;
    }

    public void changeStatus(OrganizationStatus status) {
        this.status = status;
    }

    private static void validatePeriod(
            LocalDate effectiveFrom,
            LocalDate effectiveTo
    ) {
        if (effectiveFrom != null
                && effectiveTo != null
                && effectiveFrom.isAfter(effectiveTo)) {
            throw new IllegalArgumentException(
                "조직 종료일은 시작일보다 빠를 수 없습니다."
            );
        }
    }
}