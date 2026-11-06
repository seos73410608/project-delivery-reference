
package com.seos.pmis.task.service;

import com.seos.pmis.task.dto.response.TaskProgressAggregation;
import com.seos.pmis.task.entity.Task;
import com.seos.pmis.wbs.entity.Wbs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Task 진척률 집계 서비스
 *
 * Task의 계획 공수와 진척률을 기반으로
 * WBS 및 Project의 가중 진척률을 계산한다.
 *
 * Repository에 직접 접근하지 않으며,
 * 호출하는 서비스에서 조회한 WBS와 Task 목록을 전달받아
 * 메모리에서 집계를 수행한다.
 *
 * 집계 정책:
 * - WBS 집계는 해당 WBS와 모든 하위 WBS의 Task를 대상으로 한다.
 * - Project 집계는 프로젝트의 모든 Task를 대상으로 한다.
 * - WBS가 없는 Task는 WBS 집계에서 제외하고 Project 집계에는 포함한다.
 * - 계획 공수가 없는 Task는 가중 진척률 계산에서 제외한다.
 * - 계획 공수가 없는 Task는 누락 건수에 포함한다.
 * - CANCELLED Task도 집계 대상에 포함한다.
 * - 최종 결과는 소수점 둘째 자리까지 HALF_UP으로 반올림한다.
 */
@Slf4j
@Service
public class TaskProgressAggregationService {

    /**
     * 중간 계산 정밀도
     */
    private static final int CALCULATION_SCALE = 10;

    /**
     * 최종 결과 소수점 자리수
     */
    private static final int RESULT_SCALE = 2;

    /**
     * 반올림 정책
     */
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    /**
     * WBS별 진척률 집계
     *
     * 각 WBS에 직접 연결된 Task뿐 아니라
     * 모든 하위 WBS에 연결된 Task를 포함한다.
     *
     * Task를 해당 WBS부터 최상위 WBS까지 순회하며 누적하므로
     * 하위 WBS의 집계 결과를 다시 합산할 필요가 없다.
     *
     * @param wbsList 프로젝트의 전체 WBS 목록
     * @param tasks   프로젝트의 전체 Task 목록
     * @return WBS ID별 집계 결과
     */
    public Map<Long, TaskProgressAggregation> aggregateByWbs(
            List<Wbs> wbsList,
            List<Task> tasks) {

        List<Wbs> safeWbsList = wbsList != null ? wbsList : List.of();

        List<Task> safeTasks = tasks != null ? tasks : List.of();

        // WBS ID별 Entity 조회용 Map
        Map<Long, Wbs> wbsMap = new HashMap<>();

        // WBS ID별 집계 누적기
        Map<Long, AggregationAccumulator> accumulatorMap = new LinkedHashMap<>();

        for (Wbs wbs : safeWbsList) {

            if (wbs == null || wbs.getId() == null) {
                continue;
            }

            wbsMap.put(wbs.getId(), wbs);

            accumulatorMap.put(
                    wbs.getId(),
                    new AggregationAccumulator());
        }

        // Task를 해당 WBS부터 상위 WBS까지 누적
        for (Task task : safeTasks) {

            if (task == null || task.getWbs() == null) {
                continue;
            }

            Long currentWbsId = task.getWbs().getId();

            Set<Long> visitedWbsIds = new HashSet<>();

            while (currentWbsId != null) {

                // 비정상적인 순환 참조 방지
                if (!visitedWbsIds.add(currentWbsId)) {

                    log.warn(
                            "WBS hierarchy cycle detected. wbsId={}",
                            currentWbsId);

                    break;
                }

                Wbs currentWbs = wbsMap.get(currentWbsId);

                // 전달받은 프로젝트 WBS 목록에 없는 경우 종료
                if (currentWbs == null) {
                    break;
                }

                AggregationAccumulator accumulator = accumulatorMap.get(currentWbsId);

                if (accumulator != null) {
                    accumulator.add(task);
                }

                // 상위 WBS로 이동
                currentWbsId = currentWbs.getParent() != null
                        ? currentWbs.getParent().getId()
                        : null;
            }
        }

        // 누적 결과를 최종 DTO로 변환
        Map<Long, TaskProgressAggregation> result = new LinkedHashMap<>();

        accumulatorMap.forEach(
                (wbsId, accumulator) -> result.put(wbsId, accumulator.toResult()));

        return result;
    }

