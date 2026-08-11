package com.seos.pmis.wbs.entity;

import com.seos.pmis.common.entity.BaseEntity;
import com.seos.pmis.project.entity.Project;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * WBS Entity
 *
 * 프로젝트의 Work Breakdown Structure를 표현한다.
 *
 * WBS는 자기 자신을 부모로 가질 수 있는
 * 계층형 Self-Referencing Entity이다.
 *
 * 예:
 *
 * 1
 * ├── 1.1
 * │   ├── 1.1.1
 * │   └── 1.1.2
 * └── 1.2
 */
@Getter
@Entity
@Builder
@Table(
        name = "wbs",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_wbs_project_code",
                        columnNames = {
                                "project_id",
                                "wbs_code"
                        }
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
     *
     * 하나의 WBS는 반드시 하나의 프로젝트에 속한다.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "project_id",
            nullable = false
    )
    private Project project;

    /**
     * 상위 WBS
     *
     * Self-Referencing 구조이다.
     *
     * 최상위 WBS인 경우 null이다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Wbs parent;

    /**
     * WBS 코드
     *
     * 예:
     * 1
     * 1.1
     * 1.1.1
     *
     * 프로젝트 내부에서 유일해야 한다.
     */
    @Column(
            name = "wbs_code",
            nullable = false,
            length = 50
    )
    private String wbsCode;

    /**
     * WBS 명
     */
    @Column(
            name = "wbs_name",
            nullable = false,
            length = 200
    )
    private String wbsName;

    /**
     * WBS Level
     *
     * 최상위 WBS는 1부터 시작한다.
     */
    @Column(nullable = false)
    private Integer level;

    /**
     * 동일 부모 내 표시 순서
     */
    @Column(
            name = "sort_order",
            nullable = false
    )
    private Integer sortOrder;

    /**
     * WBS 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private WbsStatus status;

    /**
     * WBS 설명
     */
    @Lob
    private String description;

    /**
     * WBS 기본 정보 수정
     *
     * 부모와 Level은 별도의 Domain 메서드에서 변경한다.
     *
     * @param wbsCode WBS 코드
     * @param wbsName WBS 명
     * @param description 설명
     * @param status 상태
     * @param sortOrder 표시 순서
     */
    public void update(
            String wbsCode,
            String wbsName,
            String description,
            WbsStatus status,
            Integer sortOrder
    ) {
        this.wbsCode = wbsCode;
        this.wbsName = wbsName;
        this.description = description;
        this.status = status;
        this.sortOrder = sortOrder;
    }

    /**
     * WBS 부모 변경
     *
     * 부모 변경에 대한 순환 참조 검증은
     * Service에서 수행한다.
     *
     * @param parent 새로운 부모 WBS
     */
    public void changeParent(Wbs parent) {
        this.parent = parent;
    }

    /**
     * WBS Level 변경
     *
     * 부모 변경에 따른 Level 재계산 시 사용한다.
     *
     * @param level 새로운 Level
     */
    public void changeLevel(Integer level) {
        this.level = level;
    }

    /**
     * WBS 상태 변경
     *
     * @param status 새로운 상태
     */
    public void changeStatus(WbsStatus status) {
        this.status = status;
    }

    /**
     * WBS 표시 순서 변경
     *
     * @param sortOrder 새로운 표시 순서
     */
    public void changeSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}