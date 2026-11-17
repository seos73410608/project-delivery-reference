
package com.seos.cmdb.ci.entity;

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
@Table(name = "ci_attribute_definitions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CiAttributeDefinition extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private CiAttributeDefinition parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ci_type_id")
    private CiType ciType;

    @Column(name = "attribute_key", nullable = false, length = 100)
    private String attributeKey;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(name = "node_type", nullable = false, length = 20)
    private AttributeNodeType nodeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "data_type", length = 20)
    private AttributeDataType dataType;

    @Column(name = "reference_type", length = 50)
    private String referenceType;

    @Column(name = "display_format", length = 100)
    private String displayFormat;

    @Builder.Default
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    @Builder.Default
    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(columnDefinition = "TEXT")
    private String description;
}