    /**
     * Project 전체 진척률 집계
     *
     * WBS 연결 여부와 관계없이 전달받은 모든 Task를 대상으로 한다.
     *
     * @param tasks 프로젝트의 전체 Task 목록
     * @return 프로젝트 집계 결과
     */
    public TaskProgressAggregation aggregateProject(
            List<Task> tasks) {

        AggregationAccumulator accumulator = new AggregationAccumulator();

        if (tasks != null) {

            for (Task task : tasks) {

                if (task != null) {
                    accumulator.add(task);
                }
            }
        }

        return accumulator.toResult();
    }

    /**
     * 집계 중간값 누적기
     *
     * 최종 반올림 전까지 계획 공수와 완료 공수를
     * 높은 정밀도로 누적한다.
     */
    private static class AggregationAccumulator {

        /**
         * 총 계획 공수
         */
        private BigDecimal totalPlannedManDays = BigDecimal.ZERO;

        /**
         * 가중 완료 공수
         */
        private BigDecimal weightedCompletedManDays = BigDecimal.ZERO;

        /**
         * 계획 공수 누락 Task 수
         */
        private long missingEffortTaskCount;

        /**
         * Task 집계값 누적
         *
         * @param task 집계 대상 Task
         */
        private void add(Task task) {

            BigDecimal plannedManDays = task.getPlannedManDays();

            // 계획 공수가 없는 Task
            if (plannedManDays == null) {

                missingEffortTaskCount++;

                return;
            }

            // 총 계획 공수 누적
            totalPlannedManDays = totalPlannedManDays.add(plannedManDays);

            // 계획 공수 × 진척률 / 100
            BigDecimal completedManDays = plannedManDays
                    .multiply(
                            BigDecimal.valueOf(
                                    task.getProgressRate()))
                    .divide(
                            BigDecimal.valueOf(100),
                            CALCULATION_SCALE,
                            ROUNDING_MODE);

            // 가중 완료 공수 누적
            weightedCompletedManDays = weightedCompletedManDays.add(
                    completedManDays);
        }

        /**
         * 누적값을 최종 집계 DTO로 변환
         *
         * @return Task 진척률 집계 결과
         */
        private TaskProgressAggregation toResult() {

            BigDecimal roundedTotalPlannedManDays = totalPlannedManDays.setScale(
                    RESULT_SCALE,
                    ROUNDING_MODE);

            BigDecimal roundedWeightedCompletedManDays = weightedCompletedManDays.setScale(
                    RESULT_SCALE,
                    ROUNDING_MODE);

            // 집계 가능한 계획 공수가 없는 경우
            if (totalPlannedManDays.compareTo(BigDecimal.ZERO) == 0) {

                return TaskProgressAggregation.builder()
                        .progressRate(null)
                        .totalPlannedManDays(
                                roundedTotalPlannedManDays)
                        .weightedCompletedManDays(
                                roundedWeightedCompletedManDays)
                        .missingEffortTaskCount(
                                missingEffortTaskCount)
                        .build();
            }

            // 가중 진척률 계산
            BigDecimal progressRate = weightedCompletedManDays
                    .divide(
                            totalPlannedManDays,
                            CALCULATION_SCALE,
                            ROUNDING_MODE)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(
                            RESULT_SCALE,
                            ROUNDING_MODE);

            return TaskProgressAggregation.builder()
                    .progressRate(progressRate)
                    .totalPlannedManDays(
                            roundedTotalPlannedManDays)
                    .weightedCompletedManDays(
                            roundedWeightedCompletedManDays)
                    .missingEffortTaskCount(
                            missingEffortTaskCount)
                    .build();
        }
    }
}
