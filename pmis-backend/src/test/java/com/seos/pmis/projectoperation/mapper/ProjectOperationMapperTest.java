package com.seos.pmis.projectoperation.mapper;

import com.seos.pmis.operation.entity.Operation;
import com.seos.pmis.operation.entity.OperationStatus;
import com.seos.pmis.operation.entity.OperationType;
import com.seos.pmis.project.entity.Project;
import com.seos.pmis.project.entity.ProjectPriority;
import com.seos.pmis.project.entity.ProjectStatus;
import com.seos.pmis.projectoperation.dto.response.ProjectOperationResponse;
import com.seos.pmis.projectoperation.entity.ProjectOperation;
import com.seos.pmis.projectoperation.entity.ProjectOperationRelationshipType;
import com.seos.pmis.projectoperation.entity.ProjectOperationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectOperationMapperTest {

    private ProjectOperationMapper projectOperationMapper;

    private Project project;
    private Operation operation;
    private ProjectOperation projectOperation;

    @BeforeEach
    void setUp() {
        projectOperationMapper = new ProjectOperationMapper();

        project = Project.builder()
                .id(1L)
                .projectCode("TEST-001")
                .projectName("테스트 프로젝트")
                .customerName("테스트 고객사")
                .projectManager("홍길동")
                .startDate(LocalDate.of(2026, 1, 1))
                .endDate(LocalDate.of(2026, 12, 31))
                .status(ProjectStatus.IN_PROGRESS)
                .priority(ProjectPriority.MEDIUM)
                .build();

        operation = Operation.builder()
                .id(2L)
                .operationCode("OP-001")
                .operationName("테스트 운영 서비스")
                .operationType(OperationType.INFRASTRUCTURE_SERVICE)
                .status(OperationStatus.ACTIVE)
                .build();

        projectOperation = ProjectOperation.builder()
                .id(3L)
                .project(project)
                .operation(operation)
                .relationshipType(ProjectOperationRelationshipType.BUILD_RESULT)
                .status(ProjectOperationStatus.ACTIVE)
                .effectiveFrom(LocalDate.of(2026, 1, 1))
                .effectiveTo(LocalDate.of(2026, 12, 31))
                .description("프로젝트 구축 결과 운영 관계")
                .build();
    }

    @Test
    void toResponse_프로젝트_운영_관계_정보를_응답으로_변환한다() {
        // when
        ProjectOperationResponse response =
                projectOperationMapper.toResponse(projectOperation);

        // then
        assertThat(response.getId()).isEqualTo(3L);
        assertThat(response.getRelationshipType())
                .isEqualTo(ProjectOperationRelationshipType.BUILD_RESULT);
        assertThat(response.getStatus())
                .isEqualTo(ProjectOperationStatus.ACTIVE);

        assertThat(response.getEffectiveFrom())
                .isEqualTo(LocalDate.of(2026, 1, 1));
        assertThat(response.getEffectiveTo())
                .isEqualTo(LocalDate.of(2026, 12, 31));
        assertThat(response.getDescription())
                .isEqualTo("프로젝트 구축 결과 운영 관계");
    }

    @Test
    void toResponse_프로젝트_정보를_응답으로_변환한다() {
        // when
        ProjectOperationResponse response =
                projectOperationMapper.toResponse(projectOperation);

        // then
        assertThat(response.getProjectId()).isEqualTo(1L);
        assertThat(response.getProjectCode()).isEqualTo("TEST-001");
        assertThat(response.getProjectName()).isEqualTo("테스트 프로젝트");
        assertThat(response.getCustomerName()).isEqualTo("테스트 고객사");
        assertThat(response.getProjectManager()).isEqualTo("홍길동");
    }

    @Test
    void toResponse_운영_서비스_정보를_응답으로_변환한다() {
        // when
        ProjectOperationResponse response =
                projectOperationMapper.toResponse(projectOperation);

        // then
        assertThat(response.getOperationId()).isEqualTo(2L);
        assertThat(response.getOperationCode()).isEqualTo("OP-001");
        assertThat(response.getOperationName())
                .isEqualTo("테스트 운영 서비스");
        assertThat(response.getOperationType())
                .isEqualTo(OperationType.INFRASTRUCTURE_SERVICE);
        assertThat(response.getOperationStatus())
                .isEqualTo(OperationStatus.ACTIVE);
    }
}