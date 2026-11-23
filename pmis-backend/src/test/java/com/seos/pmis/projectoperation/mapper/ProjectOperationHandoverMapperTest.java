
package com.seos.pmis.projectoperation.mapper;

import com.seos.pmis.company.entity.Company;
import com.seos.pmis.company.entity.Organization;
import com.seos.pmis.company.entity.OrganizationType;
import com.seos.pmis.operation.entity.Operation;
import com.seos.pmis.operation.entity.OperationParticipant;
import com.seos.pmis.operation.entity.OperationParticipantRole;
import com.seos.pmis.operation.entity.OperationStatus;
import com.seos.pmis.operation.entity.OperationType;
import com.seos.pmis.project.entity.Project;
import com.seos.pmis.project.entity.ProjectPriority;
import com.seos.pmis.project.entity.ProjectStatus;
import com.seos.pmis.projectoperation.dto.response.ProjectOperationHandoverResponse;
import com.seos.pmis.projectoperation.entity.ProjectOperation;
import com.seos.pmis.projectoperation.entity.ProjectOperationHandover;
import com.seos.pmis.projectoperation.entity.ProjectOperationHandoverStatus;
import com.seos.pmis.projectoperation.entity.ProjectOperationHandoverType;
import com.seos.pmis.projectoperation.entity.ProjectOperationRelationshipType;
import com.seos.pmis.projectoperation.entity.ProjectOperationStatus;
import com.seos.pmis.user.entity.User;
import com.seos.pmis.user.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectOperationHandoverMapperTest {

    private ProjectOperationHandoverMapper mapper;

    private Project project;
    private Operation operation;
    private ProjectOperation projectOperation;

    private Company company;
    private Organization organization;
    private OperationParticipant fromParticipant;
    private OperationParticipant toParticipant;

    private User handedOverBy;
    private User acceptedBy;

    @BeforeEach
    void setUp() {
        mapper = new ProjectOperationHandoverMapper();

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

        company = Company.builder()
                .id(4L)
                .companyCode("COMP-001")
                .companyName("테스트 회사")
                .build();

        organization = Organization.builder()
                .id(5L)
                .company(company)
                .organizationCode("ORG-001")
                .organizationName("운영팀")
                .organizationType(OrganizationType.TEAM)
                .build();

        fromParticipant = OperationParticipant.builder()
                .id(6L)
                .operation(operation)
                .company(company)
                .organization(organization)
                .participantRole(OperationParticipantRole.OPERATOR)
                .primary(true)
                .build();

        toParticipant = OperationParticipant.builder()
                .id(7L)
                .operation(operation)
                .company(company)
                .organization(organization)
                .participantRole(OperationParticipantRole.SERVICE_PROVIDER)
                .primary(false)
                .build();

        handedOverBy = User.builder()
                .id(8L)
                .username("handover.user")
                .password("encoded-password")
                .name("인계 담당자")
                .email("handover@example.com")
                .role(UserRole.ROLE_USER)
                .enabled(true)
                .build();

        acceptedBy = User.builder()
                .id(9L)
                .username("accept.user")
                .password("encoded-password")
                .name("승인 담당자")
                .email("accept@example.com")
                .role(UserRole.ROLE_USER)
                .enabled(true)
                .build();
    }

    @Test
    void toResponse_인수인계_기본_정보를_응답으로_변환한다() {
        // given
        LocalDateTime requestedAt = LocalDateTime.of(2026, 11, 1, 10, 0);
        LocalDateTime acceptedAt = LocalDateTime.of(2026, 11, 2, 10, 0);
        LocalDateTime completedAt = LocalDateTime.of(2026, 11, 3, 10, 0);

        ProjectOperationHandover handover = createHandover(
                10L,
                ProjectOperationHandoverType.OPERATOR_CHANGE,
                ProjectOperationHandoverStatus.COMPLETED,
                2,
                null,
                fromParticipant,
                toParticipant,
                handedOverBy,
                acceptedBy,
                requestedAt,
                acceptedAt,
                completedAt
        );

        // when
        ProjectOperationHandoverResponse response =
                mapper.toResponse(handover);

        // then
        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getHandoverType())
                .isEqualTo(ProjectOperationHandoverType.OPERATOR_CHANGE);
        assertThat(response.getStatus())
                .isEqualTo(ProjectOperationHandoverStatus.COMPLETED);
        assertThat(response.getSequenceNo()).isEqualTo(2);

        assertThat(response.getPlannedHandoverDate())
                .isEqualTo(LocalDate.of(2026, 11, 5));
        assertThat(response.getRequestedAt()).isEqualTo(requestedAt);
        assertThat(response.getAcceptedAt()).isEqualTo(acceptedAt);
        assertThat(response.getCompletedAt()).isEqualTo(completedAt);
        assertThat(response.getNotes()).isEqualTo("인수인계 테스트 비고");
    }

    @Test
    void toResponse_프로젝트와_운영_관계_정보를_응답으로_변환한다() {
        // given
        ProjectOperationHandover handover = createHandover(
                10L,
                ProjectOperationHandoverType.INITIAL,
                ProjectOperationHandoverStatus.DRAFT,
                1,
                null,
                null,
                toParticipant,
                null,
                null,
                null,
                null,
                null
        );

        // when
        ProjectOperationHandoverResponse response =
                mapper.toResponse(handover);

        // then
        assertThat(response.getProjectOperation().getId()).isEqualTo(3L);
        assertThat(response.getProjectOperation().getRelationshipType())
                .isEqualTo(ProjectOperationRelationshipType.BUILD_RESULT);
        assertThat(response.getProjectOperation().getStatus())
                .isEqualTo(ProjectOperationStatus.ACTIVE);

        assertThat(response.getProject().getId()).isEqualTo(1L);
        assertThat(response.getProject().getProjectCode()).isEqualTo("TEST-001");
        assertThat(response.getProject().getProjectName())
                .isEqualTo("테스트 프로젝트");
        assertThat(response.getProject().getCustomerName())
                .isEqualTo("테스트 고객사");
        assertThat(response.getProject().getProjectManager())
                .isEqualTo("홍길동");

        assertThat(response.getOperation().getId()).isEqualTo(2L);
        assertThat(response.getOperation().getOperationCode()).isEqualTo("OP-001");
        assertThat(response.getOperation().getOperationName())
                .isEqualTo("테스트 운영 서비스");
        assertThat(response.getOperation().getOperationType())
                .isEqualTo(OperationType.INFRASTRUCTURE_SERVICE);
        assertThat(response.getOperation().getStatus())
                .isEqualTo(OperationStatus.ACTIVE);
    }

    @Test
    void toResponse_이전_인수인계_정보를_응답으로_변환한다() {
        // given
        ProjectOperationHandover previousHandover = createHandover(
                11L,
                ProjectOperationHandoverType.INITIAL,
                ProjectOperationHandoverStatus.COMPLETED,
                1,
                null,
                null,
                fromParticipant,
                null,
                null,
                null,
                null,
                null
        );

        ProjectOperationHandover handover = createHandover(
                12L,
                ProjectOperationHandoverType.RE_HANDOVER,
                ProjectOperationHandoverStatus.DRAFT,
                2,
                previousHandover,
                fromParticipant,
                toParticipant,
                null,
                null,
                null,
                null,
                null
        );

        // when
        ProjectOperationHandoverResponse response =
                mapper.toResponse(handover);

        // then
        assertThat(response.getPreviousHandoverId()).isEqualTo(11L);
        assertThat(response.getPreviousSequenceNo()).isEqualTo(1);
        assertThat(response.getPreviousHandoverType())
                .isEqualTo(ProjectOperationHandoverType.INITIAL);
    }

    @Test
    void toResponse_참여자_회사와_조직_정보를_응답으로_변환한다() {
        // given
        ProjectOperationHandover handover = createHandover(
                10L,
                ProjectOperationHandoverType.OPERATOR_CHANGE,
                ProjectOperationHandoverStatus.DRAFT,
                2,
                null,
                fromParticipant,
                toParticipant,
                null,
                null,
                null,
                null,
                null
        );

        // when
        ProjectOperationHandoverResponse response =
                mapper.toResponse(handover);

        // then
        assertThat(response.getFromParticipant().getId()).isEqualTo(6L);
        assertThat(response.getFromParticipant().getRole())
                .isEqualTo(OperationParticipantRole.OPERATOR);
        assertThat(response.getFromParticipant().isPrimary()).isTrue();

        assertThat(response.getFromParticipant().getCompany().getId())
                .isEqualTo(4L);
        assertThat(response.getFromParticipant().getCompany().getCompanyCode())
                .isEqualTo("COMP-001");
        assertThat(response.getFromParticipant().getCompany().getCompanyName())
                .isEqualTo("테스트 회사");

        assertThat(response.getFromParticipant().getOrganization().getId())
                .isEqualTo(5L);
        assertThat(response.getFromParticipant().getOrganization()
                .getOrganizationCode()).isEqualTo("ORG-001");
        assertThat(response.getFromParticipant().getOrganization()
                .getOrganizationName()).isEqualTo("운영팀");

        assertThat(response.getToParticipant().getId()).isEqualTo(7L);
        assertThat(response.getToParticipant().getRole())
                .isEqualTo(OperationParticipantRole.SERVICE_PROVIDER);
        assertThat(response.getToParticipant().isPrimary()).isFalse();
    }

    @Test
    void toResponse_인계자와_승인자_정보를_응답으로_변환한다() {
        // given
        ProjectOperationHandover handover = createHandover(
                10L,
                ProjectOperationHandoverType.OPERATOR_CHANGE,
                ProjectOperationHandoverStatus.COMPLETED,
                2,
                null,
                fromParticipant,
                toParticipant,
                handedOverBy,
                acceptedBy,
                LocalDateTime.of(2026, 11, 1, 10, 0),
                LocalDateTime.of(2026, 11, 2, 10, 0),
                LocalDateTime.of(2026, 11, 3, 10, 0)
        );

        // when
        ProjectOperationHandoverResponse response =
                mapper.toResponse(handover);

        // then
        assertThat(response.getHandedOverByUserId()).isEqualTo(8L);
        assertThat(response.getHandedOverByName()).isEqualTo("인계 담당자");
        assertThat(response.getAcceptedByUserId()).isEqualTo(9L);
        assertThat(response.getAcceptedByName()).isEqualTo("승인 담당자");
    }

    @Test
    void toResponse_최초_인수인계는_이전_이력과_인계_참여자가_null이다() {
        // given
        ProjectOperationHandover handover = createHandover(
                10L,
                ProjectOperationHandoverType.INITIAL,
                ProjectOperationHandoverStatus.DRAFT,
                1,
                null,
                null,
                toParticipant,
                null,
                null,
                null,
                null,
                null
        );

        // when
        ProjectOperationHandoverResponse response =
                mapper.toResponse(handover);

        // then
        assertThat(response.getPreviousHandoverId()).isNull();
        assertThat(response.getPreviousSequenceNo()).isNull();
        assertThat(response.getPreviousHandoverType()).isNull();
        assertThat(response.getFromParticipant()).isNull();
        assertThat(response.getToParticipant()).isNotNull();
    }

    @Test
    void toResponse_조직이_없는_참여자는_조직_정보가_null이다() {
        // given
        OperationParticipant participantWithoutOrganization =
                OperationParticipant.builder()
                        .id(20L)
                        .operation(operation)
                        .company(company)
                        .organization(null)
                        .participantRole(OperationParticipantRole.OPERATOR)
                        .primary(true)
                        .build();

        ProjectOperationHandover handover = createHandover(
                10L,
                ProjectOperationHandoverType.INITIAL,
                ProjectOperationHandoverStatus.DRAFT,
                1,
                null,
                null,
                participantWithoutOrganization,
                null,
                null,
                null,
                null,
                null
        );

        // when
        ProjectOperationHandoverResponse response =
                mapper.toResponse(handover);

        // then
        assertThat(response.getToParticipant().getOrganization()).isNull();
    }

    private ProjectOperationHandover createHandover(
            Long id,
            ProjectOperationHandoverType type,
            ProjectOperationHandoverStatus status,
            Integer sequenceNo,
            ProjectOperationHandover previousHandover,
            OperationParticipant fromParticipant,
            OperationParticipant toParticipant,
            User handedOverBy,
            User acceptedBy,
            LocalDateTime requestedAt,
            LocalDateTime acceptedAt,
            LocalDateTime completedAt) {

        return ProjectOperationHandover.builder()
                .id(id)
                .projectOperation(projectOperation)
                .operation(operation)
                .handoverType(type)
                .status(status)
                .sequenceNo(sequenceNo)
                .previousHandover(previousHandover)
                .fromParticipant(fromParticipant)
                .toParticipant(toParticipant)
                .handedOverBy(handedOverBy)
                .acceptedBy(acceptedBy)
                .plannedHandoverDate(LocalDate.of(2026, 11, 5))
                .requestedAt(requestedAt)
                .acceptedAt(acceptedAt)
                .completedAt(completedAt)
                .notes("인수인계 테스트 비고")
                .build();
    }
}
