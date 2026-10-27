
package com.seos.pmis.evidence.service;

import com.seos.pmis.common.exception.BusinessException;
import com.seos.pmis.common.exception.code.CommonErrorCode;
import com.seos.pmis.evidence.entity.Evidence;
import com.seos.pmis.evidence.entity.EvidenceOcrJob;
import com.seos.pmis.evidence.repository.EvidenceOcrJobRepository;
import com.seos.pmis.evidence.repository.EvidenceRepository;
import com.seos.pmis.integration.runtime.dto.RuntimeOcrJobByRequestIdResponse;
import com.seos.pmis.integration.runtime.dto.RuntimeOcrJobRequest;
import com.seos.pmis.integration.runtime.dto.RuntimeOcrJobResponse;
import com.seos.pmis.integration.runtime.dto.RuntimeOcrJobStatusResponse;
import com.seos.pmis.integration.runtime.service.RuntimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvidenceOcrServiceTest {

    @Mock
    private EvidenceRepository evidenceRepository;

    @Mock
    private EvidenceOcrJobRepository evidenceOcrJobRepository;

    @Mock
    private RuntimeService runtimeService;

    @InjectMocks
    private EvidenceOcrService evidenceOcrService;

    private Evidence evidence;

    @BeforeEach
    void setUp() {
        evidence = Evidence.builder()
                .id(1L)
                .evidenceKey("EVD-001")
                .title("OCR test evidence")
                .build();
    }

    @Test
    void createOcrJob_success_registersRuntimeIdentifiers() {
        // given
        String inputFileId = "file-001";

        when(evidenceRepository.findById(1L))
                .thenReturn(Optional.of(evidence));

        when(evidenceOcrJobRepository.saveAndFlush(
                any(EvidenceOcrJob.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(evidenceOcrJobRepository.save(
                any(EvidenceOcrJob.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(runtimeService.createOcrJob(
                any(RuntimeOcrJobRequest.class)))
                .thenAnswer(invocation -> {
                    RuntimeOcrJobRequest request =
                            invocation.getArgument(0);

                    RuntimeOcrJobResponse response =
                            new RuntimeOcrJobResponse();

                    response.setPmisRequestId(
                            request.getPmisRequestId());
                    response.setFileId(request.getFileId());
                    response.setOcrRequestId("runtime-ocr-001");
                    response.setStatus("PENDING");

                    return response;
                });

        // when
        EvidenceOcrJob result =
                evidenceOcrService.createOcrJob(1L, inputFileId);

        // then
        assertNotNull(result);
        assertNotNull(result.getPmisRequestId());
        assertEquals(inputFileId, result.getInputFileId());
        assertEquals("runtime-ocr-001", result.getOcrRequestId());
        assertEquals(
                EvidenceOcrJob.OcrStatus.PENDING,
                result.getStatus());

        ArgumentCaptor<RuntimeOcrJobRequest> captor =
                ArgumentCaptor.forClass(RuntimeOcrJobRequest.class);

        verify(runtimeService).createOcrJob(captor.capture());

        assertEquals(
                result.getPmisRequestId(),
                captor.getValue().getPmisRequestId());

        assertEquals(
                inputFileId,
                captor.getValue().getFileId());

        verify(evidenceOcrJobRepository).saveAndFlush(
                any(EvidenceOcrJob.class));

        verify(evidenceOcrJobRepository).save(result);
    }

    @Test
    void createOcrJob_runtimeException_marksJobAsFailed() {
        // given
        String inputFileId = "file-001";

        when(evidenceRepository.findById(1L))
                .thenReturn(Optional.of(evidence));

        when(evidenceOcrJobRepository.saveAndFlush(
                any(EvidenceOcrJob.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(evidenceOcrJobRepository.save(
                any(EvidenceOcrJob.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(runtimeService.createOcrJob(
                any(RuntimeOcrJobRequest.class)))
                .thenThrow(new BusinessException(
                        CommonErrorCode.INTERNAL_SERVER_ERROR
                ));

        // when
        EvidenceOcrJob result =
                evidenceOcrService.createOcrJob(1L, inputFileId);

        // then
        assertNotNull(result);
        assertNotNull(result.getPmisRequestId());
        assertEquals(inputFileId, result.getInputFileId());
        assertEquals(
                EvidenceOcrJob.OcrStatus.FAILED,
                result.getStatus());

        assertNotNull(result.getErrorDetail());

        verify(evidenceOcrJobRepository).saveAndFlush(
                any(EvidenceOcrJob.class));

        verify(evidenceOcrJobRepository).save(result);

        verify(runtimeService).createOcrJob(
                any(RuntimeOcrJobRequest.class));

        verify(runtimeService, never())
                .getOcrJobByPmisRequestId(any());
    }

    @Test
    void refreshStatus_withoutRuntimeRequestId_recoversExistingRuntimeJob() {
        // given
        String pmisRequestId = "pmis-request-001";
        String fileId = "file-001";
        String runtimeOcrRequestId = "runtime-ocr-001";

        EvidenceOcrJob job = EvidenceOcrJob.create(
                evidence,
                pmisRequestId,
                fileId);

        when(evidenceOcrJobRepository.findById(10L))
                .thenReturn(Optional.of(job));

        RuntimeOcrJobByRequestIdResponse recovery =
                new RuntimeOcrJobByRequestIdResponse();

        recovery.setPmisRequestId(pmisRequestId);
        recovery.setFileId(fileId);
        recovery.setOcrRequestId(runtimeOcrRequestId);
        recovery.setStatus("PENDING");

        when(runtimeService.getOcrJobByPmisRequestId(pmisRequestId))
                .thenReturn(recovery);

        RuntimeOcrJobStatusResponse statusResponse =
                new RuntimeOcrJobStatusResponse();

        statusResponse.setPmisRequestId(pmisRequestId);
        statusResponse.setFileId(fileId);
        statusResponse.setOcrRequestId(runtimeOcrRequestId);
        statusResponse.setStatus("PENDING");

        when(runtimeService.getOcrJobStatus(runtimeOcrRequestId))
                .thenReturn(statusResponse);

        when(evidenceOcrJobRepository.save(
                any(EvidenceOcrJob.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        EvidenceOcrJob result =
                evidenceOcrService.refreshStatus(10L);

        // then
        assertEquals(runtimeOcrRequestId, result.getOcrRequestId());
        assertEquals(
                EvidenceOcrJob.OcrStatus.PENDING,
                result.getStatus());

        verify(runtimeService)
                .getOcrJobByPmisRequestId(pmisRequestId);

        verify(runtimeService)
                .getOcrJobStatus(runtimeOcrRequestId);

        verify(evidenceOcrJobRepository).save(job);

        verify(runtimeService, never()).createOcrJob(
                any(RuntimeOcrJobRequest.class));
    }

    @Test
    void refreshStatus_runtimeJobNotFound_keepsPendingJob() {
        // given
        String pmisRequestId = "pmis-request-002";

        EvidenceOcrJob job = EvidenceOcrJob.create(
                evidence,
                pmisRequestId,
                "file-002");

        when(evidenceOcrJobRepository.findById(20L))
                .thenReturn(Optional.of(job));

        when(runtimeService.getOcrJobByPmisRequestId(pmisRequestId))
                .thenReturn(null);

        // when
        EvidenceOcrJob result =
                evidenceOcrService.refreshStatus(20L);

        // then
        assertEquals(
                EvidenceOcrJob.OcrStatus.PENDING,
                result.getStatus());

        assertEquals(null, result.getOcrRequestId());

        verify(runtimeService)
                .getOcrJobByPmisRequestId(pmisRequestId);

        verify(runtimeService, never()).getOcrJobStatus(any());

        verify(runtimeService, never()).getOcrResult(any());

        verify(runtimeService, never()).createOcrJob(
                any(RuntimeOcrJobRequest.class));
    }

    @Test
    void refreshStatus_recoveryFileIdMismatch_throwsException() {
        // given
        String pmisRequestId = "pmis-request-003";

        EvidenceOcrJob job = EvidenceOcrJob.create(
                evidence,
                pmisRequestId,
                "expected-file");

        when(evidenceOcrJobRepository.findById(30L))
                .thenReturn(Optional.of(job));

        RuntimeOcrJobByRequestIdResponse recovery =
                new RuntimeOcrJobByRequestIdResponse();

        recovery.setPmisRequestId(pmisRequestId);
        recovery.setFileId("different-file");
        recovery.setOcrRequestId("runtime-ocr-003");
        recovery.setStatus("RUNNING");

        when(runtimeService.getOcrJobByPmisRequestId(pmisRequestId))
                .thenReturn(recovery);

        // when / then
        assertThrows(
                IllegalStateException.class,
                () -> evidenceOcrService.refreshStatus(30L));

        assertEquals(null, job.getOcrRequestId());

        verify(runtimeService, never()).getOcrJobStatus(any());
    }

    @Test
    void refreshStatus_mismatchedIdentifiers_throwsException() {
        // given
        EvidenceOcrJob job = EvidenceOcrJob.create(
                evidence,
                "pmis-request-004",
                "file-004");

        job.registerRuntimeRequest("runtime-ocr-004");

        when(evidenceOcrJobRepository.findById(40L))
                .thenReturn(Optional.of(job));

        RuntimeOcrJobStatusResponse response =
                new RuntimeOcrJobStatusResponse();

        response.setOcrRequestId("runtime-ocr-004");
        response.setPmisRequestId("different-pmis-request");
        response.setFileId("file-004");
        response.setStatus("RUNNING");

        when(runtimeService.getOcrJobStatus("runtime-ocr-004"))
                .thenReturn(response);

        // when / then
        assertThrows(
                IllegalStateException.class,
                () -> evidenceOcrService.refreshStatus(40L));
    }

    @Test
    void refreshStatus_terminalJob_doesNotCallRuntime() {
        // given
        EvidenceOcrJob job = EvidenceOcrJob.create(
                evidence,
                "pmis-request-005",
                "file-005");

        job.registerRuntimeRequest("runtime-ocr-005");
        job.fail(
                "Test terminal status",
                null,
                LocalDateTime.now());

        when(evidenceOcrJobRepository.findById(50L))
                .thenReturn(Optional.of(job));

        // when
        EvidenceOcrJob result =
                evidenceOcrService.refreshStatus(50L);

        // then
        assertEquals(
                EvidenceOcrJob.OcrStatus.FAILED,
                result.getStatus());

        verify(runtimeService, never())
                .getOcrJobByPmisRequestId(any());

        verify(runtimeService, never())
                .getOcrJobStatus(any());

        verify(runtimeService, never())
                .getOcrResult(any());
    }
}
