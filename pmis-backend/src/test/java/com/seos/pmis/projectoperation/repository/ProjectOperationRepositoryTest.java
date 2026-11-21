
package com.seos.pmis.projectoperation.repository;

import com.seos.pmis.config.jpa.JpaConfig;
import com.seos.pmis.operation.entity.Operation;
import com.seos.pmis.operation.entity.OperationStatus;
import com.seos.pmis.operation.entity.OperationType;
import com.seos.pmis.operation.repository.OperationRepository;
import com.seos.pmis.project.entity.Project;
import com.seos.pmis.project.entity.ProjectPriority;
import com.seos.pmis.project.entity.ProjectStatus;
import com.seos.pmis.project.repository.ProjectRepository;
import com.seos.pmis.projectoperation.entity.ProjectOperation;
import com.seos.pmis.projectoperation.entity.ProjectOperationRelationshipType;
import com.seos.pmis.projectoperation.entity.ProjectOperationStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaConfig.class)
class ProjectOperationRepositoryTest {

    @Autowired
    private ProjectOperationRepository projectOperationRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private OperationRepository operationRepository;

    @Test
    void projectOperationQueries_returnExpectedRelationships() {
        // given
        Project project1 = projectRepository.saveAndFlush(
                Project.builder()
                        .projectCode("TEST-PO-000001")
                        .projectName("Test Project 1")
                        .customerName("Test Customer")
                        .projectManager("Test Manager")
                        .startDate(LocalDate.of(2026, 1, 1))
                        .endDate(LocalDate.of(2026, 12, 31))
                        .status(ProjectStatus.IN_PROGRESS)
                        .priority(ProjectPriority.MEDIUM)
                        .build()
        );

        Project project2 = projectRepository.saveAndFlush(
                Project.builder()
                        .projectCode("TEST-PO-000002")
                        .projectName("Test Project 2")
                        .customerName("Test Customer")
                        .projectManager("Test Manager")
                        .startDate(LocalDate.of(2026, 1, 1))
                        .endDate(LocalDate.of(2026, 12, 31))
                        .status(ProjectStatus.PLANNING)
                        .priority(ProjectPriority.HIGH)
                        .build()
        );

        Operation operation1 = operationRepository.saveAndFlush(
                Operation.builder()
                        .operationCode("TEST-PO-OP-000001")
                        .operationName("Test Operation 1")
                        .operationType(OperationType.APPLICATION_SERVICE)
                        .status(OperationStatus.ACTIVE)
                        .build()
        );

        Operation operation2 = operationRepository.saveAndFlush(
                Operation.builder()
                        .operationCode("TEST-PO-OP-000002")
                        .operationName("Test Operation 2")
                        .operationType(OperationType.DATABASE_SERVICE)
                        .status(OperationStatus.PLANNED)
                        .build()
        );

        ProjectOperation relationship1 = ProjectOperation.builder()
                .project(project1)
                .operation(operation1)
                .relationshipType(ProjectOperationRelationshipType.BUILD_RESULT)
                .status(ProjectOperationStatus.ACTIVE)
                .build();

        ProjectOperation relationship2 = ProjectOperation.builder()
                .project(project1)
                .operation(operation2)
                .relationshipType(ProjectOperationRelationshipType.MIGRATION)
                .status(ProjectOperationStatus.PLANNED)
                .build();

        ProjectOperation relationship3 = ProjectOperation.builder()
                .project(project2)
                .operation(operation1)
                .relationshipType(ProjectOperationRelationshipType.ENHANCEMENT)
                .status(ProjectOperationStatus.COMPLETED)
                .build();

        projectOperationRepository.saveAllAndFlush(
                java.util.List.of(relationship1, relationship2, relationship3)
        );

        // when & then
        assertThat(projectOperationRepository
                .findAllByProject_Id(project1.getId()))
                .hasSize(2);

        assertThat(projectOperationRepository
                .findAllByOperation_Id(operation1.getId()))
                .hasSize(2);

        assertThat(projectOperationRepository
                .findByProject_IdAndOperation_Id(
                        project1.getId(),
                        operation1.getId()
                ))
                .isPresent()
                .get()
                .extracting(ProjectOperation::getRelationshipType)
                .isEqualTo(ProjectOperationRelationshipType.BUILD_RESULT);

        assertThat(projectOperationRepository
                .findAllByProject_IdAndStatus(
                        project1.getId(),
                        ProjectOperationStatus.ACTIVE
                ))
                .hasSize(1);

        assertThat(projectOperationRepository
                .findAllByOperation_IdAndStatus(
                        operation1.getId(),
                        ProjectOperationStatus.COMPLETED
                ))
                .hasSize(1);

        assertThat(projectOperationRepository
                .existsByProject_IdAndOperation_IdAndStatus(
                        project1.getId(),
                        operation1.getId(),
                        ProjectOperationStatus.ACTIVE
                ))
                .isTrue();

        assertThat(projectOperationRepository
                .existsByProject_IdAndOperation_IdAndStatus(
                        project1.getId(),
                        operation2.getId(),
                        ProjectOperationStatus.COMPLETED
                ))
                .isFalse();
    }
}
