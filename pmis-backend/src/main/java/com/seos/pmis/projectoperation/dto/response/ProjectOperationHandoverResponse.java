package com.seos.pmis.projectoperation.dto.response;

import com.seos.pmis.operation.entity.OperationStatus;
import com.seos.pmis.operation.entity.OperationType;
import com.seos.pmis.operation.entity.OperationParticipantRole;
import com.seos.pmis.projectoperation.entity.ProjectOperationHandoverStatus;
import com.seos.pmis.projectoperation.entity.ProjectOperationHandoverType;
import com.seos.pmis.projectoperation.entity.ProjectOperationRelationshipType;
import com.seos.pmis.projectoperation.entity.ProjectOperationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class ProjectOperationHandoverResponse {

    private Long id;
    private ProjectOperationHandoverType handoverType;
    private ProjectOperationHandoverStatus status;
    private Integer sequenceNo;

    private ProjectOperationSummary projectOperation;
    private ProjectSummary project;
    private OperationSummary operation;

    private Long previousHandoverId;
    private Integer previousSequenceNo;
    private ProjectOperationHandoverType previousHandoverType;

    private OperationParticipantSummary fromParticipant;
    private OperationParticipantSummary toParticipant;

    private Long handedOverByUserId;
    private String handedOverByName;

    private Long acceptedByUserId;
    private String acceptedByName;

    private LocalDate plannedHandoverDate;
    private LocalDateTime requestedAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime completedAt;
    private String notes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @Builder
    public static class ProjectOperationSummary {
        private Long id;
        private ProjectOperationRelationshipType relationshipType;
        private ProjectOperationStatus status;
    }

    @Getter
    @Builder
    public static class ProjectSummary {
        private Long id;
        private String projectCode;
        private String projectName;
        private String customerName;
        private String projectManager;
    }

    @Getter
    @Builder
    public static class OperationSummary {
        private Long id;
        private String operationCode;
        private String operationName;
        private OperationType operationType;
        private OperationStatus status;
    }

    @Getter
    @Builder
    public static class OperationParticipantSummary {
        private Long id;
        private OperationParticipantRole role;
        private boolean primary;
        private CompanySummary company;
        private OrganizationSummary organization;
    }

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