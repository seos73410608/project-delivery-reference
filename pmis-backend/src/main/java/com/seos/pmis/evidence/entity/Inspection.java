package com.seos.pmis.evidence.entity;

import com.seos.pmis.common.entity.BaseEntity;
import com.seos.pmis.evidence.enums.InspectionStatus;
import com.seos.pmis.project.entity.Project;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Inspection Entity
 *
 * 프로젝트 또는 프로젝트 단계에서 수행하는
 * 공식 점검/검수 정보를 표현한다.
 *
 * Inspection:
 *   전체 점검/검수
 *
 * InspectionItem:
 *   점검/검수 대상별 세부 결과
 *
 * Workflow:
 *
 * PLANNED
 *    ↓
 * IN_PROGRESS
 *    ↓
 * PASSED / FAILED
 *    ↓
 * CLOSED
 */
@Getter
@Entity
@Builder
@Table(
        name = "inspections"
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Inspection extends BaseEntity {

    /**
     * Inspection PK
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 소속 프로젝트
     *
     * 하나의 Inspection은 반드시 하나의 프로젝트에 속한다.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "project_id",
            nullable = false
    )
    private Project project;

    /**
     * 검사/점검 명칭
     */
    @Column(
            nullable = false,
            length = 200
    )
    private String title;

    /**
     * 검사/점검 설명
     */
    @Lob
    private String description;

    /**
     * 검사/점검 예정일
     */
    @Column(name = "inspection_date")
    private LocalDate inspectionDate;

    /**
     * 실제 검사/점검 시작 시간
     *
     * IN_PROGRESS 전환 시 기록할 수 있다.
     */
    @Column(name = "started_at")
    private LocalDateTime startedAt;

    /**
     * 실제 검사/점검 종료 시간
     *
     * PASSED / FAILED / CLOSED 등의 종료 처리 시
     * 기록할 수 있다.
     */
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /**
     * 검사/점검 상태
     *
     * PLANNED
     * IN_PROGRESS
     * PASSED
     * FAILED
     * CLOSED
     */
    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    @Builder.Default
    private InspectionStatus status = InspectionStatus.PLANNED;

    /**
     * 검사/점검 담당자
     *
     * V1에서는 별도의 User FK를 두지 않고
     * 문자열로 담당자 정보를 관리한다.
     *
     * 향후 User와 직접 연결할 필요가 생기면
     * User 관계로 확장할 수 있다.
     */
    @Column(
            name = "inspector_name",
            length = 100
    )
    private String inspectorName;

    /**
     * 검사/점검 결과 또는 종합 의견
     */
    @Lob
    @Column(name = "result_remark")
    private String resultRemark;

    /**
     * Inspection 정보 수정
     *
     * project와 status는 수정 대상에서 제외한다.
     * 상태 변경은 별도의 상태 변경 메서드를 사용한다.
     */
    public void update(
            String title,
            String description,
            LocalDate inspectionDate,
            String inspectorName,
            String resultRemark
    ) {
        this.title = title;
        this.description = description;
        this.inspectionDate = inspectionDate;
        this.inspectorName = inspectorName;
        this.resultRemark = resultRemark;
    }

    /**
     * 검사/점검 시작
     *
     * PLANNED → IN_PROGRESS
     */
    public void start(LocalDateTime startedAt) {
        this.status = InspectionStatus.IN_PROGRESS;
        this.startedAt = startedAt;
    }

    /**
     * 검사/점검 통과
     *
     * → PASSED
     */
    public void pass(
            LocalDateTime completedAt,
            String resultRemark
    ) {
        this.status = InspectionStatus.PASSED;
        this.completedAt = completedAt;
        this.resultRemark = resultRemark;
    }

    /**
     * 검사/점검 실패
     *
     * → FAILED
     */
    public void fail(
            LocalDateTime completedAt,
            String resultRemark
    ) {
        this.status = InspectionStatus.FAILED;
        this.completedAt = completedAt;
        this.resultRemark = resultRemark;
    }

    /**
     * 검사/점검 종료
     *
     * → CLOSED
     */
    public void close(LocalDateTime completedAt) {
        this.status = InspectionStatus.CLOSED;
        this.completedAt = completedAt;
    }

    /**
     * 상태 직접 변경
     *
     * 실제 상태 전이 검증은 Service에서 수행한다.
     */
    public void changeStatus(InspectionStatus status) {
        this.status = status;
    }
}