package com.seos.pmis.integration.runtime.service;

import com.seos.pmis.common.exception.BusinessException;
import com.seos.pmis.evidence.exception.code.EvidenceErrorCode;
import com.seos.pmis.integration.runtime.client.RuntimeApiClient;
import com.seos.pmis.integration.runtime.dto.RuntimeOutputFileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.List;

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
                    EvidenceErrorCode.RUNTIME_API_UNAVAILABLE
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
                    EvidenceErrorCode.RUNTIME_API_UNAVAILABLE
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
                    EvidenceErrorCode.RUNTIME_API_UNAVAILABLE
            );
        }
    }
}