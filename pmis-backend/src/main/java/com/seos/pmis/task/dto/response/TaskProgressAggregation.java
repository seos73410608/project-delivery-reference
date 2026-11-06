
package com.seos.pmis.task.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Task 진척률 집계 결과
 *
 * Task의 계획 공수와 진척률을 기반으로
 * WBS 및 Project의 가중 진척률을 계산한 결과를 전달한다.
 *
 * WBS 집계에서는 해당 WBS와 모든 하위 WBS에 연결된 Task를 대상으로 한다.
 * Project 집계에서는 해당 프로젝트의 모든 Task를 대상으로 한다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskProgressAggregation {

    /**
     * 가중 진척률 (%)
     *
     * 계획 공수가 존재하는 Task가 없는 경우 null이다.
     */
    private BigDecimal progressRate;

    /**
     * 총 계획 공수 (Man-Day)
     *
     * 계획 공수가 입력된 Task의 공수 합계이다.
     */
    private BigDecimal totalPlannedManDays;

    /**
     * 가중 완료 공수 (Man-Day)
     *
     * Task별 계획 공수 × 진척률의 합계이다.
     */
    private BigDecimal weightedCompletedManDays;

    /**
     * 계획 공수 누락 Task 수
     *
     * 집계 대상 중 plannedManDays가 null인 Task의 개수이다.
     */
    private long missingEffortTaskCount;
}
