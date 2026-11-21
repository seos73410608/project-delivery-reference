
package com.seos.pmis.projectoperation.service;

import com.seos.common.exception.BusinessException;
import com.seos.pmis.operation.entity.Operation;
import com.seos.pmis.operation.repository.OperationRepository;
import com.seos.pmis.project.entity.Project;
import com.seos.pmis.project.repository.ProjectRepository;
import com.seos.pmis.projectoperation.entity.ProjectOperation;
import com.seos.pmis.projectoperation.entity.ProjectOperationRelationshipType;
import com.seos.pmis.projectoperation.entity.ProjectOperationStatus;
import com.seos.pmis.projectoperation.exception.ProjectOperationErrorCode;
import com.seos.pmis.projectoperation.repository.ProjectOperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectOperationService {

    private final ProjectOperationRepository projectOperationRepository;
    private final ProjectRepository projectRepository;
    private final OperationRepository operationRepository;

    /**
     * 프로젝트와 운영 서비스 연결
     */
    @Transactional
    public ProjectOperation create(
            Long projectId,
            Long operationId,
            ProjectOperationRelationshipType relationshipType,
            LocalDate effectiveFrom,
            LocalDate effectiveTo,
            String description) {
        validateRelationshipType(relationshipType);
        validatePeriod(effectiveFrom, effectiveTo);

        Project project = findProject(projectId);
        Operation operation = findOperation(operationId);

        validateDuplicateRelationship(projectId, operationId);

        ProjectOperation projectOperation = ProjectOperation.builder()
                .project(project)
                .operation(operation)
                .relationshipType(relationshipType)
                .status(ProjectOperationStatus.PLANNED)
                .effectiveFrom(effectiveFrom)
                .effectiveTo(effectiveTo)
                .description(description)
                .build();

        return projectOperationRepository.save(projectOperation);
    }

    /**
     * 프로젝트 운영 관계 정보 수정
     */
    @Transactional
    public ProjectOperation update(
            Long projectOperationId,
            ProjectOperationRelationshipType relationshipType,
            LocalDate effectiveFrom,
            LocalDate effectiveTo,
            String description) {
        ProjectOperation projectOperation = findProjectOperation(projectOperationId);

        validateRelationshipType(relationshipType);
        validatePeriod(effectiveFrom, effectiveTo);

        projectOperation.update(
                relationshipType,
                effectiveFrom,
                effectiveTo,
                description);

        return projectOperation;
    }

    /**
     * 프로젝트 운영 관계 상태 변경
     */
    @Transactional
    public ProjectOperation changeStatus(
            Long projectOperationId,
            ProjectOperationStatus status) {
        ProjectOperation projectOperation = findProjectOperation(projectOperationId);

        validateStatusTransition(projectOperation.getStatus(), status);

        projectOperation.changeStatus(status);

        return projectOperation;
    }

    /**
     * 프로젝트 운영 관계 단건 조회
     */
    public ProjectOperation getDetail(Long projectOperationId) {
        return findProjectOperation(projectOperationId);
    }

    /**
     * 프로젝트에 연결된 운영 관계 조회
     */
    public List<ProjectOperation> findByProject(Long projectId) {
        findProject(projectId);

        return projectOperationRepository.findAllByProject_Id(projectId);
    }

    /**
     * 운영 서비스에 연결된 프로젝트 관계 조회
     */
    public List<ProjectOperation> findByOperation(Long operationId) {
        findOperation(operationId);

        return projectOperationRepository.findAllByOperation_Id(operationId);
    }

    /**
     * 프로젝트 조회
     */
    private Project findProject(Long projectId) {
        if (projectId == null) {
            throw new BusinessException(
                    ProjectOperationErrorCode.PROJECT_NOT_FOUND);
        }

        return projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException(
                        ProjectOperationErrorCode.PROJECT_NOT_FOUND));
    }

    /**
     * 운영 서비스 조회
     */
    private Operation findOperation(Long operationId) {
        if (operationId == null) {
            throw new BusinessException(
                    ProjectOperationErrorCode.OPERATION_NOT_FOUND);
        }

        return operationRepository.findById(operationId)
                .orElseThrow(() -> new BusinessException(
                        ProjectOperationErrorCode.OPERATION_NOT_FOUND));
    }

    /**
     * 프로젝트 운영 관계 조회
     */
    private ProjectOperation findProjectOperation(Long projectOperationId) {
        if (projectOperationId == null) {
            throw new BusinessException(
                    ProjectOperationErrorCode.PROJECT_OPERATION_NOT_FOUND);
        }

        return projectOperationRepository.findById(projectOperationId)
                .orElseThrow(() -> new BusinessException(
                        ProjectOperationErrorCode.PROJECT_OPERATION_NOT_FOUND));
    }

    /**
     * 활성 프로젝트 운영 관계 중복 검증
     */
    private void validateDuplicateRelationship(
            Long projectId,
            Long operationId) {
        boolean plannedExists = projectOperationRepository.existsByProject_IdAndOperation_IdAndStatus(
                projectId,
                operationId,
                ProjectOperationStatus.PLANNED);

        boolean activeExists = projectOperationRepository.existsByProject_IdAndOperation_IdAndStatus(
                projectId,
                operationId,
                ProjectOperationStatus.ACTIVE);

        if (plannedExists || activeExists) {
            throw new BusinessException(
                    ProjectOperationErrorCode.PROJECT_OPERATION_DUPLICATE);
        }
    }

    /**
     * 관계 유형 필수 검증
     */
    private void validateRelationshipType(
            ProjectOperationRelationshipType relationshipType) {
        if (relationshipType == null) {
            throw new BusinessException(
                    ProjectOperationErrorCode.PROJECT_OPERATION_RELATIONSHIP_TYPE_INVALID);
        }
    }

    /**
     * 관계 상태 전이 검증
     */
    private void validateStatusTransition(
            ProjectOperationStatus currentStatus,
            ProjectOperationStatus newStatus) {
        if (newStatus == null) {
            throw new BusinessException(
                    ProjectOperationErrorCode.PROJECT_OPERATION_STATUS_INVALID);
        }

        boolean validTransition = switch (currentStatus) {
            case PLANNED ->
                newStatus == ProjectOperationStatus.ACTIVE
                        || newStatus == ProjectOperationStatus.CANCELLED;

            case ACTIVE ->
                newStatus == ProjectOperationStatus.COMPLETED
                        || newStatus == ProjectOperationStatus.CANCELLED;

            case COMPLETED, CANCELLED -> false;
        };

        if (!validTransition) {
            throw new BusinessException(
                    ProjectOperationErrorCode.PROJECT_OPERATION_STATUS_INVALID);
        }
    }

    /**
     * 관계 기간 검증
     */
    private void validatePeriod(
            LocalDate effectiveFrom,
            LocalDate effectiveTo) {
        if (effectiveTo != null
                && (effectiveFrom == null
                        || effectiveFrom.isAfter(effectiveTo))) {
            throw new BusinessException(
                    ProjectOperationErrorCode.PROJECT_OPERATION_PERIOD_INVALID);
        }
    }
}
