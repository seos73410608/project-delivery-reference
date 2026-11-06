
package com.seos.pmis.project.dto.response;

import com.seos.pmis.project.entity.ProjectPriority;
import com.seos.pmis.project.entity.ProjectStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
public class ProjectDetailResponse {

    /**
     * 프로젝트 PK
     */
    private Long id;

    /**
     * 프로젝트 코드
     */
    private String projectCode;

    /**
     * 프로젝트명
     */
    private String projectName;

    /**
     * 고객사
     */
    private String customerName;

    /**
     * 프로젝트 관리자(PM)
     */
    private String projectManager;

    /**
     * 프로젝트 설명
     */
    private String description;

    /**
     * 시작일
     */
    private LocalDate startDate;

    /**
     * 종료 예정일
     */
    private LocalDate endDate;

    /**
     * 프로젝트 상태
     */
    private ProjectStatus status;

    /**
     * 프로젝트 우선순위
     */
    private ProjectPriority priority;

    /**
     * 프로젝트 진척률(%)
     *
     * 전체 Task의 계획 공수를 기준으로 가중 평균 산출합니다.
     * 집계 가능한 계획 공수가 없으면 null입니다.
     */
    private BigDecimal progressRate;

    /**
     * 전체 계획 공수(MD)
     */
    private BigDecimal totalPlannedManDays;

    /**
     * 가중 완료 공수(MD)
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
}
