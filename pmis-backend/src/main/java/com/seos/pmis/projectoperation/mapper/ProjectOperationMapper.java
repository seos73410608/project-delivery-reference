package com.seos.pmis.projectoperation.mapper;

import com.seos.pmis.operation.entity.Operation;
import com.seos.pmis.project.entity.Project;
import com.seos.pmis.projectoperation.dto.response.ProjectOperationResponse;
import com.seos.pmis.projectoperation.entity.ProjectOperation;
import org.springframework.stereotype.Component;

@Component
public class ProjectOperationMapper {

    public ProjectOperationResponse toResponse(ProjectOperation projectOperation) {
        Project project = projectOperation.getProject();
        Operation operation = projectOperation.getOperation();

        return ProjectOperationResponse.builder()
                // 프로젝트 운영 관계 정보
                .id(projectOperation.getId())
                .relationshipType(projectOperation.getRelationshipType())
                .status(projectOperation.getStatus())
                .effectiveFrom(projectOperation.getEffectiveFrom())
                .effectiveTo(projectOperation.getEffectiveTo())
                .description(projectOperation.getDescription())
                .createdAt(projectOperation.getCreatedAt())
                .updatedAt(projectOperation.getUpdatedAt())

                // 프로젝트 정보
                .projectId(project.getId())
                .projectCode(project.getProjectCode())
                .projectName(project.getProjectName())
                .customerName(project.getCustomerName())
                .projectManager(project.getProjectManager())

                // 운영 서비스 정보
                .operationId(operation.getId())
                .operationCode(operation.getOperationCode())
                .operationName(operation.getOperationName())
                .operationType(operation.getOperationType())
                .operationStatus(operation.getStatus())

                .build();
    }
}