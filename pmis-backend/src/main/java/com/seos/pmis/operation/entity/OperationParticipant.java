
package com.seos.pmis.operation.entity;

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
@Table(name = "operation_participants")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OperationParticipant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "operation_id", nullable = false)
    private Operation operation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @Enumerated(EnumType.STRING)
    @Column(name = "participant_role", nullable = false, length = 30)
    private OperationParticipantRole participantRole;

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
                    "참여 종료일은 필수입니다."
            );
        }

        if (this.effectiveTo != null) {
            throw new IllegalStateException(
                    "이미 종료된 참여 정보입니다."
            );
        }

        if (this.effectiveFrom == null
                || effectiveTo.isBefore(this.effectiveFrom)) {
            throw new IllegalArgumentException(
                    "참여 종료일은 시작일이 존재해야 하며 시작일보다 빠를 수 없습니다."
            );
        }

        this.effectiveTo = effectiveTo;
    }

    public void changePrimary(boolean primary) {
        this.primary = primary;
    }
}
