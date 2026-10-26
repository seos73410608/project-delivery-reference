package com.seos.pmis.integration.runtime.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RuntimeOcrJobByRequestIdResponse {

    @JsonProperty("pmis_request_id")
    private String pmisRequestId;

    @JsonProperty("ocr_request_id")
    private String ocrRequestId;

    @JsonProperty("file_id")
    private String fileId;

    private String status;

    @JsonProperty("created_at")
    private OffsetDateTime createdAt;

    @JsonProperty("updated_at")
    private OffsetDateTime updatedAt;

    private JsonNode error;
}
