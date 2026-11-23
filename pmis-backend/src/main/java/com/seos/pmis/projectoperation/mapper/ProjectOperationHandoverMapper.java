
package com.seos.pmis.projectoperation.mapper;

import com.seos.pmis.company.entity.Company;
import com.seos.pmis.company.entity.Organization;
import com.seos.pmis.operation.entity.Operation;
import com.seos.pmis.operation.entity.OperationParticipant;
import com.seos.pmis.project.entity.Project;
import com.seos.pmis.projectoperation.dto.response.ProjectOperationHandoverResponse;
import com.seos.pmis.projectoperation.entity.ProjectOperation;
import com.seos.pmis.projectoperation.entity.ProjectOperationHandover;
import com.seos.pmis.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class ProjectOperationHandoverMapper {

    public ProjectOperationHandoverResponse toResponse(
            ProjectOperationHandover handover) {

        ProjectOperation projectOperation = handover.getProjectOperation();
        Project project = projectOperation.getProject();
        Operation operation = handover.getOperation();

        return ProjectOperationHandoverResponse.builder()
                .id(handover.getId())
                .handoverType(handover.getHandoverType())
                .status(handover.getStatus())
                .sequenceNo(handover.getSequenceNo())
                .projectOperation(toProjectOperationSummary(projectOperation))
                .project(toProjectSummary(project))
                .operation(toOperationSummary(operation))
                .previousHandoverId(
                        handover.getPreviousHandover() != null
                                ? handover.getPreviousHandover().getId()
                                : null)
                .previousSequenceNo(
                        handover.getPreviousHandover() != null
                                ? handover.getPreviousHandover().getSequenceNo()
                                : null)
                .previousHandoverType(
                        handover.getPreviousHandover() != null
                                ? handover.getPreviousHandover().getHandoverType()
                                : null)
                .fromParticipant(
                        toParticipantSummary(handover.getFromParticipant()))
                .toParticipant(
                        toParticipantSummary(handover.getToParticipant()))
                .handedOverByUserId(
                        toUserId(handover.getHandedOverBy()))
                .handedOverByName(
                        toUserName(handover.getHandedOverBy()))
                .acceptedByUserId(
                        toUserId(handover.getAcceptedBy()))
                .acceptedByName(
                        toUserName(handover.getAcceptedBy()))
                .plannedHandoverDate(handover.getPlannedHandoverDate())
                .requestedAt(handover.getRequestedAt())
                .acceptedAt(handover.getAcceptedAt())
                .completedAt(handover.getCompletedAt())
                .notes(handover.getNotes())
                .createdAt(handover.getCreatedAt())
                .updatedAt(handover.getUpdatedAt())
                .build();
    }

    private ProjectOperationHandoverResponse.ProjectOperationSummary
    toProjectOperationSummary(ProjectOperation projectOperation) {

        return ProjectOperationHandoverResponse.ProjectOperationSummary
                .builder()
                .id(projectOperation.getId())
                .relationshipType(projectOperation.getRelationshipType())
                .status(projectOperation.getStatus())
                .build();
    }

    private ProjectOperationHandoverResponse.ProjectSummary
    toProjectSummary(Project project) {

        return ProjectOperationHandoverResponse.ProjectSummary.builder()
                .id(project.getId())
                .projectCode(project.getProjectCode())
                .projectName(project.getProjectName())
                .customerName(project.getCustomerName())
                .projectManager(project.getProjectManager())
                .build();
    }

    private ProjectOperationHandoverResponse.OperationSummary
    toOperationSummary(Operation operation) {

        return ProjectOperationHandoverResponse.OperationSummary.builder()
                .id(operation.getId())
                .operationCode(operation.getOperationCode())
                .operationName(operation.getOperationName())
                .operationType(operation.getOperationType())
                .status(operation.getStatus())
                .build();
    }

    private ProjectOperationHandoverResponse.OperationParticipantSummary
    toParticipantSummary(OperationParticipant participant) {

        if (participant == null) {
            return null;
        }

        Company company = participant.getCompany();
        Organization organization = participant.getOrganization();

        return ProjectOperationHandoverResponse.OperationParticipantSummary
                .builder()
                .id(participant.getId())
                .role(participant.getParticipantRole())
                .primary(participant.isPrimary())
                .company(
                        company != null
                                ? ProjectOperationHandoverResponse.CompanySummary
                                        .builder()
                                        .id(company.getId())
                                        .companyCode(company.getCompanyCode())
                                        .companyName(company.getCompanyName())
                                        .build()
                                : null)
                .organization(
                        toOrganizationSummary(organization))
                .build();
    }

    private ProjectOperationHandoverResponse.OrganizationSummary
    toOrganizationSummary(Organization organization) {

        if (organization == null) {
            return null;
        }

        return ProjectOperationHandoverResponse.OrganizationSummary.builder()
                .id(organization.getId())
                .organizationCode(organization.getOrganizationCode())
                .organizationName(organization.getOrganizationName())
                .build();
    }

    private Long toUserId(User user) {
        return user != null ? user.getId() : null;
    }

    private String toUserName(User user) {
        return user != null ? user.getName() : null;
    }
}
