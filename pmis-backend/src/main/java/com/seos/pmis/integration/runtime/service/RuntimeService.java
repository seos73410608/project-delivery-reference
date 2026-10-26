package com.seos.pmis.integration.runtime.service;

import com.seos.pmis.common.exception.BusinessException;
import com.seos.pmis.evidence.exception.code.EvidenceErrorCode;
import com.seos.pmis.integration.runtime.client.RuntimeApiClient;
import com.seos.pmis.integration.runtime.dto.RuntimeOcrJobRequest;
import com.seos.pmis.integration.runtime.dto.RuntimeOcrJobResponse;
import com.seos.pmis.integration.runtime.dto.RuntimeOcrJobStatusResponse;
import com.seos.pmis.integration.runtime.dto.RuntimeOcrResultResponse;
import com.seos.pmis.integration.runtime.dto.RuntimeOcrUploadResponse;
import com.seos.pmis.integration.runtime.dto.RuntimeOutputFileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.List;

/**
 * Automation Runtime 연동 서비스
 *
 * Runtime API 호출을 담당하고 API 통신 오류를 공통 예외로 변환한다.
 */
@Service
@RequiredArgsConstructor
public class RuntimeService {

    private final RuntimeApiClient runtimeApiClient;

    /**
     * 실행 결과 파일 목록 조회
     */
    public List<RuntimeOutputFileResponse> getExecutionFiles(
            String executionUuid
    ) {
        try {
            return runtimeApiClient.getExecutionFiles(executionUuid);
        } catch (RestClientException | IllegalStateException exception) {
            throw new BusinessException(
                    EvidenceErrorCode.RUNTIME_API_UNAVAILABLE,
                    exception
            );
        }
    }

    /**
     * 파일 메타데이터 조회
     */
    public RuntimeOutputFileResponse getFileMetadata(String fileId) {
        try {
            return runtimeApiClient.getFileMetadata(fileId);
        } catch (RestClientException | IllegalStateException exception) {
            throw new BusinessException(
                    EvidenceErrorCode.RUNTIME_API_UNAVAILABLE,
                    exception
            );
        }
    }

    /**
     * 파일 내용 다운로드
     */
    public byte[] downloadFileContent(String fileId) {
        try {
            return runtimeApiClient.downloadFileContent(fileId);
        } catch (RestClientException | IllegalStateException exception) {
            throw new BusinessException(
                    EvidenceErrorCode.RUNTIME_API_UNAVAILABLE,
                    exception
            );
        }
    }

    /**
     * OCR 대상 이미지 업로드
     *
     * @param fileName 업로드할 파일명
     * @param mediaType 이미지 MIME 타입
     * @param fileContent 이미지 바이너리
     * @return 업로드 결과
     */
    public RuntimeOcrUploadResponse uploadImage(
            String fileName,
            MediaType mediaType,
            byte[] fileContent
    ) {
        try {
            return runtimeApiClient.uploadImage(
                    fileName,
                    mediaType,
                    fileContent
            );
        } catch (RestClientException | IllegalStateException exception) {
            throw new BusinessException(
                    EvidenceErrorCode.RUNTIME_API_UNAVAILABLE,
                    exception
            );
        }
    }

    /**
     * OCR 작업 생성
     *
     * @param request OCR 작업 요청 정보
     * @return OCR 작업 생성 결과
     */
    public RuntimeOcrJobResponse createOcrJob(
            RuntimeOcrJobRequest request
    ) {
        try {
            return runtimeApiClient.createOcrJob(request);
        } catch (RestClientException | IllegalStateException exception) {
            throw new BusinessException(
                    EvidenceErrorCode.RUNTIME_API_UNAVAILABLE,
                    exception
            );
        }
    }

    /**
     * OCR 작업 상태 조회
     *
     * @param ocrRequestId Runtime OCR 요청 ID
     * @return OCR 작업 상태
     */
    public RuntimeOcrJobStatusResponse getOcrJobStatus(
            String ocrRequestId
    ) {
        try {
            return runtimeApiClient.getOcrJobStatus(ocrRequestId);
        } catch (RestClientException | IllegalStateException exception) {
            throw new BusinessException(
                    EvidenceErrorCode.RUNTIME_API_UNAVAILABLE,
                    exception
            );
        }
    }

    /**
     * OCR 결과 조회
     *
     * @param ocrRequestId Runtime OCR 요청 ID
     * @return OCR 결과
     */
    public RuntimeOcrResultResponse getOcrResult(
            String ocrRequestId
    ) {
        try {
            return runtimeApiClient.getOcrResult(ocrRequestId);
        } catch (RestClientException | IllegalStateException exception) {
            throw new BusinessException(
                    EvidenceErrorCode.RUNTIME_API_UNAVAILABLE,
                    exception
            );
        }
    }
}