
package com.seos.pmis.projectoperation.entity;

import com.seos.common.entity.BaseEntity;
import com.seos.pmis.operation.entity.Operation;
import com.seos.pmis.project.entity.Project;
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
@Table(name = "project_operations")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectOperation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 연결된 프로젝트
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    /**
     * 연결된 운영 서비스
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "operation_id", nullable = false)
    private Operation operation;

    /**
     * 프로젝트와 운영 서비스의 관계 유형
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "relationship_type", nullable = false, length = 30)
    private ProjectOperationRelationshipType relationshipType;

    /**
     * 프로젝트와 운영 서비스 간 관계 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ProjectOperationStatus status = ProjectOperationStatus.PLANNED;

    /**
     * 관계 시작일
     */
    @Column(name = "effective_from")
    private LocalDate effectiveFrom;

    /**
     * 관계 종료일
     */
    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    /**
     * 관계 설명
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 관계 정보 수정
     */
    public void update(
            ProjectOperationRelationshipType relationshipType,
            LocalDate effectiveFrom,
            LocalDate effectiveTo,
            String description) {
        if (relationshipType == null) {
            throw new IllegalArgumentException(
                    "프로젝트 운영 관계 유형은 필수입니다.");
        }

        validatePeriod(effectiveFrom, effectiveTo);

        this.relationshipType = relationshipType;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
        this.description = description;
    }

    /**
     * 관계 상태 변경
     */
    public void changeStatus(ProjectOperationStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("프로젝트 운영 관계 상태는 필수입니다.");
        }

        this.status = status;
    }

    /**
     * 관계 기간 검증
     */
    private void validatePeriod(LocalDate effectiveFrom, LocalDate effectiveTo) {
        if (effectiveTo != null
                && (effectiveFrom == null || effectiveFrom.isAfter(effectiveTo))) {
            throw new IllegalArgumentException("관계 종료일은 시작일과 같거나 이후여야 합니다.");
        }
    }
}

