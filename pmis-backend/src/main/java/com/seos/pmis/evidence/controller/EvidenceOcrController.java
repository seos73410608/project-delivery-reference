
package com.seos.pmis.evidence.controller;

import com.seos.pmis.evidence.entity.EvidenceOcrJob;
import com.seos.pmis.evidence.service.EvidenceOcrService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EvidenceOcrController {

    private final EvidenceOcrService evidenceOcrService;

    /**
     * 기존 Evidence에 OCR 작업을 생성한다.
     *
     * POST /api/evidences/{evidenceId}/ocr-jobs
     */
    @PostMapping("/evidences/{evidenceId}/ocr-jobs")
    public ResponseEntity<EvidenceOcrJobResponse> createOcrJob(
            @PathVariable Long evidenceId,
            @Valid @RequestBody CreateEvidenceOcrJobRequest request
    ) {
        EvidenceOcrJob job = evidenceOcrService.createOcrJob(
                evidenceId,
                request.inputFileId()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(EvidenceOcrJobResponse.from(job));
    }

    /**
     * Evidence에 연결된 OCR 작업 이력을 최신순으로 조회한다.
     *
     * GET /api/evidences/{evidenceId}/ocr-jobs
     */
    @GetMapping("/evidences/{evidenceId}/ocr-jobs")
    public ResponseEntity<List<EvidenceOcrJobResponse>> getOcrJobs(
            @PathVariable Long evidenceId
    ) {
        List<EvidenceOcrJobResponse> responses =
                evidenceOcrService.findAllByEvidenceId(evidenceId)
                        .stream()
                        .map(EvidenceOcrJobResponse::from)
                        .toList();

        return ResponseEntity.ok(responses);
    }

    /**
     * PMIS에 저장된 OCR 작업 이력을 단건 조회한다.
     *
     * GET /api/evidence-ocr-jobs/{ocrJobId}
     */
    @GetMapping("/evidence-ocr-jobs/{ocrJobId}")
    public ResponseEntity<EvidenceOcrJobResponse> getOcrJob(
            @PathVariable Long ocrJobId
    ) {
        EvidenceOcrJob job = evidenceOcrService.findById(ocrJobId);

        return ResponseEntity.ok(EvidenceOcrJobResponse.from(job));
    }

    /**
     * Runtime의 최신 OCR 상태 및 결과를 조회해
     * PMIS 이력을 갱신한다.
     *
     * POST /api/evidence-ocr-jobs/{ocrJobId}/refresh
     */
    @PostMapping("/evidence-ocr-jobs/{ocrJobId}/refresh")
    public ResponseEntity<EvidenceOcrJobResponse> refreshOcrJob(
            @PathVariable Long ocrJobId
    ) {
        EvidenceOcrJob job =
                evidenceOcrService.refreshStatus(ocrJobId);

        return ResponseEntity.ok(EvidenceOcrJobResponse.from(job));
    }

    /**
     * OCR 작업 생성 요청 DTO.
     */
    public record CreateEvidenceOcrJobRequest(
            @NotBlank(message = "OCR 입력 파일 ID는 필수입니다.")
            String inputFileId
    ) {
    }

    /**
     * PMIS OCR 작업 응답 DTO.
     */
    public record EvidenceOcrJobResponse(
            Long id,
            Long evidenceId,
            String pmisRequestId,
            String ocrRequestId,
            String inputFileId,
            String status,
            String recognizedText,
            String engineName,
            String engineVersion,
            String warnings,
            String errorDetail,
            String requestedAt,
            String startedAt,
            String finishedAt
    ) {
        public static EvidenceOcrJobResponse from(
                EvidenceOcrJob job
        ) {
            return new EvidenceOcrJobResponse(
                    job.getId(),
                    job.getEvidence().getId(),
                    job.getPmisRequestId(),
                    job.getOcrRequestId(),
                    job.getInputFileId(),
                    job.getStatus() == null
                            ? null
                            : job.getStatus().name(),
                    job.getRecognizedText(),
                    job.getEngineName(),
                    job.getEngineVersion(),
                    job.getWarnings(),
                    job.getErrorDetail(),
                    job.getRequestedAt() == null
                            ? null
                            : job.getRequestedAt().toString(),
                    job.getStartedAt() == null
                            ? null
                            : job.getStartedAt().toString(),
                    job.getFinishedAt() == null
                            ? null
                            : job.getFinishedAt().toString()
            );
        }
    }
}
