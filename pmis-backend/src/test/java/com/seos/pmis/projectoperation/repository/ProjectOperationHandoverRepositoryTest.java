
package com.seos.pmis.projectoperation.repository;

import com.seos.pmis.company.entity.Company;
import com.seos.pmis.company.repository.CompanyRepository;
import com.seos.pmis.config.jpa.JpaConfig;
import com.seos.pmis.operation.entity.Operation;
import com.seos.pmis.operation.entity.OperationParticipant;
import com.seos.pmis.operation.entity.OperationParticipantRole;
import com.seos.pmis.operation.entity.OperationStatus;
import com.seos.pmis.operation.entity.OperationType;
import com.seos.pmis.operation.repository.OperationParticipantRepository;
import com.seos.pmis.operation.repository.OperationRepository;
import com.seos.pmis.project.entity.Project;
import com.seos.pmis.project.entity.ProjectPriority;
import com.seos.pmis.project.entity.ProjectStatus;
import com.seos.pmis.project.repository.ProjectRepository;
import com.seos.pmis.projectoperation.entity.ProjectOperation;
import com.seos.pmis.projectoperation.entity.ProjectOperationHandover;
import com.seos.pmis.projectoperation.entity.ProjectOperationHandoverStatus;
import com.seos.pmis.projectoperation.entity.ProjectOperationHandoverType;
import com.seos.pmis.projectoperation.entity.ProjectOperationRelationshipType;
import com.seos.pmis.projectoperation.entity.ProjectOperationStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaConfig.class)
class ProjectOperationHandoverRepositoryTest {

    @Autowired
    private ProjectOperationHandoverRepository handoverRepository;

    @Autowired
    private ProjectOperationRepository projectOperationRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private OperationRepository operationRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private OperationParticipantRepository participantRepository;

    @Test
    void handoverQueries_returnExpectedHistory() {
        // given
        Project project = projectRepository.saveAndFlush(
                Project.builder()
                        .projectCode("TEST-POH-000001")
                        .projectName("Handover Test Project")
                        .customerName("Test Customer")
                        .projectManager("Test Manager")
                        .startDate(LocalDate.of(2026, 1, 1))
                        .endDate(LocalDate.of(2026, 12, 31))
                        .status(ProjectStatus.IN_PROGRESS)
                        .priority(ProjectPriority.MEDIUM)
                        .build()
        );

        Operation operation = operationRepository.saveAndFlush(
                Operation.builder()
                        .operationCode("TEST-POH-OP-000001")
                        .operationName("Handover Test Operation")
                        .operationType(OperationType.APPLICATION_SERVICE)
                        .status(OperationStatus.ACTIVE)
                        .build()
        );

        Company company1 = companyRepository.saveAndFlush(
                Company.builder()
                        .companyCode("TEST-POH-COMP-000001")
                        .companyName("Original Operator")
                        .build()
        );

        Company company2 = companyRepository.saveAndFlush(
                Company.builder()
                        .companyCode("TEST-POH-COMP-000002")
                        .companyName("New Operator")
                        .build()
        );

        OperationParticipant originalOperator =
                participantRepository.saveAndFlush(
                        OperationParticipant.builder()
                                .operation(operation)
                                .company(company1)
                                .participantRole(OperationParticipantRole.OPERATOR)
                                .primary(true)
                                .effectiveFrom(LocalDate.of(2026, 1, 1))
                                .build()
                );

        OperationParticipant newOperator =
                participantRepository.saveAndFlush(
                        OperationParticipant.builder()
                                .operation(operation)
                                .company(company2)
                                .participantRole(OperationParticipantRole.SERVICE_PROVIDER)
                                .primary(true)
                                .effectiveFrom(LocalDate.of(2026, 11, 1))
                                .build()
                );

        ProjectOperation projectOperation =
                projectOperationRepository.saveAndFlush(
                        ProjectOperation.builder()
                                .project(project)
                                .operation(operation)
                                .relationshipType(ProjectOperationRelationshipType.BUILD_RESULT)
                                .status(ProjectOperationStatus.ACTIVE)
                                .build()
                );

        ProjectOperationHandover initialHandover =
                handoverRepository.saveAndFlush(
                        ProjectOperationHandover.builder()
                                .projectOperation(projectOperation)
                                .operation(operation)
                                .handoverType(ProjectOperationHandoverType.INITIAL)
                                .status(ProjectOperationHandoverStatus.DRAFT)
                                .sequenceNo(1)
                                .toParticipant(originalOperator)
                                .notes("Initial operation handover")
                                .build()
                );

        ProjectOperationHandover operatorChange =
                handoverRepository.saveAndFlush(
                        ProjectOperationHandover.builder()
                                .projectOperation(projectOperation)
                                .operation(operation)
                                .handoverType(ProjectOperationHandoverType.OPERATOR_CHANGE)
                                .status(ProjectOperationHandoverStatus.DRAFT)
                                .sequenceNo(2)
                                .previousHandover(initialHandover)
                                .fromParticipant(originalOperator)
                                .toParticipant(newOperator)
                                .notes("Operator change handover")
                                .build()
                );

        // when & then
        List<ProjectOperationHandover> history =
                handoverRepository
                        .findAllByProjectOperation_IdOrderBySequenceNoAsc(
                                projectOperation.getId()
                        );

        assertThat(history)
                .hasSize(2)
                .extracting(ProjectOperationHandover::getSequenceNo)
                .containsExactly(1, 2);

        assertThat(handoverRepository
                .findByProjectOperation_IdAndSequenceNo(
                        projectOperation.getId(),
                        2
                ))
                .isPresent()
                .get()
                .extracting(ProjectOperationHandover::getHandoverType)
                .isEqualTo(ProjectOperationHandoverType.OPERATOR_CHANGE);

        assertThat(handoverRepository
                .findFirstByProjectOperation_IdOrderBySequenceNoDesc(
                        projectOperation.getId()
                ))
                .isPresent()
                .get()
                .extracting(ProjectOperationHandover::getId)
                .isEqualTo(operatorChange.getId());

        assertThat(handoverRepository
                .findAllByProjectOperation_IdAndStatus(
                        projectOperation.getId(),
                        ProjectOperationHandoverStatus.DRAFT
                ))
                .hasSize(2);

        assertThat(handoverRepository
                .existsByProjectOperation_IdAndStatusIn(
                        projectOperation.getId(),
                        List.of(
                                ProjectOperationHandoverStatus.DRAFT,
                                ProjectOperationHandoverStatus.REQUESTED
                        )
                ))
                .isTrue();

        assertThat(handoverRepository
                .existsByProjectOperation_IdAndStatusIn(
                        projectOperation.getId(),
                        List.of(ProjectOperationHandoverStatus.COMPLETED)
                ))
                .isFalse();

        assertThat(handoverRepository
                .findAllByOperation_IdOrderByCreatedAtDesc(operation.getId()))
                .hasSize(2);

        assertThat(operatorChange.getPreviousHandover().getId())
                .isEqualTo(initialHandover.getId());

        assertThat(operatorChange.getFromParticipant().getId())
                .isEqualTo(originalOperator.getId());

        assertThat(operatorChange.getToParticipant().getId())
                .isEqualTo(newOperator.getId());
    }
}
