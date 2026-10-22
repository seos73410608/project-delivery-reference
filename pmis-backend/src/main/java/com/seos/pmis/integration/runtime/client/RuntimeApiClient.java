package com.seos.pmis.integration.runtime.client;

import com.seos.pmis.integration.runtime.config.RuntimeProperties;
import com.seos.pmis.integration.runtime.dto.RuntimeOcrJobRequest;
import com.seos.pmis.integration.runtime.dto.RuntimeOcrJobResponse;
import com.seos.pmis.integration.runtime.dto.RuntimeOcrJobStatusResponse;
import com.seos.pmis.integration.runtime.dto.RuntimeOcrResultResponse;
import com.seos.pmis.integration.runtime.dto.RuntimeOcrUploadResponse;
import com.seos.pmis.integration.runtime.dto.RuntimeOutputFileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * Automation Runtime API 클라이언트
 *
 * PMIS 백엔드에서 Automation Runtime API를 호출한다.
 *
 * 담당 기능:
 * 1. 실행 결과 파일 목록 조회
 * 2. 파일 메타데이터 조회
 * 3. 파일 바이너리 조회
 * 4. OCR 대상 이미지 업로드
 * 5. OCR 작업 생성
 * 6. OCR 작업 상태 조회
 * 7. OCR 결과 조회
 *
 * 업무 규칙과 증적 등록 처리는 Service 계층에서 담당한다.
 */
@Component
@RequiredArgsConstructor
public class RuntimeApiClient {

    private final RestClient.Builder restClientBuilder;
    private final RuntimeProperties runtimeProperties;

    /**
     * 실행 결과 파일 목록 조회
     *
     * API:
     * GET /api/v1/executions/{execution_uuid}/files
     *
     * @param executionUuid Runtime 실행 UUID
     * @return 실행 결과 파일 목록
     */
    public List<RuntimeOutputFileResponse> getExecutionFiles(
            String executionUuid
    ) {
        List<RuntimeOutputFileResponse> response =
                createRestClient()
                        .get()
                        .uri(
                                "/api/v1/executions/{executionUuid}/files",
                                executionUuid
                        )
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .body(
                                new ParameterizedTypeReference<
                                        List<RuntimeOutputFileResponse>
                                        >() {
                                }
                        );

        if (response == null) {
            throw new IllegalStateException(
                    "Runtime API returned an empty file list response."
            );
        }

        return response;
    }

    /**
     * 파일 메타데이터 조회
     *
     * API:
     * GET /api/v1/files/{file_id}
     *
     * @param fileId Runtime 파일 ID
     * @return 파일 메타데이터
     */
    public RuntimeOutputFileResponse getFileMetadata(
            String fileId
    ) {
        RuntimeOutputFileResponse response =
                createRestClient()
                        .get()
                        .uri(
                                "/api/v1/files/{fileId}",
                                fileId
                        )
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .body(RuntimeOutputFileResponse.class);

        if (response == null) {
            throw new IllegalStateException(
                    "Runtime API returned an empty file metadata response."
            );
        }

        return response;
    }

    /**
     * 파일 바이너리 조회
     *
     * API:
     * GET /api/v1/files/{file_id}/content
     *
     * @param fileId Runtime 파일 ID
     * @return 파일 바이너리 데이터
     */
    public byte[] downloadFileContent(
            String fileId
    ) {
        byte[] response =
                createRestClient()
                        .get()
                        .uri(
                                "/api/v1/files/{fileId}/content",
                                fileId
                        )
                        .accept(MediaType.APPLICATION_OCTET_STREAM)
                        .retrieve()
                        .body(byte[].class);

        if (response == null) {
            throw new IllegalStateException(
                    "Runtime API returned an empty file content response."
            );
        }

        return response;
    }

