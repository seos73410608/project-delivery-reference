
package com.seos.pmis.integration.runtime.service;

import com.seos.pmis.common.exception.BusinessException;
import com.seos.pmis.evidence.exception.code.EvidenceErrorCode;
import com.seos.pmis.integration.runtime.client.RuntimeApiClient;
import com.seos.pmis.integration.runtime.dto.RuntimeOcrJobByRequestIdResponse;
import com.seos.pmis.integration.runtime.dto.RuntimeOcrJobRequest;
import com.seos.pmis.integration.runtime.dto.RuntimeOcrJobResponse;
import com.seos.pmis.integration.runtime.dto.RuntimeOcrJobStatusResponse;
import com.seos.pmis.integration.runtime.dto.RuntimeOcrResultResponse;
import com.seos.pmis.integration.runtime.dto.RuntimeOcrUploadResponse;
import com.seos.pmis.integration.runtime.dto.RuntimeOutputFileResponse;
import com.seos.pmis.integration.runtime.dto.RuntimePmisFileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
 *
 * 담당 기능:
 * 1. Job 실행 결과 파일 조회
 * 2. PMIS 일반 파일 업로드 및 조회
 * 3. OCR 이미지 업로드
 * 4. OCR 작업 생성 및 조회
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RuntimeService {

    private final RuntimeApiClient runtimeApiClient;

    /**
     * 실행 결과 파일 목록 조회
     *
     * @param executionUuid Runtime 실행 UUID
     * @return 실행 결과 파일 목록
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
     * Job 실행 결과 파일 메타데이터 조회
     *
     * @param fileId Runtime 파일 ID
     * @return 파일 메타데이터
     */
    public RuntimeOutputFileResponse getFileMetadata(
            String fileId
    ) {
        return execute(
                "getFileMetadata",
                () -> runtimeApiClient.getFileMetadata(fileId)
        );
    }

    /**
     * Job 실행 결과 파일 다운로드
     *
     * @param fileId Runtime 파일 ID
     * @return 파일 바이너리
     */
    public byte[] downloadFileContent(
            String fileId
    ) {
        return execute(
                "downloadFileContent",
                () -> runtimeApiClient.downloadFileContent(fileId)
        );
    }

    /**
     * PMIS 일반 파일 업로드
     *
     * API:
     * POST /api/v1/pmis/files
     *
     * @param fileName 업로드할 파일명
     * @param mediaType 파일 MIME 타입
     * @param fileContent 파일 바이너리
     * @return PMIS 일반 파일 업로드 결과
     */
    public RuntimePmisFileResponse uploadPmisFile(
            String fileName,
            MediaType mediaType,
            byte[] fileContent
    ) {
        return execute(
                "uploadPmisFile",
                () -> runtimeApiClient.uploadPmisFile(
                        fileName,
                        mediaType,
                        fileContent
                )
        );
    }

    /**
     * PMIS 일반 파일 메타데이터 조회
     *
     * API:
     * GET /api/v1/pmis/files/{file_id}
     *
     * @param fileId Runtime PMIS 파일 ID
     * @return PMIS 일반 파일 메타데이터
     */
    public RuntimePmisFileResponse getPmisFileMetadata(
            String fileId
    ) {
        return execute(
                "getPmisFileMetadata",
                () -> runtimeApiClient.getPmisFileMetadata(fileId)
        );
    }

    /**
     * PMIS 일반 파일 다운로드
     *
     * API:
     * GET /api/v1/pmis/files/{file_id}/content
     *
     * @param fileId Runtime PMIS 파일 ID
     * @return 파일 바이너리
     */
    public byte[] downloadPmisFileContent(
            String fileId
    ) {
        return execute(
                "downloadPmisFileContent",
                () -> runtimeApiClient.downloadPmisFileContent(fileId)
        );
    }

    /**
     * OCR 대상 이미지 업로드
     *
     * @param fileName 업로드할 파일명
     * @param mediaType 이미지 MIME 타입
     * @param fileContent 이미지 바이너리
     * @return OCR 이미지 업로드 결과
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
     * PMIS 요청 ID로 Runtime OCR 작업 조회
     *
     * Runtime의 404는 작업 미발견으로 구분한다.
     * 그 외 오류는 기존 Runtime API 장애 처리 규약을 유지한다.
     *
     * @param pmisRequestId PMIS 요청 ID
     * @return OCR 작업 정보, 또는 Runtime에서 찾지 못한 경우 null
     */
    public RuntimeOcrJobByRequestIdResponse getOcrJobByPmisRequestId(
            String pmisRequestId
    ) {
        try {
            return runtimeApiClient.getOcrJobByPmisRequestId(pmisRequestId);
        } catch (RestClientResponseException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.info(
                        "Runtime OCR job not found by PMIS request ID. pmisRequestId={}",
                        pmisRequestId
                );
                return null;
            }

            log.warn(
                    "Runtime OCR recovery lookup returned an HTTP error. status={}",
                    exception.getStatusCode().value()
            );

            throw new BusinessException(
                    EvidenceErrorCode.RUNTIME_API_UNAVAILABLE,
                    exception
            );
        } catch (RestClientException | IllegalStateException exception) {
            log.error(
                    "Runtime OCR recovery lookup failed. exceptionType={}",
                    exception.getClass().getSimpleName()
            );

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
