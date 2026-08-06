package com.seos.pmis.project.dto.request;

import com.seos.pmis.project.entity.ProjectPriority;
import com.seos.pmis.project.entity.ProjectStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "프로젝트 검색 조건")
public class ProjectSearchRequest {

    /**
     * 프로젝트 코드
     */
    @Schema(
            description = "프로젝트 코드",
            example = "PMIS-2026-001"
    )
    private String projectCode;

    /**
     * 프로젝트명
     */
    @Schema(
            description = "프로젝트명",
            example = "PMIS 구축"
    )
    private String projectName;

    /**
     * 고객사
     */
    @Schema(
            description = "고객사",
            example = "OpenAI"
    )
    private String customerName;

    /**
     * 프로젝트 관리자(PM)
     */
    @Schema(
            description = "프로젝트 관리자",
            example = "홍길동"
    )
    private String projectManager;

    /**
     * 프로젝트 상태
     */
    @Schema(
            description = "프로젝트 상태"
    )
    private ProjectStatus status;

    /**
     * 프로젝트 우선순위
     */
    @Schema(
            description = "프로젝트 우선순위"
    )
    private ProjectPriority priority;

    /**
     * 시작일 From
     */
    @Schema(
            description = "프로젝트 시작일(From)",
            example = "2026-01-01"
    )
    private LocalDate startDateFrom;

    /**
     * 시작일 To
     */
    @Schema(
            description = "프로젝트 시작일(To)",
            example = "2026-12-31"
    )
    private LocalDate startDateTo;

    /**
     * 종료일 From
     */
    @Schema(
            description = "프로젝트 종료일(From)",
            example = "2026-01-01"
    )
    private LocalDate endDateFrom;

    /**
     * 종료일 To
     */
    @Schema(
            description = "프로젝트 종료일(To)",
            example = "2026-12-31"
    )
    private LocalDate endDateTo;
}