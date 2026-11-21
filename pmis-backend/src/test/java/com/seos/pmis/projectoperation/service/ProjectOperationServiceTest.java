
package com.seos.pmis.projectoperation.service;

import com.seos.common.exception.BusinessException;
import com.seos.pmis.operation.entity.Operation;
import com.seos.pmis.operation.entity.OperationType;
import com.seos.pmis.operation.repository.OperationRepository;
import com.seos.pmis.project.entity.Project;
import com.seos.pmis.project.entity.ProjectPriority;
import com.seos.pmis.project.entity.ProjectStatus;
import com.seos.pmis.project.repository.ProjectRepository;
import com.seos.pmis.projectoperation.entity.ProjectOperation;
import com.seos.pmis.projectoperation.entity.ProjectOperationRelationshipType;
import com.seos.pmis.projectoperation.entity.ProjectOperationStatus;
import com.seos.pmis.projectoperation.exception.ProjectOperationErrorCode;
import com.seos.pmis.projectoperation.repository.ProjectOperationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectOperationServiceTest {

    @Mock
    private ProjectOperationRepository projectOperationRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private OperationRepository operationRepository;

    @InjectMocks
    private ProjectOperationService projectOperationService;

    private Project project;
    private Operation operation;
    private ProjectOperation projectOperation;

    @BeforeEach
    void setUp() {
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
                .id(1L)
                .operationCode("OP-001")
                .operationName("테스트 운영 서비스")
                .operationType(OperationType.INFRASTRUCTURE_SERVICE)
                .build();

        projectOperation = ProjectOperation.builder()
                .id(1L)
                .project(project)
                .operation(operation)
                .relationshipType(ProjectOperationRelationshipType.BUILD_RESULT)
                .status(ProjectOperationStatus.PLANNED)
                .effectiveFrom(LocalDate.of(2026, 1, 1))
                .build();
    }

    @Test
    void create_프로젝트와_운영_서비스_관계를_생성한다() {
        // given
        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));
        when(operationRepository.findById(1L))
                .thenReturn(Optional.of(operation));
        when(projectOperationRepository
                .existsByProject_IdAndOperation_IdAndStatus(
                        1L, 1L, ProjectOperationStatus.PLANNED))
                .thenReturn(false);
        when(projectOperationRepository
                .existsByProject_IdAndOperation_IdAndStatus(
                        1L, 1L, ProjectOperationStatus.ACTIVE))
                .thenReturn(false);
        when(projectOperationRepository.save(any(ProjectOperation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        ProjectOperation result = projectOperationService.create(
                1L,
                1L,
                ProjectOperationRelationshipType.BUILD_RESULT,
                LocalDate.of(2026, 1, 1),
                null,
                "최초 구축 프로젝트 운영 이관"
        );

        // then
        assertThat(result.getProject()).isEqualTo(project);
        assertThat(result.getOperation()).isEqualTo(operation);
        assertThat(result.getRelationshipType())
                .isEqualTo(ProjectOperationRelationshipType.BUILD_RESULT);
        assertThat(result.getStatus())
                .isEqualTo(ProjectOperationStatus.PLANNED);
        assertThat(result.getDescription())
                .isEqualTo("최초 구축 프로젝트 운영 이관");

        verify(projectOperationRepository).save(any(ProjectOperation.class));
    }

    @Test
    void create_관계_유형이_null이면_예외가_발생한다() {
        // when & then
        assertThatThrownBy(() -> projectOperationService.create(
                1L, 1L, null,
                LocalDate.of(2026, 1, 1), null, null
        ))
                .isInstanceOf(BusinessException.class);

        verifyNoInteractions(
                projectRepository,
                operationRepository,
                projectOperationRepository
        );
    }

    @Test
    void create_관계_기간이_잘못되면_예외가_발생한다() {
        // when & then
        assertThatThrownBy(() -> projectOperationService.create(
                1L,
                1L,
                ProjectOperationRelationshipType.BUILD_RESULT,
                LocalDate.of(2026, 12, 31),
                LocalDate.of(2026, 1, 1),
                null
        ))
                .isInstanceOf(BusinessException.class);

        verifyNoInteractions(
                projectRepository,
                operationRepository,
                projectOperationRepository
        );
    }

    @Test
    void create_동일한_PLANNED_관계가_있으면_중복_예외가_발생한다() {
        // given
        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));
        when(operationRepository.findById(1L))
                .thenReturn(Optional.of(operation));
        when(projectOperationRepository
                .existsByProject_IdAndOperation_IdAndStatus(
                        1L, 1L, ProjectOperationStatus.PLANNED))
                .thenReturn(true);

        // when & then
        assertThatThrownBy(() -> projectOperationService.create(
                1L,
                1L,
                ProjectOperationRelationshipType.BUILD_RESULT,
                LocalDate.of(2026, 1, 1),
                null,
                null
        ))
                .isInstanceOf(BusinessException.class);

        verify(projectOperationRepository, never())
                .save(any(ProjectOperation.class));
    }

    @Test
    void create_동일한_ACTIVE_관계가_있으면_중복_예외가_발생한다() {
        // given
        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));
        when(operationRepository.findById(1L))
                .thenReturn(Optional.of(operation));
        when(projectOperationRepository
                .existsByProject_IdAndOperation_IdAndStatus(
                        1L, 1L, ProjectOperationStatus.PLANNED))
                .thenReturn(false);
        when(projectOperationRepository
                .existsByProject_IdAndOperation_IdAndStatus(
                        1L, 1L, ProjectOperationStatus.ACTIVE))
                .thenReturn(true);

        // when & then
        assertThatThrownBy(() -> projectOperationService.create(
                1L,
                1L,
                ProjectOperationRelationshipType.BUILD_RESULT,
                LocalDate.of(2026, 1, 1),
                null,
                null
        ))
                .isInstanceOf(BusinessException.class);

        verify(projectOperationRepository, never())
                .save(any(ProjectOperation.class));
    }

    @Test
    void create_프로젝트가_없으면_예외가_발생한다() {
        // given
        when(projectRepository.findById(999L))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> projectOperationService.create(
                999L,
                1L,
                ProjectOperationRelationshipType.BUILD_RESULT,
                LocalDate.of(2026, 1, 1),
                null,
                null
        ))
                .isInstanceOf(BusinessException.class);

        verifyNoInteractions(operationRepository);
    }

    @Test
    void create_운영_서비스가_없으면_예외가_발생한다() {
        // given
        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));
        when(operationRepository.findById(999L))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> projectOperationService.create(
                1L,
                999L,
                ProjectOperationRelationshipType.BUILD_RESULT,
                LocalDate.of(2026, 1, 1),
                null,
                null
        ))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void update_프로젝트_운영_관계_정보를_수정한다() {
        // given
        when(projectOperationRepository.findById(1L))
                .thenReturn(Optional.of(projectOperation));

        LocalDate effectiveFrom = LocalDate.of(2026, 2, 1);
        LocalDate effectiveTo = LocalDate.of(2026, 12, 31);

        // when
        ProjectOperation result = projectOperationService.update(
                1L,
                ProjectOperationRelationshipType.ENHANCEMENT,
                effectiveFrom,
                effectiveTo,
                "운영 관계 수정"
        );

        // then
        assertThat(result.getRelationshipType())
                .isEqualTo(ProjectOperationRelationshipType.ENHANCEMENT);
        assertThat(result.getEffectiveFrom()).isEqualTo(effectiveFrom);
        assertThat(result.getEffectiveTo()).isEqualTo(effectiveTo);
        assertThat(result.getDescription()).isEqualTo("운영 관계 수정");
    }

    @Test
    void update_관계_유형이_null이면_예외가_발생한다() {
        // given
        when(projectOperationRepository.findById(1L))
                .thenReturn(Optional.of(projectOperation));

        // when & then
        assertThatThrownBy(() -> projectOperationService.update(
                1L, null,
                LocalDate.of(2026, 1, 1),
                null,
                null
        ))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void update_관계_기간이_잘못되면_예외가_발생한다() {
        // given
        when(projectOperationRepository.findById(1L))
                .thenReturn(Optional.of(projectOperation));

        // when & then
        assertThatThrownBy(() -> projectOperationService.update(
                1L,
                ProjectOperationRelationshipType.BUILD_RESULT,
                LocalDate.of(2026, 12, 31),
                LocalDate.of(2026, 1, 1),
                null
        ))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void changeStatus_PLANNED에서_ACTIVE로_변경한다() {
        // given
        when(projectOperationRepository.findById(1L))
                .thenReturn(Optional.of(projectOperation));

        // when
        ProjectOperation result = projectOperationService.changeStatus(
                1L, ProjectOperationStatus.ACTIVE
        );

        // then
        assertThat(result.getStatus()).isEqualTo(ProjectOperationStatus.ACTIVE);
    }

    @Test
    void changeStatus_PLANNED에서_CANCELLED로_변경한다() {
        // given
        when(projectOperationRepository.findById(1L))
                .thenReturn(Optional.of(projectOperation));

        // when
        ProjectOperation result = projectOperationService.changeStatus(
                1L, ProjectOperationStatus.CANCELLED
        );

        // then
        assertThat(result.getStatus()).isEqualTo(ProjectOperationStatus.CANCELLED);
    }

    @Test
    void changeStatus_ACTIVE에서_COMPLETED로_변경한다() {
        // given
        projectOperation.changeStatus(ProjectOperationStatus.ACTIVE);

        when(projectOperationRepository.findById(1L))
                .thenReturn(Optional.of(projectOperation));

        // when
        ProjectOperation result = projectOperationService.changeStatus(
                1L, ProjectOperationStatus.COMPLETED
        );

        // then
        assertThat(result.getStatus()).isEqualTo(ProjectOperationStatus.COMPLETED);
    }

    @Test
    void changeStatus_ACTIVE에서_CANCELLED로_변경한다() {
        // given
        projectOperation.changeStatus(ProjectOperationStatus.ACTIVE);

        when(projectOperationRepository.findById(1L))
                .thenReturn(Optional.of(projectOperation));

        // when
        ProjectOperation result = projectOperationService.changeStatus(
                1L, ProjectOperationStatus.CANCELLED
        );

        // then
        assertThat(result.getStatus()).isEqualTo(ProjectOperationStatus.CANCELLED);
    }

    @Test
    void changeStatus_null이면_예외가_발생한다() {
        // given
        when(projectOperationRepository.findById(1L))
                .thenReturn(Optional.of(projectOperation));

        // when & then
        assertThatThrownBy(() -> projectOperationService.changeStatus(1L, null))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void changeStatus_PLANNED에서_COMPLETED로_변경할_수_없다() {
        // given
        when(projectOperationRepository.findById(1L))
                .thenReturn(Optional.of(projectOperation));

        // when & then
        assertThatThrownBy(() -> projectOperationService.changeStatus(
                1L, ProjectOperationStatus.COMPLETED
        ))
                .isInstanceOf(BusinessException.class);

        assertThat(projectOperation.getStatus())
                .isEqualTo(ProjectOperationStatus.PLANNED);
    }

    @Test
    void changeStatus_동일한_상태로_변경할_수_없다() {
        // given
        when(projectOperationRepository.findById(1L))
                .thenReturn(Optional.of(projectOperation));

        // when & then
        assertThatThrownBy(() -> projectOperationService.changeStatus(
                1L, ProjectOperationStatus.PLANNED
        ))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void changeStatus_COMPLETED는_다른_상태로_변경할_수_없다() {
        // given
        projectOperation.changeStatus(ProjectOperationStatus.COMPLETED);

        when(projectOperationRepository.findById(1L))
                .thenReturn(Optional.of(projectOperation));

        // when & then
        assertThatThrownBy(() -> projectOperationService.changeStatus(
                1L, ProjectOperationStatus.ACTIVE
        ))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void changeStatus_CANCELLED는_다른_상태로_변경할_수_없다() {
        // given
        projectOperation.changeStatus(ProjectOperationStatus.CANCELLED);

        when(projectOperationRepository.findById(1L))
                .thenReturn(Optional.of(projectOperation));

        // when & then
        assertThatThrownBy(() -> projectOperationService.changeStatus(
                1L, ProjectOperationStatus.ACTIVE
        ))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void getDetail_프로젝트_운영_관계를_조회한다() {
        // given
        when(projectOperationRepository.findById(1L))
                .thenReturn(Optional.of(projectOperation));

        // when
        ProjectOperation result = projectOperationService.getDetail(1L);

        // then
        assertThat(result).isEqualTo(projectOperation);
        verify(projectOperationRepository).findById(1L);
    }

    @Test
    void getDetail_프로젝트_운영_관계가_없으면_예외가_발생한다() {
        // given
        when(projectOperationRepository.findById(999L))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> projectOperationService.getDetail(999L))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void findByProject_프로젝트에_연결된_운영_관계를_조회한다() {
        // given
        List<ProjectOperation> relationships = List.of(projectOperation);

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));
        when(projectOperationRepository.findAllByProject_Id(1L))
                .thenReturn(relationships);

        // when
        List<ProjectOperation> result =
                projectOperationService.findByProject(1L);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(projectOperation);
    }

    @Test
    void findByOperation_운영_서비스에_연결된_프로젝트_관계를_조회한다() {
        // given
        List<ProjectOperation> relationships = List.of(projectOperation);

        when(operationRepository.findById(1L))
                .thenReturn(Optional.of(operation));
        when(projectOperationRepository.findAllByOperation_Id(1L))
                .thenReturn(relationships);

        // when
        List<ProjectOperation> result =
                projectOperationService.findByOperation(1L);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(projectOperation);
    }
}
