package com.seos.pmis.integration.runtime.client;

import com.seos.pmis.integration.runtime.config.RuntimeProperties;
import com.seos.pmis.integration.runtime.dto.RuntimeOutputFileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
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
 *
 * 업무 규칙과 증적 등록 처리는 EvidenceService에서 담당한다.
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
        RestClient restClient = createRestClient();

        List<RuntimeOutputFileResponse> response =
                restClient.get()
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
     * Runtime API 호출용 RestClient 생성
     *
     * application.yaml의 runtime.api.base-url을 사용한다.
     */
    private RestClient createRestClient() {
        return restClientBuilder
                .baseUrl(runtimeProperties.getBaseUrl())
                .build();
    }
}