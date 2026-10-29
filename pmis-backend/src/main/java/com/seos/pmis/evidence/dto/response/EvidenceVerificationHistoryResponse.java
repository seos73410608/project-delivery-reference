package com.seos.pmis.evidence.dto.response;

import com.seos.pmis.evidence.enums.VerificationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class EvidenceVerificationHistoryResponse {

    private Long id;

    private Long evidenceId;

    private Integer versionNumber;

    private VerificationStatus verificationStatus;

    private Long verifiedById;

    private LocalDateTime verifiedAt;

    private String remark;
}