package com.seos.pmis.operation.dto.response;

import com.seos.pmis.operation.entity.OperationParticipantRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OperationParticipantResponse {

    private Long id;

    private OperationParticipantRole role;

    private boolean primary;

    private CompanySummary company;

    private OrganizationSummary organization;

    @Getter
    @Builder
    public static class CompanySummary {

        private Long id;

        private String companyCode;

        private String companyName;
    }

    @Getter
    @Builder
    public static class OrganizationSummary {

        private Long id;

        private String organizationCode;

        private String organizationName;
    }
}