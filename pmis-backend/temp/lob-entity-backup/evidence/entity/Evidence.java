
package com.seos.pmis.evidence.entity;

import com.seos.common.entity.BaseEntity;
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
 * 어떤 증적이 필요한가?
 *
 * Evidence:
 * 실제 어떤 증적이 등록되었는가?
 *
 * Verification:
 * 등록된 증적이 적합한가?
 *
 * Evidence는 최신 EvidenceVersion의 대표 객체로 관리한다.
 * 각 EvidenceVersion은 제출 시점의 독립적인 스냅샷을 보존한다.
 *
 * Automation Runtime:
 * Runtime에서 생성된 결과 파일을 외부 식별자로 연결한다.
 * 기존 수동 등록 증적과의 호환성을 위해
 * Runtime 연동 필드는 nullable로 관리한다.
 */
@Getter
@Entity
@Builder
@Table(name = "evidence", uniqueConstraints = {
                @UniqueConstraint(name = "uk_evidence_project_key", columnNames = { "project_id", "evidence_key" })
})
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
        @JoinColumn(name = "project_id", nullable = false)
        private Project project;

        /**
         * Evidence Requirement
         *
         * 어떤 증적 요구사항에 대한 Evidence인지 표현한다.
         */
        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "requirement_id", nullable = false)
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
        @Column(name = "evidence_key", nullable = false, length = 30)
        private String evidenceKey;

        /**
         * Evidence 유형
         */
        @Enumerated(EnumType.STRING)
        @Column(name = "evidence_type", nullable = false, length = 30)
        private EvidenceType evidenceType;

        /**
         * Evidence 제목
         */
        @Column(nullable = false, length = 200)
        private String title;

        /**
         * Evidence 설명
         */
        @Lob
        private String description;

        /**
         * 파일명
         */
        @Column(name = "file_name", length = 255)
        private String fileName;

        /**
         * 파일 경로
         */
        @Column(name = "file_path", length = 1000)
        private String filePath;

        /**
         * Automation Runtime 파일 ID
         */
        @Column(name = "source_file_id", length = 64)
        private String sourceFileId;

        /**
         * Automation Runtime 실행 UUID
         */
        @Column(name = "execution_uuid", length = 64)
        private String executionUuid;

        /**
         * Automation Runtime Job ID
         */
        @Column(name = "source_job_id", length = 64)
        private String sourceJobId;

        /**
         * 증적 최초 제출자
         *
         * 최초 Evidence 등록 정보를 유지한다.
         * 새로운 EvidenceVersion 생성 시 변경하지 않는다.
         */
        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "submitted_by", nullable = false)
        private User submittedBy;

        /**
         * 증적 최초 제출 일시
         *
         * 최초 Evidence 등록 일시를 유지한다.
         */
        @Column(name = "submitted_at", nullable = false)
        private LocalDateTime submittedAt;

        /**
         * 최신 버전의 대표 검증 상태
         */
        @Enumerated(EnumType.STRING)
        @Column(name = "verification_status", nullable = false, length = 30)
        @Builder.Default
        private VerificationStatus verificationStatus = VerificationStatus.PENDING;

        /**
         * 최신 버전의 대표 검증 담당자
         */
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "verified_by")
        private User verifiedBy;

        /**
         * 최신 버전의 대표 검증 일시
         */
        @Column(name = "verified_at")
        private LocalDateTime verifiedAt;

        /**
         * 최신 버전의 대표 검증 의견 및 반려 사유
         */
        @Lob
        @Column(name = "verification_remark")
        private String verificationRemark;

        /**
         * Evidence 관리 정보 수정
         *
         * 일반 수정에서는 WBS 연결 정보만 변경한다.
         * 제출물 내용 변경은 EvidenceVersion 생성으로 처리한다.
         *
         * @param wbs 연결할 WBS
         */
        public void update(Wbs wbs) {
                this.wbs = wbs;
        }

        /**
         * 최신 EvidenceVersion 정보 동기화
         *
         * 신규 버전 생성 시 해당 버전의 제출물 정보를
         * Evidence 대표 정보에 반영한다.
         *
         * 신규 버전은 아직 검증되지 않은 상태이므로
         * Evidence의 대표 검증 정보도 PENDING으로 초기화한다.
         *
         * 최초 Evidence 등록자와 등록 일시는 변경하지 않는다.
         *
         * @param version 최신 EvidenceVersion
         */
        public void synchronizeLatestVersion(EvidenceVersion version) {
                if (version == null) {
                        throw new IllegalArgumentException(
                                        "동기화할 EvidenceVersion은 필수입니다.");
                }

                this.evidenceType = version.getEvidenceType();
                this.title = version.getTitle();
                this.description = version.getDescription();
                this.fileName = version.getFileName();
                this.filePath = version.getFilePath();

                this.sourceFileId = version.getSourceFileId();
                this.executionUuid = version.getExecutionUuid();
                this.sourceJobId = version.getSourceJobId();

                this.verificationStatus = VerificationStatus.PENDING;
                this.verifiedBy = null;
                this.verifiedAt = null;
                this.verificationRemark = null;
        }

        /**
         * 이전 활성 EvidenceVersion 정보 복원
         *
         * 최신 버전이 논리 삭제된 경우 이전 활성 버전의
         * 제출물 정보와 검증 정보를 Evidence 대표 객체에 복원한다.
         *
         * 검증 상태는 PENDING으로 초기화하지 않고
         * 기존 버전의 검증 정보를 그대로 복원한다.
         *
         * Evidence의 최초 제출자와 최초 제출 일시는 유지한다.
         * 버전별 제출자와 제출 일시는 EvidenceVersion에 보존한다.
         *
         * 삭제된 버전은 복원 대상으로 사용할 수 없다.
         *
         * @param version 복원할 이전 활성 EvidenceVersion
         */
        public void restoreFromVersion(EvidenceVersion version) {
                if (version == null) {
                        throw new IllegalArgumentException(
                                        "복원할 EvidenceVersion은 필수입니다.");
                }

                if (version.isDeleted()) {
                        throw new IllegalArgumentException(
                                        "삭제된 EvidenceVersion은 복원할 수 없습니다.");
                }

                this.evidenceType = version.getEvidenceType();
                this.title = version.getTitle();
                this.description = version.getDescription();
                this.fileName = version.getFileName();
                this.filePath = version.getFilePath();

                this.sourceFileId = version.getSourceFileId();
                this.executionUuid = version.getExecutionUuid();
                this.sourceJobId = version.getSourceJobId();

                this.verificationStatus = version.getVerificationStatus();
                this.verifiedBy = version.getVerifiedBy();
                this.verifiedAt = version.getVerifiedAt();
                this.verificationRemark = version.getVerificationRemark();
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
         * @param sourceFileId  Runtime 파일 ID
         * @param executionUuid Runtime 실행 UUID
         * @param sourceJobId   Runtime Job ID
         */
        public void linkRuntimeFile(
                        String sourceFileId,
                        String executionUuid,
                        String sourceJobId) {
                this.sourceFileId = sourceFileId;
                this.executionUuid = executionUuid;
                this.sourceJobId = sourceJobId;
        }

        /**
         * Evidence 대표 검증 정보 변경
         *
         * 최신 EvidenceVersion의 검증 결과를
         * Evidence 대표 정보에 반영할 때 사용한다.
         *
         * 허용되는 검증 상태와 반려 사유 등은
         * Service 계층에서 검증한다.
         *
         * @param verificationStatus 검증 상태
         * @param verifiedBy         검증 담당자
         * @param verifiedAt         검증 일시
         * @param verificationRemark 검증 의견
         */
        public void verify(
                        VerificationStatus verificationStatus,
                        User verifiedBy,
                        LocalDateTime verifiedAt,
                        String verificationRemark) {
                this.verificationStatus = verificationStatus;
                this.verifiedBy = verifiedBy;
                this.verifiedAt = verifiedAt;
                this.verificationRemark = verificationRemark;
        }
}
