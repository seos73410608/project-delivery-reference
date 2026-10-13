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
 *
 * Automation Runtime:
 *   Runtime에서 생성된 결과 파일을 외부 식별자로 연결한다.
 *   기존 수동 등록 증적과의 호환성을 위해
 *   Runtime 연동 필드는 nullable로 관리한다.
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
     * Requirement와 프로젝트의 정합성은 Service에서 검증한다.
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
     * 특정 WBS에 연결되지 않을 수 있으므로 선택적으로 관리한다.
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
     * 수동 등록 증적의 파일명을 저장한다.
     * Runtime 연동 파일은 Runtime 메타데이터에
     * 실제 파일명이 제공되는지 확인한 후 설정한다.
     */
    @Column(
            name = "file_name",
            length = 255
    )
    private String fileName;

    /**
     * 파일 경로
     *
     * 기존 수동 등록 증적의 호환성을 위해 유지한다.
     *
     * Runtime 연동 증적은 내부 파일 경로를 저장하거나
     * 외부에 노출하지 않고 Runtime 콘텐츠 API를 통해 접근한다.
     */
    @Column(
            name = "file_path",
            length = 1000
    )
    private String filePath;

    /**
     * Automation Runtime 파일 ID
     *
     * Runtime의 tb_output_file.file_id를 저장한다.
     * 수동 등록 증적에서는 null이다.
     */
    @Column(
            name = "source_file_id",
            length = 64
    )
    private String sourceFileId;

    /**
     * Automation Runtime 실행 UUID
     *
     * 해당 파일을 생성한 실행을 식별한다.
     * 수동 등록 증적에서는 null이다.
     */
    @Column(
            name = "execution_uuid",
            length = 64
    )
    private String executionUuid;

    /**
     * Automation Runtime Job ID
     *
     * 해당 파일을 생성한 자동화 Job을 식별한다.
     * 수동 등록 증적에서는 null이다.
     */
    @Column(
            name = "source_job_id",
            length = 64
    )
    private String sourceJobId;

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
     * 증적 등록 시 기본값은 PENDING이다.
     * 등록 자체가 검증 완료를 의미하지는 않는다.
     */
    @Enumerated(EnumType.STRING)
    @Column(
            name = "verification_status",
            nullable = false,
            length = 30
    )
    @Builder.Default
    private VerificationStatus verificationStatus =
            VerificationStatus.PENDING;

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
     * APPROVED 또는 REJECTED 처리 시 기록한다.
     */
    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    /**
     * 검증 의견 및 반려 사유
     */
    @Lob
    @Column(name = "verification_remark")
    private String verificationRemark;

    /**
     * Evidence 수정
     *
     * 프로젝트, Requirement, Evidence Key,
     * Runtime 연결 정보 및 Verification 정보는
     * 일반 수정 대상에서 제외한다.
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
     * Automation Runtime 파일 연결
     *
     * Runtime API에서 파일 메타데이터를 조회하고
     * Service 계층에서 유효성을 검증한 후 호출한다.
     *
     * 이 메서드 자체는 Runtime API를 호출하거나
     * 파일의 존재 여부를 검증하지 않는다.
     *
     * @param sourceFileId Runtime 파일 ID
     * @param executionUuid Runtime 실행 UUID
     * @param sourceJobId Runtime Job ID
     */
    public void linkRuntimeFile(
            String sourceFileId,
            String executionUuid,
            String sourceJobId
    ) {
        this.sourceFileId = sourceFileId;
        this.executionUuid = executionUuid;
        this.sourceJobId = sourceJobId;
    }

    /**
     * Evidence 검증 처리
     *
     * 허용되는 검증 상태와 반려 사유 등은
     * Service 계층에서 검증한다.
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