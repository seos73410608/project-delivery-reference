
package com.seos.pmis.project.service;

import com.seos.common.exception.BusinessException;
import com.seos.pmis.project.dto.response.ProjectDetailResponse;
import com.seos.pmis.project.entity.Project;
import com.seos.pmis.project.entity.ProjectPriority;
import com.seos.pmis.project.entity.ProjectStatus;
import com.seos.pmis.project.mapper.ProjectMapper;
import com.seos.pmis.project.repository.ProjectRepository;
import com.seos.pmis.task.dto.response.TaskProgressAggregation;
import com.seos.pmis.task.entity.Task;
import com.seos.pmis.task.repository.TaskRepository;
import com.seos.pmis.task.service.TaskProgressAggregationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectDetailServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskProgressAggregationService taskProgressAggregationService;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectDetailService projectDetailService;

    private Project project;

    @BeforeEach
    void setUp() {
        project = Project.builder()
                .id(1L)
                .projectCode("TEST-001")
                .projectName("집계 테스트 프로젝트")
                .customerName("테스트 고객사")
                .projectManager("홍길동")
                .startDate(LocalDate.of(2026, 1, 1))
                .endDate(LocalDate.of(2026, 12, 31))
                .status(ProjectStatus.IN_PROGRESS)
                .priority(ProjectPriority.MEDIUM)
                .build();
    }

    @Test
    void getProjectDetail_프로젝트_전체_Task_집계_결과를_반영한다() {
        // given
        Long projectId = 1L;
        List<Task> tasks = List.of(mock(Task.class));

        when(projectMapper.toDetailResponse(project))
                .thenReturn(ProjectDetailResponse.builder()
                        .id(1L)
                        .projectCode("TEST-001")
                        .projectName("집계 테스트 프로젝트")
                        .build());

        TaskProgressAggregation aggregation = TaskProgressAggregation.builder()
                .progressRate(new BigDecimal("83.33"))
                .totalPlannedManDays(new BigDecimal("30.00"))
                .weightedCompletedManDays(new BigDecimal("25.00"))
                .missingEffortTaskCount(1L)
                .build();

        when(projectRepository.findById(projectId))
                .thenReturn(Optional.of(project));
        when(taskRepository.findByProject_IdOrderBySortOrderAsc(projectId))
                .thenReturn(tasks);
        when(taskProgressAggregationService.aggregateProject(tasks))
                .thenReturn(aggregation);

        // when
        ProjectDetailResponse response = projectDetailService.getProjectDetail(projectId);

        // then
        assertThat(response.getId()).isEqualTo(projectId);
        assertThat(response.getProjectCode()).isEqualTo("TEST-001");
        assertThat(response.getProjectName()).isEqualTo("집계 테스트 프로젝트");

        assertThat(response.getProgressRate())
                .isEqualByComparingTo("83.33");
        assertThat(response.getTotalPlannedManDays())
                .isEqualByComparingTo("30.00");
        assertThat(response.getWeightedCompletedManDays())
                .isEqualByComparingTo("25.00");
        assertThat(response.getMissingEffortTaskCount()).isEqualTo(1L);

        verify(projectRepository).findById(projectId);
        verify(taskRepository).findByProject_IdOrderBySortOrderAsc(projectId);
        verify(taskProgressAggregationService).aggregateProject(tasks);
    }

    @Test
    void getProjectDetail_집계할_공수가_없으면_진척률은_null이다() {
        // given
        Long projectId = 1L;

        when(projectMapper.toDetailResponse(project))
                .thenReturn(ProjectDetailResponse.builder()
                        .id(1L)
                        .projectCode("TEST-001")
                        .projectName("집계 테스트 프로젝트")
                        .build());

        TaskProgressAggregation aggregation = TaskProgressAggregation.builder()
                .progressRate(null)
                .totalPlannedManDays(new BigDecimal("0.00"))
                .weightedCompletedManDays(new BigDecimal("0.00"))
                .missingEffortTaskCount(2L)
                .build();

        when(projectRepository.findById(projectId))
                .thenReturn(Optional.of(project));
        when(taskRepository.findByProject_IdOrderBySortOrderAsc(projectId))
                .thenReturn(List.of());
        when(taskProgressAggregationService.aggregateProject(List.of()))
                .thenReturn(aggregation);

        // when
        ProjectDetailResponse response = projectDetailService.getProjectDetail(projectId);

        // then
        assertThat(response.getProgressRate()).isNull();
        assertThat(response.getTotalPlannedManDays())
                .isEqualByComparingTo("0.00");
        assertThat(response.getWeightedCompletedManDays())
                .isEqualByComparingTo("0.00");
        assertThat(response.getMissingEffortTaskCount()).isEqualTo(2L);
    }

    @Test
    void getProjectDetail_프로젝트가_없으면_예외가_발생한다() {
        // given
        Long projectId = 999L;

        when(projectRepository.findById(projectId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> projectDetailService.getProjectDetail(projectId))
                .isInstanceOf(BusinessException.class);

        verify(projectRepository).findById(projectId);
        verifyNoInteractions(taskRepository, taskProgressAggregationService);
    }
}
