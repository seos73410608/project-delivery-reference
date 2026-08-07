package com.seos.pmis.project.dto.response;

import com.seos.pmis.project.entity.ProjectPriority;
import com.seos.pmis.project.entity.ProjectStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class ProjectDashboardResponse {

    /**
     * 전체 프로젝트 수
     */
    private long totalProjects;


    /**
     * 상태별 프로젝트 수
     *
     * 예)
     * PLANNING : 3
     * IN_PROGRESS : 5
     * COMPLETED : 2
     */
    private Map<ProjectStatus, Long> projectStatusCount;


    /**
     * 우선순위별 프로젝트 수
     *
     * 예)
     * HIGH : 3
     * MEDIUM : 5
     * LOW : 2
     */
    private Map<ProjectPriority, Long> projectPriorityCount;


    /**
     * 최근 생성 프로젝트 수
     */
    private long recentProjects;


    /**
     * 종료 예정 프로젝트 수
     */
    private long upcomingDeadlineProjects;

}