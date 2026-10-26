package com.seos.pmis.evidence.service;

import com.seos.pmis.common.exception.BusinessException;
import com.seos.pmis.evidence.entity.Evidence;
import com.seos.pmis.evidence.entity.EvidenceOcrJob;
import com.seos.pmis.evidence.exception.code.EvidenceErrorCode;
import com.seos.pmis.evidence.repository.EvidenceOcrJobRepository;
import com.seos.pmis.evidence.repository.EvidenceRepository;
import com.seos.pmis.integration.runtime.dto.RuntimeOcrJobByRequestIdResponse;
import com.seos.pmis.integration.runtime.dto.RuntimeOcrJobRequest;
import com.seos.pmis.integration.runtime.dto.RuntimeOcrJobResponse;
import com.seos.pmis.integration.runtime.dto.RuntimeOcrJobStatusResponse;
import com.seos.pmis.integration.runtime.dto.RuntimeOcrResultResponse;
import com.seos.pmis.integration.runtime.service.RuntimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/**
 * Evidence OCR 서비스
 *
 * 기존 Evidence에 OCR 작업을 연결하고,
 * Automation Runtime의 OCR 요청 및 처리 결과를 관리한다.
 *
 * 식별자 구분:
 * - Evidence.sourceFileId: 기존 Runtime 실행 결과 파일 ID
 * - EvidenceOcrJob.inputFileId: OCR 입력 이미지 업로드 파일 ID
 * - EvidenceOcrJob.pmisRequestId: PMIS 요청 ID
 * - EvidenceOcrJob.ocrRequestId: Runtime OCR 요청 ID
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EvidenceOcrService {

    private final EvidenceRepository evidenceRepository;
    private final EvidenceOcrJobRepository evidenceOcrJobRepository;
    private final RuntimeService runtimeService;

    // =========================================================================
    // OCR 작업 생성
    // =========================================================================

    /**
     * 기존 Evidence에 OCR 작업을 생성한다.
     *
     * inputFileId는 기존 Runtime OCR 업로드 API에서 발급받은
     * 파일 ID를 전달받는다.
     *
     * @param evidenceId 기존 Evidence ID
     * @param inputFileId OCR 입력 이미지의 Runtime 파일 ID
     * @return 생성된 OCR 작업 이력
     */
    @Transactional
    public EvidenceOcrJob createOcrJob(
            Long evidenceId,
            String inputFileId
    ) {
        if (evidenceId == null) {
            throw new IllegalArgumentException(
                    "Evidence ID는 필수입니다."
            );
        }

        if (!hasText(inputFileId)) {
            throw new IllegalArgumentException(
                    "OCR 입력 파일 ID는 필수입니다."
            );
        }

        Evidence evidence = evidenceRepository.findById(evidenceId)
                .orElseThrow(() -> new BusinessException(
                        EvidenceErrorCode.EVIDENCE_NOT_FOUND
                ));

        // PMIS 요청 ID는 요청마다 새로 생성한다.
        String pmisRequestId = UUID.randomUUID().toString();

        EvidenceOcrJob ocrJob = EvidenceOcrJob.create(
                evidence,
                pmisRequestId,
                inputFileId
        );

        /*
         * Runtime 호출 전에 PMIS 작업 이력을 저장한다.
         *
         * DB 저장과 외부 API 호출은 하나의 원자적 작업이 아니다.
         * Runtime이 작업을 접수한 후 응답이 유실될 수 있으므로,
         * PMIS 요청 ID 기반 복구 경로를 제공한다.
         */
        EvidenceOcrJob savedJob =
                evidenceOcrJobRepository.saveAndFlush(ocrJob);

        RuntimeOcrJobRequest request = new RuntimeOcrJobRequest(
                pmisRequestId,
                inputFileId
        );

        try {
            RuntimeOcrJobResponse response =
                    runtimeService.createOcrJob(request);

            if (response == null) {
                savedJob.fail(
                        "Runtime OCR 응답이 비어 있습니다.",
                        null,
                        LocalDateTime.now()
                );

                return evidenceOcrJobRepository.save(savedJob);
            }

            if (!hasText(response.getOcrRequestId())) {
                savedJob.fail(
                        "Runtime 응답에 OCR 요청 ID가 없습니다.",
                        null,
                        LocalDateTime.now()
                );

                return evidenceOcrJobRepository.save(savedJob);
            }

            // Runtime 응답의 요청 식별자를 검증한다.
            if (!Objects.equals(
                    pmisRequestId,
                    response.getPmisRequestId()
            )) {
                savedJob.fail(
                        "Runtime 응답의 PMIS 요청 ID가 일치하지 않습니다.",
                        null,
                        LocalDateTime.now()
                );

                return evidenceOcrJobRepository.save(savedJob);
            }

            if (!Objects.equals(
                    inputFileId,
                    response.getFileId()
            )) {
                savedJob.fail(
                        "Runtime 응답의 입력 파일 ID가 일치하지 않습니다.",
                        null,
                        LocalDateTime.now()
                );

                return evidenceOcrJobRepository.save(savedJob);
            }

            savedJob.registerRuntimeRequest(
                    response.getOcrRequestId()
            );

            applyCreationStatus(savedJob, response);

            return evidenceOcrJobRepository.save(savedJob);

        } catch (BusinessException exception) {
            /*
             * 주의:
             * 네트워크 오류는 Runtime이 작업을 접수한 뒤 응답만
             * 유실된 경우일 수도 있다.
             *
             * 현재는 기존 정책에 따라 FAILED로 기록한다.
             * 따라서 이 FAILED 작업은 아래 refreshStatus()의
             * 자동 복구 대상이 아니다. 모호한 실패를 복구하려면
             * 별도의 상태 정책 변경이 필요하다.
             */
            savedJob.fail(
                    "Runtime OCR 요청 중 오류가 발생했습니다: "
                            + exception.getMessage(),
                    null,
                    LocalDateTime.now()
            );

            return evidenceOcrJobRepository.save(savedJob);
        }
    }

    /**
     * Runtime OCR 작업 생성 응답의 초기 상태를 반영한다.
     */
    private void applyCreationStatus(
            EvidenceOcrJob ocrJob,
            RuntimeOcrJobResponse response
    ) {
        String status = normalizeStatus(response.getStatus());

        switch (status) {
            case "PENDING" -> {
                // 엔티티의 기본 상태를 유지한다.
            }

            case "RUNNING" -> ocrJob.markRunning(
                    LocalDateTime.now()
            );

            case "FAILED" -> ocrJob.fail(
                    "Runtime에서 OCR 작업 실패 상태를 반환했습니다.",
                    null,
                    LocalDateTime.now()
            );

            case "SUCCEEDED" -> {
                /*
                 * 결과 본문을 확인하기 전에는 완료로 확정하지 않는다.
                 * 이후 refreshStatus()에서 상태와 결과를 동기화한다.
                 */
            }

            default -> {
                // 알 수 없는 상태는 임의로 성공 또는 실패 처리하지 않는다.
            }
        }
    }

    // =========================================================================
    // OCR 작업 이력 조회
    // =========================================================================

    /**
     * Evidence에 연결된 OCR 작업 이력을 최신 요청 순으로 조회한다.
     */
    public List<EvidenceOcrJob> findAllByEvidenceId(Long evidenceId) {
        if (evidenceId == null
                || !evidenceRepository.existsById(evidenceId)) {
            throw new BusinessException(
                    EvidenceErrorCode.EVIDENCE_NOT_FOUND
            );
        }

        return evidenceOcrJobRepository
                .findAllByEvidenceIdOrderByRequestedAtDesc(evidenceId);
    }

    /**
     * PMIS 요청 ID로 OCR 작업 이력을 조회한다.
     */
    public EvidenceOcrJob findByPmisRequestId(String pmisRequestId) {
        if (!hasText(pmisRequestId)) {
            throw new IllegalArgumentException(
                    "PMIS 요청 ID는 필수입니다."
            );
        }

        return evidenceOcrJobRepository
                .findByPmisRequestId(pmisRequestId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "OCR 작업 이력을 찾을 수 없습니다. pmisRequestId="
                                + pmisRequestId
                ));
    }

    /**
     * PMIS OCR 작업 이력 ID로 단건 조회한다.
     */
    public EvidenceOcrJob findById(Long ocrJobId) {
        return getOcrJob(ocrJobId);
    }

    // =========================================================================
    // OCR 상태 동기화 및 Runtime 작업 복구
    // =========================================================================

    /**
     * Runtime의 최신 OCR 상태를 조회하고 PMIS 이력에 반영한다.
     *
     * Runtime OCR 요청 ID가 아직 연결되지 않은 PENDING 작업은
     * PMIS 요청 ID로 기존 Runtime 작업을 조회하여 복구를 시도한다.
     *
     * SUCCEEDED 및 FAILED 상태는 종결 상태로 취급한다.
     */
    @Transactional
    public EvidenceOcrJob refreshStatus(Long ocrJobId) {
        EvidenceOcrJob ocrJob = getOcrJob(ocrJobId);

        if (isTerminalStatus(ocrJob)) {
            return ocrJob;
        }

        String ocrRequestId = ocrJob.getOcrRequestId();

        /*
         * Runtime 요청 ID가 없다면 PMIS 요청 ID로 기존 Runtime 작업을
         * 조회한다. 여기서는 새 OCR 작업을 생성하지 않는다.
         */
        if (!hasText(ocrRequestId)) {
            if (ocrJob.getStatus() != EvidenceOcrJob.OcrStatus.PENDING) {
                return ocrJob;
            }

            RuntimeOcrJobByRequestIdResponse recovery =
                    runtimeService.getOcrJobByPmisRequestId(
                            ocrJob.getPmisRequestId()
                    );

            // Runtime에서 찾지 못한 경우 현재 PMIS 상태를 유지한다.
            if (recovery == null) {
                return ocrJob;
            }

            // 복구 응답이 현재 PMIS 작업에 해당하는지 검증한다.
            if (!Objects.equals(
                    ocrJob.getPmisRequestId(),
                    recovery.getPmisRequestId()
            )) {
                throw new IllegalStateException(
                        "Runtime 복구 응답의 PMIS 요청 ID가 일치하지 않습니다."
                );
            }

            if (!Objects.equals(
                    ocrJob.getInputFileId(),
                    recovery.getFileId()
            )) {
                throw new IllegalStateException(
                        "Runtime 복구 응답의 입력 파일 ID가 일치하지 않습니다."
                );
            }

            if (!hasText(recovery.getOcrRequestId())) {
                throw new IllegalStateException(
                        "Runtime 복구 응답에 OCR 요청 ID가 없습니다."
                );
            }

            // 기존 PMIS 작업에 Runtime 요청 ID를 연결한다.
            ocrJob.registerRuntimeRequest(
                    recovery.getOcrRequestId()
            );

            ocrRequestId = recovery.getOcrRequestId();
        }

        RuntimeOcrJobStatusResponse response =
                runtimeService.getOcrJobStatus(ocrRequestId);

        if (response == null) {
            return evidenceOcrJobRepository.save(ocrJob);
        }

        // Runtime 응답이 다른 작업의 정보인지 검증한다.
        if (!Objects.equals(
                ocrRequestId,
                response.getOcrRequestId()
        )) {
            throw new IllegalStateException(
                    "Runtime OCR 요청 ID가 일치하지 않습니다."
            );
        }

        if (!Objects.equals(
                ocrJob.getPmisRequestId(),
                response.getPmisRequestId()
        )) {
            throw new IllegalStateException(
                    "Runtime PMIS 요청 ID가 일치하지 않습니다."
            );
        }

        if (!Objects.equals(
                ocrJob.getInputFileId(),
                response.getFileId()
        )) {
            throw new IllegalStateException(
                    "Runtime OCR 입력 파일 ID가 일치하지 않습니다."
            );
        }

        String status = normalizeStatus(response.getStatus());

        switch (status) {
            case "PENDING" -> {
                // 현재 상태를 유지한다.
            }

            case "RUNNING" -> {
                ocrJob.markRunning(
                        toLocalDateTime(response.getStartedAt())
                );
            }

            case "SUCCEEDED" -> syncOcrResult(ocrJob, response);

            case "FAILED" -> {
                String errorDetail = response.getError() == null
                        ? "Runtime OCR 작업이 실패했습니다."
                        : response.getError().toString();

                ocrJob.fail(
                        errorDetail,
                        toLocalDateTime(response.getStartedAt()),
                        toLocalDateTime(response.getFinishedAt())
                );
            }

            default -> {
                // 미지원 상태는 임의로 변환하지 않는다.
            }
        }

        return evidenceOcrJobRepository.save(ocrJob);
    }

    // =========================================================================
    // OCR 결과 동기화
    // =========================================================================

    /**
     * Runtime OCR 결과를 조회하고 PMIS 이력에 반영한다.
     *
     * 상태 API와 결과 API의 응답을 각각 검증한 뒤 완료 처리한다.
     */
    private void syncOcrResult(
            EvidenceOcrJob ocrJob,
            RuntimeOcrJobStatusResponse statusResponse
    ) {
        RuntimeOcrResultResponse result =
                runtimeService.getOcrResult(
                        ocrJob.getOcrRequestId()
                );

        if (result == null) {
            return;
        }

        if (!Objects.equals(
                ocrJob.getOcrRequestId(),
                result.getOcrRequestId()
        )) {
            throw new IllegalStateException(
                    "OCR 결과의 Runtime 요청 ID가 일치하지 않습니다."
            );
        }

        if (!Objects.equals(
                ocrJob.getPmisRequestId(),
                result.getPmisRequestId()
        )) {
            throw new IllegalStateException(
                    "OCR 결과의 PMIS 요청 ID가 일치하지 않습니다."
            );
        }

        if (!Objects.equals(
                ocrJob.getInputFileId(),
                result.getFileId()
        )) {
            throw new IllegalStateException(
                    "OCR 결과의 입력 파일 ID가 일치하지 않습니다."
            );
        }

        if (!"SUCCEEDED".equals(normalizeStatus(result.getStatus()))) {
            // 결과 API가 완료 상태가 아니라면 성공으로 확정하지 않는다.
            return;
        }

        RuntimeOcrResultResponse.Engine engine = result.getEngine();

        String engineName = engine == null
                ? null
                : engine.getName();

        String engineVersion = engine == null
                ? null
                : engine.getVersion();

        String warnings = result.getWarnings() == null
                || result.getWarnings().isEmpty()
                ? null
                : String.join("\n", result.getWarnings());

        ocrJob.complete(
                result.getText(),
                engineName,
                engineVersion,
                warnings,
                toLocalDateTime(statusResponse.getStartedAt()),
                toLocalDateTime(statusResponse.getFinishedAt())
        );
    }

    /**
     * OCR 상태와 결과를 다시 동기화한다.
     */
    @Transactional
    public EvidenceOcrJob refreshResult(Long ocrJobId) {
        return refreshStatus(ocrJobId);
    }

    // =========================================================================
    // 내부 조회 및 변환
    // =========================================================================

    private EvidenceOcrJob getOcrJob(Long ocrJobId) {
        if (ocrJobId == null) {
            throw new IllegalArgumentException(
                    "OCR 작업 이력 ID는 필수입니다."
            );
        }

        return evidenceOcrJobRepository.findById(ocrJobId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "OCR 작업 이력을 찾을 수 없습니다. ocrJobId="
                                + ocrJobId
                ));
    }

    private boolean isTerminalStatus(EvidenceOcrJob ocrJob) {
        return ocrJob.getStatus() == EvidenceOcrJob.OcrStatus.SUCCEEDED
                || ocrJob.getStatus() == EvidenceOcrJob.OcrStatus.FAILED;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String normalizeStatus(String status) {
        return status == null
                ? ""
                : status.trim().toUpperCase(Locale.ROOT);
    }

    /**
     * Runtime OffsetDateTime을 DB 엔티티의 LocalDateTime으로 변환한다.
     *
     * Asia/Seoul 기준의 날짜·시간으로 변환한다.
     */
    private LocalDateTime toLocalDateTime(OffsetDateTime value) {
        return value == null
                ? null
                : value.atZoneSameInstant(ZoneId.of("Asia/Seoul"))
                        .toLocalDateTime();
    }
}