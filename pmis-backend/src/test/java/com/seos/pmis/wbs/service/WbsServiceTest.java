
package com.seos.pmis.wbs.service;

import com.seos.pmis.project.entity.Project;
import com.seos.pmis.project.repository.ProjectRepository;
import com.seos.pmis.task.dto.response.TaskProgressAggregation;
import com.seos.pmis.task.entity.Task;
import com.seos.pmis.task.repository.TaskRepository;
import com.seos.pmis.task.service.TaskProgressAggregationService;
import com.seos.pmis.wbs.dto.response.WbsResponse;
import com.seos.pmis.wbs.dto.response.WbsTreeResponse;
import com.seos.pmis.wbs.entity.Wbs;
import com.seos.pmis.wbs.entity.WbsStatus;
import com.seos.pmis.wbs.repository.WbsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WbsServiceTest {

    @Mock
    private WbsRepository wbsRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskProgressAggregationService taskProgressAggregationService;

    @InjectMocks
    private WbsService wbsService;

    private Project project;
    private Wbs rootWbs;
    private Wbs childWbs;
    private Task task;

    @BeforeEach
    void setUp() {
        project = Project.builder()
                .id(1L)
                .projectCode("TEST-001")
                .projectName("WBS 집계 테스트 프로젝트")
                .build();

        rootWbs = createWbs(
                1L,
                null,
                "1",
                "프로젝트 관리",
                1);

        childWbs = createWbs(
                2L,
                rootWbs,
                "1.1",
                "요구사항 분석",
                2);

        task = mock(Task.class);
    }

    @Test
    void findById_WBS_단건_조회시_집계_결과를_반영한다() {
        // given
        Long wbsId = 2L;

        List<Wbs> wbsList = List.of(rootWbs, childWbs);
        List<Task> tasks = List.of(task);

        TaskProgressAggregation childAggregation =
                createAggregation("75.00", "20.00", "15.00", 1L);

        when(wbsRepository.findById(wbsId))
                .thenReturn(Optional.of(childWbs));
        when(wbsRepository.findByProjectIdOrderBySortOrderAsc(1L))
                .thenReturn(wbsList);
        when(taskRepository.findByProject_IdOrderBySortOrderAsc(1L))
                .thenReturn(tasks);
        when(taskProgressAggregationService.aggregateByWbs(wbsList, tasks))
                .thenReturn(Map.of(2L, childAggregation));

        // when
        WbsResponse response = wbsService.findById(wbsId);

        // then
        assertThat(response.getId()).isEqualTo(2L);
        assertThat(response.getParentId()).isEqualTo(1L);
        assertThat(response.getProgressRate())
                .isEqualByComparingTo("75.00");
        assertThat(response.getTotalPlannedManDays())
                .isEqualByComparingTo("20.00");
        assertThat(response.getWeightedCompletedManDays())
                .isEqualByComparingTo("15.00");
        assertThat(response.getMissingEffortTaskCount()).isEqualTo(1L);

        verify(taskProgressAggregationService)
                .aggregateByWbs(wbsList, tasks);
    }

    @Test
    void findByProjectId_프로젝트_WBS_목록에_집계_결과를_반영한다() {
        // given
        Long projectId = 1L;

        List<Wbs> wbsList = List.of(rootWbs, childWbs);
        List<Task> tasks = List.of(task);

        TaskProgressAggregation rootAggregation =
                createAggregation("75.00", "20.00", "15.00", 1L);

        TaskProgressAggregation childAggregation =
                createAggregation("50.00", "10.00", "5.00", 0L);

        when(projectRepository.existsById(projectId))
                .thenReturn(true);
        when(wbsRepository.findByProjectIdOrderBySortOrderAsc(projectId))
                .thenReturn(wbsList);
        when(taskRepository.findByProject_IdOrderBySortOrderAsc(projectId))
                .thenReturn(tasks);
        when(taskProgressAggregationService.aggregateByWbs(wbsList, tasks))
                .thenReturn(Map.of(
                        1L, rootAggregation,
                        2L, childAggregation));

        // when
        List<WbsResponse> responses = wbsService.findByProjectId(projectId);

        // then
        assertThat(responses).hasSize(2);

        assertThat(responses.get(0).getId()).isEqualTo(1L);
        assertThat(responses.get(0).getProgressRate())
                .isEqualByComparingTo("75.00");

        assertThat(responses.get(1).getId()).isEqualTo(2L);
        assertThat(responses.get(1).getProgressRate())
                .isEqualByComparingTo("50.00");

        verify(taskProgressAggregationService)
                .aggregateByWbs(wbsList, tasks);
    }

    @Test
    void findRootWbs_하위_WBS의_Task를_포함한_집계_결과를_반영한다() {
        // given
        Long projectId = 1L;

        List<Wbs> wbsList = List.of(rootWbs, childWbs);
        List<Task> tasks = List.of(task);

        TaskProgressAggregation rootAggregation =
                createAggregation("80.00", "50.00", "40.00", 0L);

        when(projectRepository.existsById(projectId))
                .thenReturn(true);
        when(wbsRepository.findByProjectIdAndParentIsNullOrderBySortOrderAsc(projectId))
                .thenReturn(List.of(rootWbs));
        when(wbsRepository.findByProjectIdOrderBySortOrderAsc(projectId))
                .thenReturn(wbsList);
        when(taskRepository.findByProject_IdOrderBySortOrderAsc(projectId))
                .thenReturn(tasks);
        when(taskProgressAggregationService.aggregateByWbs(wbsList, tasks))
                .thenReturn(Map.of(1L, rootAggregation));

        // when
        List<WbsResponse> responses = wbsService.findRootWbs(projectId);

        // then
        assertThat(responses).hasSize(1);

        WbsResponse response = responses.get(0);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getParentId()).isNull();
        assertThat(response.getProgressRate())
                .isEqualByComparingTo("80.00");
        assertThat(response.getTotalPlannedManDays())
                .isEqualByComparingTo("50.00");
        assertThat(response.getWeightedCompletedManDays())
                .isEqualByComparingTo("40.00");

        verify(taskProgressAggregationService)
                .aggregateByWbs(wbsList, tasks);
    }

    @Test
    void findChildren_하위_WBS별_집계_결과를_반영한다() {
        // given
        Long parentId = 1L;

        List<Wbs> wbsList = List.of(rootWbs, childWbs);
        List<Task> tasks = List.of(task);

        TaskProgressAggregation childAggregation =
                createAggregation("60.00", "10.00", "6.00", 2L);

        when(wbsRepository.findById(parentId))
                .thenReturn(Optional.of(rootWbs));
        when(wbsRepository.findByParentIdOrderBySortOrderAsc(parentId))
                .thenReturn(List.of(childWbs));
        when(wbsRepository.findByProjectIdOrderBySortOrderAsc(1L))
                .thenReturn(wbsList);
        when(taskRepository.findByProject_IdOrderBySortOrderAsc(1L))
                .thenReturn(tasks);
        when(taskProgressAggregationService.aggregateByWbs(wbsList, tasks))
                .thenReturn(Map.of(2L, childAggregation));

        // when
        List<WbsResponse> responses = wbsService.findChildren(parentId);

        // then
        assertThat(responses).hasSize(1);

        WbsResponse response = responses.get(0);

        assertThat(response.getId()).isEqualTo(2L);
        assertThat(response.getParentId()).isEqualTo(1L);
        assertThat(response.getProgressRate())
                .isEqualByComparingTo("60.00");
        assertThat(response.getTotalPlannedManDays())
                .isEqualByComparingTo("10.00");
        assertThat(response.getMissingEffortTaskCount()).isEqualTo(2L);

        verify(taskProgressAggregationService)
                .aggregateByWbs(wbsList, tasks);
    }

    @Test
    void findTree_계층_구조와_각_WBS의_집계_결과를_반영한다() {
        // given
        Long projectId = 1L;

        List<Wbs> wbsList = List.of(rootWbs, childWbs);
        List<Task> tasks = List.of(task);

        TaskProgressAggregation rootAggregation =
                createAggregation("75.00", "20.00", "15.00", 1L);

        TaskProgressAggregation childAggregation =
                createAggregation("50.00", "10.00", "5.00", 0L);

        when(projectRepository.existsById(projectId))
                .thenReturn(true);
        when(wbsRepository.findByProjectIdOrderBySortOrderAsc(projectId))
                .thenReturn(wbsList);
        when(taskRepository.findByProject_IdOrderBySortOrderAsc(projectId))
                .thenReturn(tasks);
        when(taskProgressAggregationService.aggregateByWbs(wbsList, tasks))
                .thenReturn(Map.of(
                        1L, rootAggregation,
                        2L, childAggregation));

        // when
        List<WbsTreeResponse> roots = wbsService.findTree(projectId);

        // then
        assertThat(roots).hasSize(1);

        WbsTreeResponse root = roots.get(0);

        assertThat(root.getId()).isEqualTo(1L);
        assertThat(root.getParentId()).isNull();
        assertThat(root.getProgressRate())
                .isEqualByComparingTo("75.00");

        assertThat(root.getChildren()).hasSize(1);

        WbsTreeResponse child = root.getChildren().get(0);

        assertThat(child.getId()).isEqualTo(2L);
        assertThat(child.getParentId()).isEqualTo(1L);
        assertThat(child.getProgressRate())
                .isEqualByComparingTo("50.00");
        assertThat(child.getTotalPlannedManDays())
                .isEqualByComparingTo("10.00");

        verify(taskProgressAggregationService)
                .aggregateByWbs(wbsList, tasks);
    }

    private Wbs createWbs(
            Long id,
            Wbs parent,
            String wbsCode,
            String wbsName,
            int level) {

        return Wbs.builder()
                .id(id)
                .project(project)
                .parent(parent)
                .wbsCode(wbsCode)
                .wbsName(wbsName)
                .level(level)
                .sortOrder(0)
                .status(WbsStatus.PLANNED)
                .build();
    }

    private TaskProgressAggregation createAggregation(
            String progressRate,
            String totalPlannedManDays,
            String weightedCompletedManDays,
            long missingEffortTaskCount) {

        return TaskProgressAggregation.builder()
                .progressRate(new BigDecimal(progressRate))
                .totalPlannedManDays(new BigDecimal(totalPlannedManDays))
                .weightedCompletedManDays(new BigDecimal(weightedCompletedManDays))
                .missingEffortTaskCount(missingEffortTaskCount)
                .build();
    }
}
