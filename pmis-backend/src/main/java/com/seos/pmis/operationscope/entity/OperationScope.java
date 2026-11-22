
package com.seos.pmis.operationscope.entity;

import com.seos.common.entity.BaseEntity;
import com.seos.pmis.operation.entity.Operation;
import com.seos.pmis.requirement.entity.OperationalScope;
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
@Table(name = "operation_scopes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OperationScope extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "operation_id", nullable = false)
    private Operation operation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "operational_scope_id", nullable = false)
    private OperationalScope operationalScope;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private OperationScopeStatus status = OperationScopeStatus.PLANNED;

    @Column(name = "effective_from")
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Column(columnDefinition = "TEXT")
    private String description;

    public void update(
            LocalDate effectiveFrom,
            LocalDate effectiveTo,
            String description) {
        validatePeriod(effectiveFrom, effectiveTo);

        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
        this.description = description;
    }

    public void changeStatus(OperationScopeStatus status) {
        if (status == null) {
            throw new IllegalArgumentException(
                    "운영 범위 관계 상태는 필수입니다.");
        }

        boolean allowed = switch (this.status) {
            case PLANNED ->
                status == OperationScopeStatus.ACTIVE
                        || status == OperationScopeStatus.CANCELLED;
            case ACTIVE ->
                status == OperationScopeStatus.COMPLETED
                        || status == OperationScopeStatus.CANCELLED;
            case COMPLETED, CANCELLED -> false;
        };

        if (!allowed) {
            throw new IllegalArgumentException(
                    "현재 운영 범위 관계 상태에서는 해당 상태로 변경할 수 없습니다.");
        }

        this.status = status;
    }

    private static void validatePeriod(
            LocalDate effectiveFrom,
            LocalDate effectiveTo) {
        if (effectiveTo != null
                && (effectiveFrom == null
                        || effectiveFrom.isAfter(effectiveTo))) {
            throw new IllegalArgumentException(
                    "운영 범위 관계 종료일은 시작일이 존재해야 하며 시작일보다 빠를 수 없습니다.");
        }
    }
}
