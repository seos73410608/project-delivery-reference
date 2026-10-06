package com.seos.pmis.evidence.entity;

import com.seos.pmis.common.entity.BaseEntity;
import com.seos.pmis.evidence.enums.InspectionItemResult;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Inspection Item Entity
 *
 * 하나의 Inspection에서 개별 점검 대상을 표현한다.
 *
 * Inspection:
 *   전체 점검/검수
 *
 * InspectionItem:
 *   개별 요구사항에 대한 점검 결과
 *
 * 관계:
 *
 * Inspection
 *     └── InspectionItem
 *              ├── EvidenceRequirement
 *              └── Evidence
 *
 * 하나의 InspectionItem은 하나의
 * EvidenceRequirement를 대상으로 한다.
 */
@Getter
@Entity
@Builder
@Table(
        name = "inspection_items"
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class InspectionItem extends BaseEntity {

    /**
     * Inspection Item PK
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 소속 Inspection
     *
     * 하나의 InspectionItem은 반드시 하나의 Inspection에 속한다.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "inspection_id",
            nullable = false
    )
    private Inspection inspection;

    /**
     * 점검 대상 Evidence Requirement
     *
     * 어떤 증적 요구사항을 점검하는지 표현한다.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "requirement_id",
            nullable = false
    )
    private EvidenceRequirement requirement;

    /**
     * 실제 점검에 사용된 Evidence
     *
     * 아직 Evidence가 등록되지 않았거나
     * 특정 Evidence와 연결할 필요가 없는 경우를 고려하여
     * 선택적으로 관리한다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evidence_id")
    private Evidence evidence;

    /**
     * 점검 결과
     *
     * PENDING
     * PASSED
     * FAILED
     * NOT_APPLICABLE
     */
    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    @Builder.Default
    private InspectionItemResult result = InspectionItemResult.PENDING;

    /**
     * 점검 의견
     */
    @Lob
    @Column(name = "remark")
    private String remark;

    /**
     * 점검 항목 순서
     *
     * 하나의 Inspection 안에서
     * 항목 표시 순서를 관리한다.
     */
    @Column(
            name = "sort_order",
            nullable = false
    )
    @Builder.Default
    private Integer sortOrder = 0;

    /**
     * Inspection Item 수정
     *
     * inspection은 수정 대상에서 제외한다.
     *
     * Requirement 변경이나 Evidence 변경은
     * 별도의 변경 메서드를 사용한다.
     *
     * 결과(result)는 별도의 결과 변경 메서드를 사용한다.
     */
    public void update(
            String remark,
            Integer sortOrder
    ) {
        this.remark = remark;
        this.sortOrder = sortOrder;
    }

    /**
     * Evidence 연결
     */
    public void changeEvidence(Evidence evidence) {
        this.evidence = evidence;
    }

    /**
     * 점검 결과 변경
     */
    public void changeResult(
            InspectionItemResult result,
            String remark
    ) {
        this.result = result;
        this.remark = remark;
    }

    /**
     * 점검 통과
     */
    public void pass(String remark) {
        this.result = InspectionItemResult.PASSED;
        this.remark = remark;
    }

    /**
     * 점검 실패
     */
    public void fail(String remark) {
        this.result = InspectionItemResult.FAILED;
        this.remark = remark;
    }

    /**
     * 점검 대상에서 제외
     */
    public void notApplicable(String remark) {
        this.result = InspectionItemResult.NOT_APPLICABLE;
        this.remark = remark;
    }

    /**
     * 점검 결과 초기화
     */
    public void reset() {
        this.result = InspectionItemResult.PENDING;
        this.remark = null;
    }
}