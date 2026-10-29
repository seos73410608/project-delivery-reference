package com.seos.pmis.evidence.dto.response;

import com.seos.pmis.evidence.enums.EvidenceType;
import com.seos.pmis.evidence.enums.VerificationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Evidence Version Response
 *
 * Evidence 버전별 상세 정보를 반환한다.
 */
@Getter
@Builder
public class EvidenceVersionResponse {

    private Long id;
    private Long evidenceId;
    private Integer versionNumber;

    private EvidenceType evidenceType;
    private String title;
    private String description;

    private String fileName;
    private String filePath;

    private String sourceFileId;
    private String executionUuid;
    private String sourceJobId;

    private Long submittedById;
    private LocalDateTime submittedAt;

    private VerificationStatus verificationStatus;
    private Long verifiedById;
    private LocalDateTime verifiedAt;
    private String verificationRemark;

}