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
@Table(name = "user_organization_assignments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserOrganizationAssignment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Enumerated(EnumType.STRING)
    @Column(name = "assignment_type", nullable = false, length = 30)
    private OrganizationAssignmentType assignmentType;

    @Column(name = "is_primary", nullable = false)
    @Builder.Default
    private boolean primary = false;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Column(length = 500)
    private String description;

    public void close(LocalDate effectiveTo) {
        if (effectiveTo == null || effectiveTo.isBefore(effectiveFrom)) {
            throw new IllegalArgumentException(
                    "조직 배정 종료일은 시작일보다 빠를 수 없습니다.");
        }

        this.effectiveTo = effectiveTo;
    }

    public void changePrimary(
            OrganizationAssignmentType assignmentType,
            boolean primary) {
        if (primary && assignmentType != OrganizationAssignmentType.PRIMARY) {
            throw new IllegalArgumentException(
                    "주 소속 배정 유형은 PRIMARY여야 합니다.");
        }

        this.assignmentType = assignmentType;
        this.primary = primary;
    }
}