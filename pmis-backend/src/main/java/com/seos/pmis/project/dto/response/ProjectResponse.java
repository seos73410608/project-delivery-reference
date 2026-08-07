package com.seos.pmis.project.dto.response;

import com.seos.pmis.project.entity.ProjectPriority;
import com.seos.pmis.project.entity.ProjectStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class ProjectResponse {

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
     * 생성일시
     */
    private LocalDateTime createdAt;

    /**
     * 수정일시
     */
    private LocalDateTime updatedAt;

}