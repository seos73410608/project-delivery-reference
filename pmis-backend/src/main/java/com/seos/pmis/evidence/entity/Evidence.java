package com.seos.pmis.evidence.entity;

import com.seos.pmis.common.entity.BaseEntity;
import com.seos.pmis.evidence.enums.EvidenceType;
import com.seos.pmis.evidence.enums.VerificationStatus;
import com.seos.pmis.project.entity.Project;
import com.seos.pmis.user.entity.User;
import com.seos.pmis.wbs.entity.Wbs;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Evidence Entity
 *
 * Evidence Requirement에 대해 실제 등록된
 * 증적/산출물을 표현한다.
 *
 * Requirement:
 *   어떤 증적이 필요한가?
 *
 * Evidence:
 *   실제 어떤 증적이 등록되었는가?
 *
 * Verification:
 *   등록된 증적이 적합한가?
 *
 * V1에서는 Verification을 별도 Entity로 분리하지 않고
 * Evidence Entity 내부의 검증 정보로 관리한다.
 */
@Getter
@Entity
@Builder
@Table(
        name = "evidence",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_evidence_project_key",
                        columnNames = {
                                "project_id",
                                "evidence_key"
                        }
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Evidence extends BaseEntity {

    /**
     * Evidence PK
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 소속 프로젝트
     *
     * 하나의 Evidence는 반드시 하나의 프로젝트에 속한다.
     *
     * EvidenceRequirement의 프로젝트와 반드시 동일해야 하며,
     * 이 정합성 검증은 Service에서 수행한다.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "project_id",
            nullable = false
    )
    private Project project;

    /**
     * Evidence Requirement
     *
     * 어떤 증적 요구사항에 대한 Evidence인지 표현한다.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "requirement_id",
            nullable = false
    )
    private EvidenceRequirement requirement;

    /**
     * 연결된 WBS
     *
     * Requirement와 동일하게 특정 WBS에 연결되지 않을 수 있으므로
     * 선택적으로 관리한다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wbs_id")
    private Wbs wbs;

    /**
     * Evidence Key
     *
     * 프로젝트 내부에서 유일하다.
     *
     * 예:
     * EVD-001
     * EVD-002
     */
    @Column(
            name = "evidence_key",
            nullable = false,
            length = 30
    )
    private String evidenceKey;

    /**
     * Evidence 유형
     */
    @Enumerated(EnumType.STRING)
    @Column(
            name = "evidence_type",
            nullable = false,
            length = 30
    )
    private EvidenceType evidenceType;

    /**
     * Evidence 제목
     */
    @Column(
            nullable = false,
            length = 200
    )
    private String title;

    /**
     * Evidence 설명
     */
    @Lob
    private String description;

    /**
     * 파일명
     *
     * V1에서는 실제 Binary Storage를 관리하지 않고
     * 파일명과 파일 경로만 관리한다.
     */
    @Column(
            name = "file_name",
            length = 255
    )
    private String fileName;

    /**
     * 파일 경로
     *
     * 향후 Local Storage / Object Storage / S3 / MinIO 등으로
     * 확장할 수 있도록 경로 정보만 관리한다.
     */
    @Column(
            name = "file_path",
            length = 1000
    )
    private String filePath;

    /**
     * 증적 제출자
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "submitted_by",
            nullable = false
    )
    private User submittedBy;

    /**
     * 증적 제출 일시
     */
    @Column(
            name = "submitted_at",
            nullable = false
    )
    private LocalDateTime submittedAt;

    /**
     * 검증 상태
     *
     * 기본값은 PENDING이다.
     *
     * Evidence가 등록되었다고 해서
     * 검증이 완료된 것은 아니다.
     */
    @Enumerated(EnumType.STRING)
    @Column(
            name = "verification_status",
            nullable = false,
            length = 30
    )
    @Builder.Default
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    /**
     * 검증 담당자
     *
     * 검증 전에는 null이다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by")
    private User verifiedBy;

    /**
     * 검증 일시
     *
     * APPROVED / REJECTED 처리 시 기록한다.
     */
    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    /**
     * 검증 의견
     *
     * APPROVED:
     * 검증 완료 의견
     *
     * REJECTED:
     * 반려 사유
     */
    @Lob
    @Column(name = "verification_remark")
    private String verificationRemark;

    /**
     * Evidence 수정
     *
     * 프로젝트, Requirement, Evidence Key,
     * Verification 정보는 수정 대상에서 제외한다.
     *
     * Verification 정보는 별도의 검증 처리 메서드에서 변경한다.
     */
    public void update(
            Wbs wbs,
            EvidenceType evidenceType,
            String title,
            String description,
            String fileName,
            String filePath
    ) {
        this.wbs = wbs;
        this.evidenceType = evidenceType;
        this.title = title;
        this.description = description;
        this.fileName = fileName;
        this.filePath = filePath;
    }

    /**
     * Evidence 검증 처리
     *
     * APPROVED 또는 REJECTED 상태로 변경한다.
     *
     * @param verificationStatus 검증 상태
     * @param verifiedBy 검증 담당자
     * @param verifiedAt 검증 일시
     * @param verificationRemark 검증 의견
     */
    public void verify(
            VerificationStatus verificationStatus,
            User verifiedBy,
            LocalDateTime verifiedAt,
            String verificationRemark
    ) {
        this.verificationStatus = verificationStatus;
        this.verifiedBy = verifiedBy;
        this.verifiedAt = verifiedAt;
        this.verificationRemark = verificationRemark;
    }
}