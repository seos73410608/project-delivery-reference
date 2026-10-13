package com.seos.pmis.integration.runtime.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Automation Runtime 출력 파일 메타데이터 응답 DTO
 *
 * Runtime API 응답을 PMIS에서 매핑하기 위해 사용한다.
 *
 * 대상 API:
 * GET /api/v1/executions/{execution_uuid}/files
 * GET /api/v1/files/{file_id}
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RuntimeOutputFileResponse {

    /**
     * Runtime 파일 ID
     */
    @JsonProperty("file_id")
    private String fileId;

    /**
     * Runtime Job ID
     */
    @JsonProperty("job_id")
    private String jobId;

    /**
     * Runtime 실행 UUID
     */
    @JsonProperty("execution_uuid")
    private String executionUuid;

    /**
     * 파일 제공 업체 또는 생성 주체 이름
     */
    @JsonProperty("vendor_name")
    private String vendorName;

    /**
     * 파일 유형
     */
    @JsonProperty("file_type")
    private String fileType;

    /**
     * 파일 생성 시각
     */
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}