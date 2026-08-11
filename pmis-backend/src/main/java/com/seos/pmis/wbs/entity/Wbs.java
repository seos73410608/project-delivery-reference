package com.seos.pmis.wbs.entity;

import com.seos.pmis.common.entity.BaseEntity;
import com.seos.pmis.project.entity.Project;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Builder
@Table(
    name = "wbs",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_wbs_project_code",
            columnNames = {"project_id", "wbs_code"}
        )
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Wbs extends BaseEntity {

    /**
     * WBS PK
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 소속 프로젝트
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    /**
     * 상위 WBS
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Wbs parent;

    /**
     * WBS 코드
     * 예) 1, 1.1, 1.1.1
     */
    @Column(name = "wbs_code", nullable = false, length = 50)
    private String wbsCode;

    /**
     * WBS 명
     */
    @Column(name = "wbs_name", nullable = false, length = 200)
    private String wbsName;

    /**
     * WBS Level
     */
    @Column(nullable = false)
    private Integer level;

    /**
     * 동일 부모 내 표시 순서
     */
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    /**
     * WBS 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private WbsStatus status;

    /**
     * 설명
     */
    @Lob
    private String description;

    /**
     * WBS 수정
     */
    public void update(
            String wbsName,
            String description,
            WbsStatus status,
            Integer sortOrder
    ) {
        this.wbsName = wbsName;
        this.description = description;
        this.status = status;
        this.sortOrder = sortOrder;
    }

    /**
     * WBS 상태 변경
     */
    public void changeStatus(WbsStatus status) {
        this.status = status;
    }

    /**
     * WBS 표시 순서 변경
     */
    public void changeSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}