package com.seos.pmis.requirement.entity;

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
        name = "operational_scopes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_operational_scopes_scope_code",
                        columnNames = "scope_code"
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OperationalScope extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "scope_code",
            nullable = false,
            length = 50,
            updatable = false
    )
    private String scopeCode;

    @Column(name = "scope_name", nullable = false, length = 200)
    private String scopeName;

    @Enumerated(EnumType.STRING)
    @Column(name = "scope_type", nullable = false, length = 30)
    private OperationalScopeType scopeType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private OperationalScopeStatus status = OperationalScopeStatus.PLANNED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_scope_id")
    private OperationalScope parentScope;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id")
    private User owner;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "effective_from")
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Version
    @Column(nullable = false)
    private Long version;

    public void update(
            String scopeName,
            OperationalScopeType scopeType,
            OperationalScope parentScope,
            User owner,
            String description,
            LocalDate effectiveFrom,
            LocalDate effectiveTo
    ) {
        this.scopeName = scopeName;
        this.scopeType = scopeType;
        this.parentScope = parentScope;
        this.owner = owner;
        this.description = description;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
    }

    public void changeStatus(OperationalScopeStatus status) {
        this.status = status;
    }
}
