package com.seos.pmis.integration.runtime.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RuntimeOcrResultResponse {

    @JsonProperty("ocr_request_id")
    private String ocrRequestId;

    @JsonProperty("pmis_request_id")
    private String pmisRequestId;

    @JsonProperty("file_id")
    private String fileId;

    private String status;

    private String text;

    private Engine engine;

    private List<String> warnings;

    @JsonProperty("created_at")
    private OffsetDateTime createdAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Engine {

        private String name;

        private String version;
    }
}