    /**
     * OCR 대상 이미지 업로드
     *
     * API:
     * POST /api/v1/pmis/uploads
     *
     * Content-Type:
     * multipart/form-data
     *
     * @param fileName 업로드할 파일명
     * @param mediaType 이미지 MIME 타입 (image/png 또는 image/jpeg)
     * @param fileContent 이미지 바이너리
     * @return 업로드 결과
     */
    public RuntimeOcrUploadResponse uploadImage(
            String fileName,
            MediaType mediaType,
            byte[] fileContent
    ) {
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();

        ByteArrayResource fileResource =
                new ByteArrayResource(fileContent) {
                    @Override
                    public String getFilename() {
                        return fileName;
                    }
                };

        bodyBuilder.part("file", fileResource)
                .contentType(mediaType)
                .filename(fileName);

        RuntimeOcrUploadResponse response =
                createRestClient()
                        .post()
                        .uri("/api/v1/pmis/uploads")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .body(bodyBuilder.build())
                        .retrieve()
                        .body(RuntimeOcrUploadResponse.class);

        if (response == null) {
            throw new IllegalStateException(
                    "Runtime API returned an empty OCR upload response."
            );
        }

        return response;
    }

    /**
     * OCR 작업 생성
     *
     * API:
     * POST /api/v1/pmis/ocr/jobs
     *
     * @param request OCR 작업 요청 정보
     * @return OCR 작업 생성 결과
     */
    public RuntimeOcrJobResponse createOcrJob(
            RuntimeOcrJobRequest request
    ) {
        RuntimeOcrJobResponse response =
                createRestClient()
                        .post()
                        .uri("/api/v1/pmis/ocr/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .body(request)
                        .retrieve()
                        .body(RuntimeOcrJobResponse.class);

        if (response == null) {
            throw new IllegalStateException(
                    "Runtime API returned an empty OCR job response."
            );
        }

        return response;
    }

    /**
     * OCR 작업 상태 조회
     *
     * API:
     * GET /api/v1/pmis/ocr/jobs/{ocr_request_id}
     *
     * @param ocrRequestId Runtime OCR 요청 ID
     * @return OCR 작업 상태
     */
    public RuntimeOcrJobStatusResponse getOcrJobStatus(
            String ocrRequestId
    ) {
        RuntimeOcrJobStatusResponse response =
                createRestClient()
                        .get()
                        .uri(
                                "/api/v1/pmis/ocr/jobs/{ocrRequestId}",
                                ocrRequestId
                        )
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .body(RuntimeOcrJobStatusResponse.class);

        if (response == null) {
            throw new IllegalStateException(
                    "Runtime API returned an empty OCR job status response."
            );
        }

        return response;
    }

    /**
     * OCR 결과 조회
     *
     * API:
     * GET /api/v1/pmis/ocr/jobs/{ocr_request_id}/result
     *
     * @param ocrRequestId Runtime OCR 요청 ID
     * @return OCR 결과
     */
    public RuntimeOcrResultResponse getOcrResult(
            String ocrRequestId
    ) {
        RuntimeOcrResultResponse response =
                createRestClient()
                        .get()
                        .uri(
                                "/api/v1/pmis/ocr/jobs/{ocrRequestId}/result",
                                ocrRequestId
                        )
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .body(RuntimeOcrResultResponse.class);

        if (response == null) {
            throw new IllegalStateException(
                    "Runtime API returned an empty OCR result response."
            );
        }

        return response;
    }

    /**
     * Runtime API 호출용 RestClient 생성
     *
     * application.yaml의 다음 설정을 사용한다.
     * - runtime.api.base-url
     * - runtime.api.api-key
     *
     * X-API-Key 헤더를 통해 Runtime API 인증을 수행한다.
     */
    private RestClient createRestClient() {
        RestClient.Builder builder =
                restClientBuilder
                        .baseUrl(runtimeProperties.getBaseUrl());

        String apiKey = runtimeProperties.getApiKey();

        if (apiKey != null && !apiKey.isBlank()) {
            builder.defaultHeader("X-API-Key", apiKey);
        }

        return builder.build();
    }
}