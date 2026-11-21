
package com.seos.pmis.operation.entity;

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
        name = "operations",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_operations_operation_code",
                        columnNames = "operation_code"
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Operation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "operation_code",
            nullable = false,
            length = 50,
            updatable = false
    )
    private String operationCode;

    @Column(name = "operation_name", nullable = false, length = 200)
    private String operationName;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false, length = 30)
    private OperationType operationType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private OperationStatus status = OperationStatus.PLANNED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_user_id")
    private User manager;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "effective_from")
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    public void update(
            String operationName,
            OperationType operationType,
            User manager,
            String description,
            LocalDate effectiveFrom,
            LocalDate effectiveTo
    ) {
        validatePeriod(effectiveFrom, effectiveTo);

        this.operationName = operationName;
        this.operationType = operationType;
        this.manager = manager;
        this.description = description;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
    }

    public void changeStatus(OperationStatus status) {
        if (status == null) {
            throw new IllegalArgumentException(
                    "운영 상태는 필수입니다."
            );
        }

        this.status = status;
    }

    private static void validatePeriod(
            LocalDate effectiveFrom,
            LocalDate effectiveTo
    ) {
        if (effectiveTo != null
                && (effectiveFrom == null
                || effectiveFrom.isAfter(effectiveTo))) {
            throw new IllegalArgumentException(
                    "운영 종료일은 시작일이 존재해야 하며 시작일보다 빠를 수 없습니다."
            );
        }
    }
}
