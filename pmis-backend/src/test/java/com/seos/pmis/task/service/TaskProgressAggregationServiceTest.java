
package com.seos.pmis.task.service;

import com.seos.pmis.task.dto.response.TaskProgressAggregation;
import com.seos.pmis.task.entity.Task;
import com.seos.pmis.task.entity.TaskPriority;
import com.seos.pmis.task.entity.TaskStatus;
import com.seos.pmis.project.entity.Project;
import com.seos.pmis.wbs.entity.Wbs;
import com.seos.pmis.wbs.entity.WbsStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TaskProgressAggregationServiceTest {

    private TaskProgressAggregationService service;

    private Project project;

    @BeforeEach
    void setUp() {
        service = new TaskProgressAggregationService();

        project = Project.builder()
                .projectCode("TEST-001")
                .projectName("집계 테스트 프로젝트")
                .build();
    }

    @Test
    @DisplayName("계획 공수 가중 평균으로 프로젝트 진척률을 계산한다")
    void aggregateProject_calculatesWeightedProgressRate() {

        // given
        Task task1 = createTask(
                null,
                "작업 1",
                TaskStatus.IN_PROGRESS,
                50,
                "10.00");

        Task task2 = createTask(
                null,
                "작업 2",
                TaskStatus.COMPLETED,
                100,
                "20.00");

        // when
        TaskProgressAggregation result =
                service.aggregateProject(List.of(task1, task2));

        // then
        assertThat(result.getTotalPlannedManDays())
                .isEqualByComparingTo("30.00");

        assertThat(result.getWeightedCompletedManDays())
                .isEqualByComparingTo("25.00");

        assertThat(result.getProgressRate())
                .isEqualByComparingTo("83.33");

        assertThat(result.getMissingEffortTaskCount())
                .isZero();
    }

    @Test
    @DisplayName("하위 WBS의 Task는 상위 WBS 집계에도 포함한다")
    void aggregateByWbs_includesDescendantTasks() {

        // given
        Wbs root = createWbs(
                1L,
                null,
                "1",
                "인프라 구축");

        Wbs child = createWbs(
                2L,
                root,
                "1.1",
                "서버 구축");

        Wbs grandChild = createWbs(
                3L,
                child,
                "1.1.1",
                "서버 설치");

        Task rootTask = createTask(
                root,
                "상위 작업",
                TaskStatus.IN_PROGRESS,
                50,
                "10.00");

        Task childTask = createTask(
                child,
                "하위 작업",
                TaskStatus.IN_PROGRESS,
                100,
                "20.00");

        Task grandChildTask = createTask(
                grandChild,
                "최하위 작업",
                TaskStatus.IN_PROGRESS,
                0,
                "30.00");

        // when
        Map<Long, TaskProgressAggregation> result =
                service.aggregateByWbs(
                        List.of(root, child, grandChild),
                        List.of(rootTask, childTask, grandChildTask));

        // then
        assertThat(result.get(1L).getTotalPlannedManDays())
                .isEqualByComparingTo("60.00");

        assertThat(result.get(1L).getProgressRate())
                .isEqualByComparingTo("41.67");

        assertThat(result.get(2L).getTotalPlannedManDays())
                .isEqualByComparingTo("50.00");

        assertThat(result.get(2L).getProgressRate())
                .isEqualByComparingTo("40.00");

        assertThat(result.get(3L).getTotalPlannedManDays())
                .isEqualByComparingTo("30.00");

        assertThat(result.get(3L).getProgressRate())
                .isEqualByComparingTo("0.00");
    }

    @Test
    @DisplayName("WBS 미할당 Task는 WBS 집계에서 제외하고 프로젝트 집계에는 포함한다")
    void aggregateByWbs_excludesUnassignedTasks() {

        // given
        Wbs root = createWbs(
                1L,
                null,
                "1",
                "인프라 구축");

        Task assignedTask = createTask(
                root,
                "WBS 할당 작업",
                TaskStatus.IN_PROGRESS,
                50,
                "10.00");

        Task unassignedTask = createTask(
                null,
                "WBS 미할당 작업",
                TaskStatus.IN_PROGRESS,
                100,
                "20.00");

        // when
        Map<Long, TaskProgressAggregation> wbsResult =
                service.aggregateByWbs(
                        List.of(root),
                        List.of(assignedTask, unassignedTask));

        TaskProgressAggregation projectResult =
                service.aggregateProject(
                        List.of(assignedTask, unassignedTask));

        // then
        assertThat(wbsResult.get(1L).getTotalPlannedManDays())
                .isEqualByComparingTo("10.00");

        assertThat(wbsResult.get(1L).getProgressRate())
                .isEqualByComparingTo("50.00");

        assertThat(projectResult.getTotalPlannedManDays())
                .isEqualByComparingTo("30.00");

        assertThat(projectResult.getProgressRate())
                .isEqualByComparingTo("83.33");
    }

    @Test
    @DisplayName("계획 공수가 없는 Task는 진척률 계산에서 제외하고 누락 건수에 포함한다")
    void aggregateProject_countsTasksWithMissingEffort() {

        // given
        Task task1 = createTask(
                null,
                "공수 입력 작업",
                TaskStatus.IN_PROGRESS,
                50,
                "10.00");

        Task task2 = createTask(
                null,
                "공수 미입력 작업",
                TaskStatus.IN_PROGRESS,
                100,
                null);

        // when
        TaskProgressAggregation result =
                service.aggregateProject(List.of(task1, task2));

        // then
        assertThat(result.getTotalPlannedManDays())
                .isEqualByComparingTo("10.00");

        assertThat(result.getWeightedCompletedManDays())
                .isEqualByComparingTo("5.00");

        assertThat(result.getProgressRate())
                .isEqualByComparingTo("50.00");

        assertThat(result.getMissingEffortTaskCount())
                .isEqualTo(1);
    }

    @Test
    @DisplayName("CANCELLED Task도 기존 진척률과 계획 공수를 집계한다")
    void aggregateProject_includesCancelledTasks() {

        // given
        Task cancelledTask = createTask(
                null,
                "취소 작업",
                TaskStatus.CANCELLED,
                40,
                "10.00");

        Task completedTask = createTask(
                null,
                "완료 작업",
                TaskStatus.COMPLETED,
                100,
                "10.00");

        // when
        TaskProgressAggregation result =
                service.aggregateProject(
                        List.of(cancelledTask, completedTask));

        // then
        assertThat(result.getTotalPlannedManDays())
                .isEqualByComparingTo("20.00");

        assertThat(result.getWeightedCompletedManDays())
                .isEqualByComparingTo("14.00");

        assertThat(result.getProgressRate())
                .isEqualByComparingTo("70.00");
    }

    @Test
    @DisplayName("모든 Task의 계획 공수가 없으면 진척률은 null을 반환한다")
    void aggregateProject_returnsNullProgressWhenAllEffortsMissing() {

        // given
        Task task1 = createTask(
                null,
                "작업 1",
                TaskStatus.IN_PROGRESS,
                50,
                null);

        Task task2 = createTask(
                null,
                "작업 2",
                TaskStatus.IN_PROGRESS,
                80,
                null);

        // when
        TaskProgressAggregation result =
                service.aggregateProject(List.of(task1, task2));

        // then
        assertThat(result.getProgressRate())
                .isNull();

        assertThat(result.getTotalPlannedManDays())
                .isEqualByComparingTo("0.00");

        assertThat(result.getWeightedCompletedManDays())
                .isEqualByComparingTo("0.00");

        assertThat(result.getMissingEffortTaskCount())
                .isEqualTo(2);
    }

    @Test
    @DisplayName("Task가 없으면 진척률은 null이고 집계 공수는 0이다")
    void aggregateProject_returnsEmptyAggregationWhenNoTasks() {

        // when
        TaskProgressAggregation result =
                service.aggregateProject(List.of());

        // then
        assertThat(result.getProgressRate())
                .isNull();

        assertThat(result.getTotalPlannedManDays())
                .isEqualByComparingTo("0.00");

        assertThat(result.getWeightedCompletedManDays())
                .isEqualByComparingTo("0.00");

        assertThat(result.getMissingEffortTaskCount())
                .isZero();
    }

    @Test
    @DisplayName("모든 WBS는 연결된 Task가 없어도 집계 결과를 반환한다")
    void aggregateByWbs_returnsEmptyAggregationForWbsWithoutTasks() {

        // given
        Wbs root = createWbs(
                1L,
                null,
                "1",
                "인프라 구축");

        // when
        Map<Long, TaskProgressAggregation> result =
                service.aggregateByWbs(List.of(root), List.of());

        // then
        assertThat(result).containsKey(1L);

        assertThat(result.get(1L).getProgressRate())
                .isNull();

        assertThat(result.get(1L).getTotalPlannedManDays())
                .isEqualByComparingTo("0.00");

        assertThat(result.get(1L).getMissingEffortTaskCount())
                .isZero();
    }

    private Task createTask(
            Wbs wbs,
            String taskName,
            TaskStatus status,
            Integer progressRate,
            String plannedManDays) {

        return Task.builder()
                .project(project)
                .wbs(wbs)
                .taskName(taskName)
                .status(status)
                .progressRate(progressRate)
                .plannedManDays(
                        plannedManDays != null
                                ? new BigDecimal(plannedManDays)
                                : null)
                .priority(TaskPriority.MEDIUM)
                .sortOrder(0)
                .build();
    }

    private Wbs createWbs(
            Long id,
            Wbs parent,
            String wbsCode,
            String wbsName) {

        return Wbs.builder()
                .id(id)
                .project(project)
                .parent(parent)
                .wbsCode(wbsCode)
                .wbsName(wbsName)
                .level(parent == null ? 1 : parent.getLevel() + 1)
                .sortOrder(0)
                .status(WbsStatus.PLANNED)
                .build();
    }
}
