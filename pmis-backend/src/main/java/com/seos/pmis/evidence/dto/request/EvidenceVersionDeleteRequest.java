
package com.seos.pmis.evidence.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EvidenceVersionDeleteRequest {

    @NotBlank(message = "삭제 사유는 필수입니다.")
    @Size(
            max = 1000,
            message = "삭제 사유는 1000자 이하여야 합니다."
    )
    private String reason;
}
