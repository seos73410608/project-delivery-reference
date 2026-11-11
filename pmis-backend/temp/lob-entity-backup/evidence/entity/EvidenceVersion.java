package com.seos.pmis.evidence.entity;

import com.seos.common.entity.BaseEntity;
import com.seos.pmis.evidence.enums.EvidenceType;
import com.seos.pmis.evidence.enums.VerificationStatus;
import com.seos.pmis.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "evidence_version", uniqueConstraints = {
                @UniqueConstraint(name = "uk_evidence_version_number", columnNames = { "evidence_id",
                                "version_number" })
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EvidenceVersion extends BaseEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "evidence_id", nullable = false)
        private Evidence evidence;

        @Column(name = "version_number", nullable = false)
        private Integer versionNumber;

        @Enumerated(EnumType.STRING)
        @Column(name = "evidence_type", nullable = false, length = 30)
        private EvidenceType evidenceType;

        @Column(nullable = false, length = 200)
        private String title;

        @Lob
        private String description;

        @Column(name = "file_name", length = 255)
        private String fileName;

        @Column(name = "file_path", length = 1000)
        private String filePath;

        @Column(name = "source_file_id", length = 64)
        private String sourceFileId;

        @Column(name = "execution_uuid", length = 64)
        private String executionUuid;

        @Column(name = "source_job_id", length = 64)
        private String sourceJobId;

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "submitted_by", nullable = false)
        private User submittedBy;

        @Column(name = "submitted_at", nullable = false)
        private LocalDateTime submittedAt;

        @Enumerated(EnumType.STRING)
        @Column(name = "verification_status", nullable = false, length = 30)
        private VerificationStatus verificationStatus;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "verified_by")
        private User verifiedBy;

        @Column(name = "verified_at")
        private LocalDateTime verifiedAt;

        @Lob
        @Column(name = "verification_remark")
        private String verificationRemark;

        // 논리 삭제 정보
        @Column(nullable = false)
        private boolean deleted;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "deleted_by")
        private User deletedBy;

        @Column(name = "deleted_at")
        private LocalDateTime deletedAt;

        @Column(name = "deletion_reason", length = 1000)
        private String deletionReason;

        @Builder
        private EvidenceVersion(
                        Evidence evidence,
                        Integer versionNumber,
                        EvidenceType evidenceType,
                        String title,
                        String description,
                        String fileName,
                        String filePath,
                        String sourceFileId,
                        String executionUuid,
                        String sourceJobId,
                        User submittedBy,
                        LocalDateTime submittedAt) {
                this.evidence = evidence;
                this.versionNumber = versionNumber;
                this.evidenceType = evidenceType;
                this.title = title;
                this.description = description;
                this.fileName = fileName;
                this.filePath = filePath;
                this.sourceFileId = sourceFileId;
                this.executionUuid = executionUuid;
                this.sourceJobId = sourceJobId;
                this.submittedBy = submittedBy;
                this.submittedAt = submittedAt;
                this.verificationStatus = VerificationStatus.PENDING;
                this.deleted = false;
        }

        public static EvidenceVersion createInitial(
                        Evidence evidence,
                        User submittedBy,
                        LocalDateTime submittedAt) {
                return fromEvidence(
                                evidence, 1, submittedBy, submittedAt);
        }

        public static EvidenceVersion createNewVersion(
                        Evidence evidence,
                        int versionNumber,
                        EvidenceType evidenceType,
                        String title,
                        String description,
                        String fileName,
                        String filePath,
                        String sourceFileId,
                        String executionUuid,
                        String sourceJobId,
                        User submittedBy,
                        LocalDateTime submittedAt) {
                return EvidenceVersion.builder()
                                .evidence(evidence)
                                .versionNumber(versionNumber)
                                .evidenceType(evidenceType)
                                .title(title)
                                .description(description)
                                .fileName(fileName)
                                .filePath(filePath)
                                .sourceFileId(sourceFileId)
                                .executionUuid(executionUuid)
                                .sourceJobId(sourceJobId)
                                .submittedBy(submittedBy)
                                .submittedAt(submittedAt)
                                .build();
        }

        public static EvidenceVersion fromEvidence(
                        Evidence evidence,
                        int versionNumber,
                        User submittedBy,
                        LocalDateTime submittedAt) {
                return EvidenceVersion.builder()
                                .evidence(evidence)
                                .versionNumber(versionNumber)
                                .evidenceType(evidence.getEvidenceType())
                                .title(evidence.getTitle())
                                .description(evidence.getDescription())
                                .fileName(evidence.getFileName())
                                .filePath(evidence.getFilePath())
                                .sourceFileId(evidence.getSourceFileId())
                                .executionUuid(evidence.getExecutionUuid())
                                .sourceJobId(evidence.getSourceJobId())
                                .submittedBy(submittedBy)
                                .submittedAt(submittedAt)
                                .build();
        }

        public void verify(
                        VerificationStatus status,
                        User verifiedBy,
                        LocalDateTime verifiedAt,
                        String remark) {
                if (this.deleted) {
                        throw new IllegalStateException(
                                        "삭제된 증적 버전은 검증할 수 없습니다.");
                }

                this.verificationStatus = status;
                this.verifiedBy = verifiedBy;
                this.verifiedAt = verifiedAt;
                this.verificationRemark = remark;
        }

    /**
     * 증적 버전 논리 삭제
     *
     * 제출 정보, 검증 정보 및 Runtime 파일 참조는 보존한다.
     */
    public void softDelete(
            User deletedBy,
            LocalDateTime deletedAt,
            String deletionReason
    ) {
        if (this.deleted) {
            throw new IllegalStateException(
                    "이미 삭제된 증적 버전입니다."
            );
        }

        if (deletedBy == null) {
            throw new IllegalArgumentException(
                    "삭제자는 필수입니다."
            );
        }

        if (deletedAt == null) {
            throw new IllegalArgumentException(
                    "삭제 일시는 필수입니다."
            );
        }

        if (deletionReason == null || deletionReason.isBlank()) {
            throw new IllegalArgumentException(
                    "삭제 사유는 필수입니다."
            );
        }

        this.deleted = true;
        this.deletedBy = deletedBy;
        this.deletedAt = deletedAt;
        this.deletionReason = deletionReason.trim();
    }
}