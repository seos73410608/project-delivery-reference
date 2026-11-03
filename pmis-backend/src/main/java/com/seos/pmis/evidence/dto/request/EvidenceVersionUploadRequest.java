
package com.seos.pmis.evidence.dto.request;

import com.seos.pmis.evidence.enums.EvidenceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Evidence 파일 업로드 요청 DTO
 *
 * Multipart 파일 자체는 Controller에서 MultipartFile로 전달받고,
 * 이 DTO는 Evidence Version 생성에 필요한 정보를 전달한다.
 */
@Getter
@Setter
@NoArgsConstructor
public class EvidenceVersionUploadRequest {

    @NotNull(message = "Evidence 유형은 필수입니다.")
    private EvidenceType evidenceType;

    @NotBlank(message = "Evidence 제목은 필수입니다.")
    @Size(max = 200, message = "Evidence 제목은 200자 이하여야 합니다.")
    private String title;

    private String description;
}
