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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.function.Supplier;

/**
 * Automation Runtime 연동 서비스
 *
 * Runtime API 호출과 예외 변환을 담당한다.
 * 외부 API 오류의 세부 계약이 확정되기 전까지
 * 기존 PMIS 오류 응답 규격을 유지한다.
 */
@Slf4j
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
        return execute(
                "getExecutionFiles",
                () -> runtimeApiClient.getExecutionFiles(executionUuid)
        );
    }

    /**
     * 파일 메타데이터 조회
     */
    public RuntimeOutputFileResponse getFileMetadata(String fileId) {
        return execute(
                "getFileMetadata",
                () -> runtimeApiClient.getFileMetadata(fileId)
        );
    }

    /**
     * 파일 내용 다운로드
     */
    public byte[] downloadFileContent(String fileId) {
        return execute(
                "downloadFileContent",
                () -> runtimeApiClient.downloadFileContent(fileId)
        );
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
        return execute(
                "uploadImage",
                () -> runtimeApiClient.uploadImage(
                        fileName,
                        mediaType,
                        fileContent
                )
        );
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
        return execute(
                "createOcrJob",
                () -> runtimeApiClient.createOcrJob(request)
        );
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
        return execute(
                "getOcrJobStatus",
                () -> runtimeApiClient.getOcrJobStatus(ocrRequestId)
        );
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
        return execute(
                "getOcrResult",
                () -> runtimeApiClient.getOcrResult(ocrRequestId)
        );
    }

    /**
     * Runtime API 호출 및 예외 변환
     *
     * HTTP 응답 오류와 통신 오류를 내부 로그에서 구분한다.
     * PMIS 외부 응답은 기존 RUNTIME_API_UNAVAILABLE 규격을 유지한다.
     *
     * @param operation Runtime API 작업명
     * @param action 실행할 Runtime API 호출
     * @param <T> 응답 타입
     * @return Runtime API 응답
     */
    private <T> T execute(
            String operation,
            Supplier<T> action
    ) {
        try {
            return action.get();
        } catch (RestClientResponseException exception) {
            log.warn(
                    "Runtime API returned an HTTP error. operation={}, status={}",
                    operation,
                    exception.getStatusCode().value()
            );

            throw new BusinessException(
                    EvidenceErrorCode.RUNTIME_API_UNAVAILABLE,
                    exception
            );
        } catch (RestClientException exception) {
            log.error(
                    "Runtime API communication failed. operation={}, exceptionType={}",
                    operation,
                    exception.getClass().getSimpleName()
            );

            throw new BusinessException(
                    EvidenceErrorCode.RUNTIME_API_UNAVAILABLE,
                    exception
            );
        } catch (IllegalStateException exception) {
            log.error(
                    "Runtime API returned an invalid response. operation={}, exceptionType={}",
                    operation,
                    exception.getClass().getSimpleName()
            );

            throw new BusinessException(
                    EvidenceErrorCode.RUNTIME_API_UNAVAILABLE,
                    exception
            );
        }
    }
}