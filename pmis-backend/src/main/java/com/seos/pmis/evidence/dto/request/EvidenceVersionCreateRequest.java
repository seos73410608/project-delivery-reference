package com.seos.pmis.evidence.dto.request;

import com.seos.pmis.evidence.enums.EvidenceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EvidenceVersionCreateRequest {

    @NotNull(message = "Evidence 유형은 필수입니다.")
    private EvidenceType evidenceType;

    @NotBlank(message = "Evidence 제목은 필수입니다.")
    @Size(max = 200, message = "Evidence 제목은 200자 이하여야 합니다.")
    private String title;

    private String description;

    @Size(max = 255, message = "파일명은 255자 이하여야 합니다.")
    private String fileName;

    @Size(max = 1000, message = "파일 경로는 1000자 이하여야 합니다.")
    private String filePath;

    @Size(max = 64, message = "Runtime 파일 ID는 64자 이하여야 합니다.")
    private String sourceFileId;

    @Size(max = 64, message = "Runtime 실행 UUID는 64자 이하여야 합니다.")
    private String executionUuid;

    @Size(max = 64, message = "Runtime Job ID는 64자 이하여야 합니다.")
    private String sourceJobId;
}