package com.seos.pmis.integration.runtime.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RuntimeOcrJobRequest {

    @JsonProperty("pmis_request_id")
    private String pmisRequestId;

    @JsonProperty("file_id")
    private String fileId;
}
