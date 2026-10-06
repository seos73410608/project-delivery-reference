package com.seos.pmis.evidence.entity;

import com.seos.pmis.common.entity.BaseEntity;
import com.seos.pmis.evidence.enums.RequirementStatus;
import com.seos.pmis.evidence.enums.RequirementType;
import com.seos.pmis.project.entity.Project;
import com.seos.pmis.wbs.entity.Wbs;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Evidence Requirement Entity
 *
 * 프로젝트 수행 과정에서 확보해야 하는
 * 증적/산출물 요구사항을 표현한다.
 *
 * Evidence Requirement와 실제 Evidence는 분리한다.
 *
 * Requirement:
 *   어떤 증적이 필요한가?
 *
 * Evidence:
 *   실제 어떤 증적이 제출되었는가?
 */
@Getter
@Entity
@Builder
@Table(
        name = "evidence_requirements",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_evidence_requirement_project_key",
                        columnNames = {
                                "project_id",
                                "requirement_key"
                        }
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EvidenceRequirement extends BaseEntity {

    /**
     * Evidence Requirement PK
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 소속 프로젝트
     *
     * 하나의 Requirement는 반드시 하나의 프로젝트에 속한다.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "project_id",
            nullable = false
    )
    private Project project;

    /**
     * 연결된 WBS
     *
     * Requirement가 특정 WBS에 연결되지 않을 수도 있으므로
     * 선택적으로 관리한다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wbs_id")
    private Wbs wbs;

    /**
     * Requirement Key
     *
     * 프로젝트 내부에서 유일하다.
     *
     * 예:
     * EVR-001
     * EVR-002
     */
    @Column(
            name = "requirement_key",
            nullable = false,
            length = 30
    )
    private String requirementKey;

    /**
     * Requirement 제목
     */
    @Column(
            nullable = false,
            length = 200
    )
    private String title;

    /**
     * Requirement 설명
     */
    @Lob
    private String description;

    /**
     * Requirement 유형
     */
    @Enumerated(EnumType.STRING)
    @Column(
            name = "requirement_type",
            nullable = false,
            length = 30
    )
    private RequirementType requirementType;

    /**
     * 필수 여부
     *
     * true:
     * 반드시 확보해야 하는 증적
     *
     * false:
     * 선택적으로 관리하는 증적
     */
    @Column(
            nullable = false
    )
    private Boolean required;

    /**
     * 증적 확보 예정일
     */
    @Column(name = "due_date")
    private LocalDate dueDate;

    /**
     * Requirement 상태
     *
     * 실제 Evidence 등록 여부를 기준으로
     * Service에서 관리한다.
     */
    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private RequirementStatus status;

    /**
     * 표시 순서
     */
    @Column(
            name = "sort_order",
            nullable = false
    )
    private Integer sortOrder;

    /**
     * Requirement 수정
     *
     * 프로젝트와 Requirement Key는 변경하지 않는다.
     */
    public void update(
            Wbs wbs,
            String title,
            String description,
            RequirementType requirementType,
            Boolean required,
            LocalDate dueDate,
            Integer sortOrder
    ) {
        this.wbs = wbs;
        this.title = title;
        this.description = description;
        this.requirementType = requirementType;
        this.required = required;
        this.dueDate = dueDate;
        this.sortOrder = sortOrder;
    }

    /**
     * Requirement 상태 변경
     *
     * 상태 변경 정책은 Service에서 검증한다.
     */
    public void changeStatus(RequirementStatus status) {
        this.status = status;
    }

    /**
     * WBS 변경
     */
    public void changeWbs(Wbs wbs) {
        this.wbs = wbs;
    }
}