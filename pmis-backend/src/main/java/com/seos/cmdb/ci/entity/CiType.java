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
@Table(
        name = "ci_types",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_ci_types_code",
                        columnNames = "code"
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CiType extends BaseEntity {

    /**
     * CI 유형 PK
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * CI 유형 코드
     * 예) HARDWARE, SERVER, DATABASE
     */
    @Column(nullable = false, length = 50)
    private String code;

    /**
     * CI 유형명
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 상위 CI 유형
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private CiType parent;

    /**
     * 유형 설명
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 실제 CI 할당 가능 여부
     *
     * false: 분류용 상위 유형
     * true: CI 등록 가능한 유형
     */
    @Builder.Default
    @Column(nullable = false)
    private Boolean assignable = false;

    /**
     * 사용 여부
     */
    @Builder.Default
    @Column(nullable = false)
    private Boolean enabled = true;

    /**
     * 정렬 순서
     */
    @Builder.Default
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    /**
     * CI 유형 정보 수정
     */
    public void update(
            String name,
            CiType parent,
            String description,
            Boolean assignable,
            Integer sortOrder
    ) {
        this.name = name;
        this.parent = parent;
        this.description = description;
        this.assignable = assignable;
        this.sortOrder = sortOrder;
    }

    /**
     * CI 유형 사용 중지
     */
    public void disable() {
        this.enabled = false;
    }

    /**
     * CI 유형 사용
     */
    public void enable() {
        this.enabled = true;
    }
}