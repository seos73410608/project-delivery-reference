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
public class RuntimeOcrUploadResponse {

    @JsonProperty("file_id")
    private String fileId;

    @JsonProperty("file_name")
    private String fileName;

    @JsonProperty("media_type")
    private String mediaType;

    @JsonProperty("size_bytes")
    private long sizeBytes;

    private String status;

    @JsonProperty("created_at")
    private OffsetDateTime createdAt;
}
