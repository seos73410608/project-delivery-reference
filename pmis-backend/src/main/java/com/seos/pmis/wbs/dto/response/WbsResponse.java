
package com.seos.pmis.wbs.dto.response;

import com.seos.pmis.task.dto.response.TaskProgressAggregation;
import com.seos.pmis.wbs.entity.Wbs;
import com.seos.pmis.wbs.entity.WbsStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * WBS 응답
 *
 * WBS 조회 결과와 하위 Task 기반 진척률 집계 결과를
 * API Response 형태로 전달한다.
 *
 * Entity의 연관관계 객체(Project, Parent Wbs)는
 * 직접 노출하지 않고 ID 형태로 반환한다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WbsResponse {

        /**
         * WBS PK
         */
        private Long id;

        /**
         * 프로젝트 ID
         */
        private Long projectId;

        /**
         * 상위 WBS ID
         *
         * 최상위 WBS인 경우 null이다.
         */
        private Long parentId;

        /**
         * WBS 코드
         */
        private String wbsCode;

        /**
         * WBS 명
         */
        private String wbsName;

        /**
         * WBS Level
         */
        private Integer level;

        /**
         * 동일 부모 내 표시 순서
         */
        private Integer sortOrder;

        /**
         * WBS 상태
         */
        private WbsStatus status;

        /**
         * 설명
         */
        private String description;

        /**
         * WBS 가중 진척률
         *
         * 집계 가능한 계획 공수가 없으면 null이다.
         */
        private BigDecimal progressRate;

        /**
         * 하위 WBS를 포함한 총 계획 공수
         */
        private BigDecimal totalPlannedManDays;

        /**
         * 하위 WBS를 포함한 가중 완료 공수
         */
        private BigDecimal weightedCompletedManDays;

        /**
         * 계획 공수가 입력되지 않은 Task 수
         */
        private long missingEffortTaskCount;

        /**
         * 생성일시
         */
        private LocalDateTime createdAt;

        /**
         * 수정일시
         */
        private LocalDateTime updatedAt;

        /**
         * Wbs Entity → WbsResponse 변환
         *
         * 기존 호출부와의 호환성을 유지한다.
         * 집계 결과는 아직 전달되지 않은 상태이므로
         * 집계 필드는 기본값을 사용한다.
         *
         * @param wbs WBS Entity
         * @return WBS Response
         */
        public static WbsResponse from(Wbs wbs) {

                return from(wbs, null);
        }

        /**
         * Wbs Entity → WbsResponse 변환
         *
         * @param wbs         WBS Entity
         * @param aggregation Task 집계 결과
         * @return WBS Response
         */
        public static WbsResponse from(
                        Wbs wbs,
                        TaskProgressAggregation aggregation) {

                return WbsResponse.builder()
                                .id(wbs.getId())
                                .projectId(
                                                wbs.getProject() != null
                                                                ? wbs.getProject().getId()
                                                                : null)
                                .parentId(
                                                wbs.getParent() != null
                                                                ? wbs.getParent().getId()
                                                                : null)
                                .wbsCode(wbs.getWbsCode())
                                .wbsName(wbs.getWbsName())
                                .level(wbs.getLevel())
                                .sortOrder(wbs.getSortOrder())
                                .status(wbs.getStatus())
                                .description(wbs.getDescription())
                                .progressRate(
                                                aggregation != null
                                                                ? aggregation.getProgressRate()
                                                                : null)
                                .totalPlannedManDays(
                                                aggregation != null
                                                                ? aggregation.getTotalPlannedManDays()
                                                                : BigDecimal.ZERO.setScale(2))
                                .weightedCompletedManDays(
                                                aggregation != null
                                                                ? aggregation.getWeightedCompletedManDays()
                                                                : BigDecimal.ZERO.setScale(2))
                                .missingEffortTaskCount(
                                                aggregation != null
                                                                ? aggregation.getMissingEffortTaskCount()
                                                                : 0L)
                                .createdAt(wbs.getCreatedAt())
                                .updatedAt(wbs.getUpdatedAt())
                                .build();
        }
}
