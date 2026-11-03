package com.seos.pmis.integration.runtime.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Getter
@NoArgsConstructor
public class RuntimePmisFileResponse {

    @JsonProperty("file_id")
    private String fileId;

    @JsonProperty("original_filename")
    private String originalFilename;

    @JsonProperty("content_type")
    private String contentType;

    @JsonProperty("file_size")
    private Long fileSize;

    @JsonProperty("sha256")
    private String sha256;

    @JsonProperty("created_at")
    private OffsetDateTime createdAt;
}