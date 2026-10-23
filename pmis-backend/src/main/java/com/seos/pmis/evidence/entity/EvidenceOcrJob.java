package com.seos.pmis.evidence.entity;

import com.seos.pmis.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "evidence_ocr_job",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_evidence_ocr_job_pmis_request_id",
                        columnNames = "pmis_request_id"
                ),
                @UniqueConstraint(
                        name = "uk_evidence_ocr_job_ocr_request_id",
                        columnNames = "ocr_request_id"
                )
        },
        indexes = {
                @Index(
                        name = "idx_evidence_ocr_job_evidence_id",
                        columnList = "evidence_id"
                ),
                @Index(
                        name = "idx_evidence_ocr_job_status",
                        columnList = "status"
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EvidenceOcrJob extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * OCR 작업의 내부 상태.
     *
     * Runtime에서 반환하는 상태는 Service 계층에서
     * 이 상태값으로 변환한다.
     */
    public enum OcrStatus {
        PENDING,
        RUNNING,
        SUCCEEDED,
        FAILED
    }

    /**
     * OCR 대상 증적.
     * 하나의 증적에 여러 OCR 작업을 연결할 수 있다.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "evidence_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_evidence_ocr_job_evidence"
            )
    )
    private Evidence evidence;

    /**
     * PMIS에서 생성하는 고유 요청 ID.
     * Runtime 재요청 시 멱등성 확인에 사용한다.
     */
    @Column(
            name = "pmis_request_id",
            nullable = false,
            length = 100
    )
    private String pmisRequestId;

    /**
     * Runtime이 반환한 OCR 작업 ID.
     * Runtime 접수 전에는 null일 수 있다.
     */
    @Column(name = "ocr_request_id", length = 100)
    private String ocrRequestId;

    /**
     * OCR 처리 대상 이미지의 Runtime 업로드 ID.
     *
     * Evidence.sourceFileId와는 다른 식별자다.
     */
    @Column(
            name = "input_file_id",
            nullable = false,
            length = 100
    )
    private String inputFileId;

    /**
     * PMIS에서 관리하는 OCR 작업 상태.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OcrStatus status = OcrStatus.PENDING;

    /**
     * OCR 인식 결과 텍스트.
     */
    @Lob
    @Column(name = "recognized_text")
    private String recognizedText;

    /**
     * OCR 엔진 정보.
     */
    @Column(name = "engine_name", length = 100)
    private String engineName;

    @Column(name = "engine_version", length = 100)
    private String engineVersion;

    /**
     * Runtime에서 반환한 경고 목록.
     * JSON 문자열로 저장하며, API 응답 DTO 변환 시 해석한다.
     */
    @Lob
    @Column(name = "warnings")
    private String warnings;

    /**
     * OCR 실패 원인 또는 Runtime 오류 정보.
     * JSON 문자열 또는 오류 메시지를 저장할 수 있다.
     */
    @Lob
    @Column(name = "error_detail")
    private String errorDetail;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    /**
     * OCR 작업 이력을 생성한다.
     */
    public static EvidenceOcrJob create(
            Evidence evidence,
            String pmisRequestId,
            String inputFileId
    ) {
        EvidenceOcrJob job = new EvidenceOcrJob();

        job.evidence = evidence;
        job.pmisRequestId = pmisRequestId;
        job.inputFileId = inputFileId;
        job.status = OcrStatus.PENDING;
        job.requestedAt = LocalDateTime.now();

        return job;
    }

    /**
     * Runtime에서 OCR 작업 ID를 반환받았을 때 연결한다.
     */
    public void registerRuntimeRequest(String ocrRequestId) {
        this.ocrRequestId = ocrRequestId;
    }

    /**
     * OCR 작업이 실행 중임을 기록한다.
     */
    public void markRunning(LocalDateTime startedAt) {
        this.status = OcrStatus.RUNNING;

        if (this.startedAt == null) {
            this.startedAt = startedAt != null
                    ? startedAt
                    : LocalDateTime.now();
        }
    }

    /**
     * OCR 작업 결과를 저장하고 성공 상태로 변경한다.
     */
    public void complete(
            String recognizedText,
            String engineName,
            String engineVersion,
            String warnings,
            LocalDateTime startedAt,
            LocalDateTime finishedAt
    ) {
        this.status = OcrStatus.SUCCEEDED;
        this.recognizedText = recognizedText;
        this.engineName = engineName;
        this.engineVersion = engineVersion;
        this.warnings = warnings;
        this.errorDetail = null;

        if (this.startedAt == null) {
            this.startedAt = startedAt;
        }

        this.finishedAt = finishedAt != null
                ? finishedAt
                : LocalDateTime.now();
    }

    /**
     * OCR 작업 실패 정보를 저장한다.
     */
    public void fail(
            String errorDetail,
            LocalDateTime startedAt,
            LocalDateTime finishedAt
    ) {
        this.status = OcrStatus.FAILED;
        this.errorDetail = errorDetail;

        if (this.startedAt == null) {
            this.startedAt = startedAt;
        }

        this.finishedAt = finishedAt != null
                ? finishedAt
                : LocalDateTime.now();
    }
}
