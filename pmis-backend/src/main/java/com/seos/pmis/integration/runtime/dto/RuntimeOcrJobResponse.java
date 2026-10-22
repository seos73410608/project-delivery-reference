package com.seos.pmis.integration.runtime.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RuntimeOcrJobResponse {

    @JsonProperty("ocr_request_id")
    private String ocrRequestId;

    @JsonProperty("pmis_request_id")
    private String pmisRequestId;

    @JsonProperty("file_id")
    private String fileId;

    private String status;

    private Boolean accepted;

    @JsonProperty("created_at")
    private OffsetDateTime createdAt;

    @JsonProperty("status_url")
    private String statusUrl;

    @JsonProperty("result_url")
    private String resultUrl;
}
