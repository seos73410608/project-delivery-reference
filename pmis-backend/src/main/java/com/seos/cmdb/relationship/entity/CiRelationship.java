package com.seos.cmdb.relationship.entity;

import com.seos.cmdb.ci.entity.ConfigurationItem;
import com.seos.common.entity.BaseEntity;
import com.seos.pmis.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@Table(
        name = "ci_relationships",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_ci_relationships_source_target_type",
                        columnNames = {
                                "source_ci_id",
                                "target_ci_id",
                                "relationship_type_id"
                        }
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CiRelationship extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_ci_id", nullable = false)
    private ConfigurationItem sourceCi;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "target_ci_id", nullable = false)
    private ConfigurationItem targetCi;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "relationship_type_id", nullable = false)
    private CiRelationshipType relationshipType;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, length = 20)
    private RelationshipStatus status = RelationshipStatus.ACTIVE;

    private LocalDateTime deactivatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deactivated_by")
    private User deactivatedBy;

    @Column(length = 500)
    private String deactivationReason;

    public void updateDescription(String description) {
        this.description = description;
    }

    public void deactivate(User actor, String reason) {
        if (this.status == RelationshipStatus.INACTIVE) {
            return;
        }

        this.status = RelationshipStatus.INACTIVE;
        this.deactivatedAt = LocalDateTime.now();
        this.deactivatedBy = actor;
        this.deactivationReason = reason;
    }

    public void reactivate() {
        if (this.status == RelationshipStatus.ACTIVE) {
            return;
        }

        this.status = RelationshipStatus.ACTIVE;
        this.deactivatedAt = null;
        this.deactivatedBy = null;
        this.deactivationReason = null;
    }
}
