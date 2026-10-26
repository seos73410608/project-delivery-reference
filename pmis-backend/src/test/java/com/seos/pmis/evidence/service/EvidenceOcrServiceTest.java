package com.seos.pmis.evidence.service;

import com.seos.pmis.evidence.entity.Evidence;
import com.seos.pmis.evidence.entity.EvidenceOcrJob;
import com.seos.pmis.evidence.repository.EvidenceOcrJobRepository;
import com.seos.pmis.evidence.repository.EvidenceRepository;
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
                .title("OCR 테스트 증적")
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
    void refreshStatus_withoutRuntimeRequestId_doesNotCallRuntime() {
        // given
        EvidenceOcrJob job = EvidenceOcrJob.create(
                evidence,
                "pmis-request-001",
                "file-001");

        when(evidenceOcrJobRepository.findById(10L))
                .thenReturn(Optional.of(job));

        // when
        EvidenceOcrJob result =
                evidenceOcrService.refreshStatus(10L);

        // then
        assertEquals(job, result);
        assertEquals(
                EvidenceOcrJob.OcrStatus.PENDING,
                result.getStatus());

        verify(runtimeService, never())
                .getOcrJobStatus(any());

        verify(runtimeService, never())
                .getOcrResult(any());
    }

    @Test
    void refreshStatus_mismatchedIdentifiers_throwsException() {
        // given
        EvidenceOcrJob job = EvidenceOcrJob.create(
                evidence,
                "pmis-request-002",
                "file-002");

        job.registerRuntimeRequest("runtime-ocr-002");

        when(evidenceOcrJobRepository.findById(20L))
                .thenReturn(Optional.of(job));

        RuntimeOcrJobStatusResponse response =
                new RuntimeOcrJobStatusResponse();

        response.setOcrRequestId("runtime-ocr-002");
        response.setPmisRequestId("different-pmis-request");
        response.setFileId("file-002");
        response.setStatus("RUNNING");

        when(runtimeService.getOcrJobStatus("runtime-ocr-002"))
                .thenReturn(response);

        // when / then
        assertThrows(
                IllegalStateException.class,
                () -> evidenceOcrService.refreshStatus(20L));
    }

    @Test
    void refreshStatus_terminalJob_doesNotCallRuntime() {
        // given
        EvidenceOcrJob job = EvidenceOcrJob.create(
                evidence,
                "pmis-request-003",
                "file-003");

        job.registerRuntimeRequest("runtime-ocr-003");
        job.fail(
                "테스트용 종료 상태",
                null,
                LocalDateTime.now());

        when(evidenceOcrJobRepository.findById(30L))
                .thenReturn(Optional.of(job));

        // when
        EvidenceOcrJob result =
                evidenceOcrService.refreshStatus(30L);

        // then
        assertEquals(
                EvidenceOcrJob.OcrStatus.FAILED,
                result.getStatus());

        verify(runtimeService, never())
                .getOcrJobStatus(any());

        verify(runtimeService, never())
                .getOcrResult(any());
    }
}