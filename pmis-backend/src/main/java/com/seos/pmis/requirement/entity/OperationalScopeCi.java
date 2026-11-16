
package com.seos.pmis.requirement.entity;

import com.seos.cmdb.ci.entity.ConfigurationItem;
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
@Table(
        name = "operational_scope_cis",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_operational_scope_cis_scope_ci",
                        columnNames = {
                                "operational_scope_id",
                                "ci_id"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_operational_scope_cis_scope_id",
                        columnList = "operational_scope_id"
                ),
                @Index(
                        name = "idx_operational_scope_cis_ci_id",
                        columnList = "ci_id"
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OperationalScopeCi extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "operational_scope_id", nullable = false)
    private OperationalScope operationalScope;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ci_id", nullable = false)
    private ConfigurationItem configurationItem;

    public static OperationalScopeCi create(
            OperationalScope operationalScope,
            ConfigurationItem configurationItem
    ) {
        return OperationalScopeCi.builder()
                .operationalScope(operationalScope)
                .configurationItem(configurationItem)
                .build();
    }
}
