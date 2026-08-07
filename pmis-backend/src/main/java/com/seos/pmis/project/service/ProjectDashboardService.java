package com.seos.pmis.project.service;

import com.seos.pmis.project.dto.response.ProjectDashboardResponse;
import com.seos.pmis.project.entity.ProjectPriority;
import com.seos.pmis.project.entity.ProjectStatus;
import com.seos.pmis.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectDashboardService {


    private final ProjectRepository projectRepository;


    /**
     * 프로젝트 Dashboard 조회
     */
    public ProjectDashboardResponse getDashboard() {


        long totalProjects =
                projectRepository.count();


        Map<ProjectStatus, Long> projectStatusCount =
                new EnumMap<>(ProjectStatus.class);


        projectStatusCount.put(
                ProjectStatus.PLANNING,
                projectRepository.countByStatus(ProjectStatus.PLANNING)
        );

        projectStatusCount.put(
                ProjectStatus.IN_PROGRESS,
                projectRepository.countByStatus(ProjectStatus.IN_PROGRESS)
        );

        projectStatusCount.put(
                ProjectStatus.COMPLETED,
                projectRepository.countByStatus(ProjectStatus.COMPLETED)
        );

        projectStatusCount.put(
                ProjectStatus.ON_HOLD,
                projectRepository.countByStatus(ProjectStatus.ON_HOLD)
        );


        Map<ProjectPriority, Long> projectPriorityCount =
                new EnumMap<>(ProjectPriority.class);


        projectPriorityCount.put(
                ProjectPriority.HIGH,
                projectRepository.countByPriority(ProjectPriority.HIGH)
        );

        projectPriorityCount.put(
                ProjectPriority.MEDIUM,
                projectRepository.countByPriority(ProjectPriority.MEDIUM)
        );

        projectPriorityCount.put(
                ProjectPriority.LOW,
                projectRepository.countByPriority(ProjectPriority.LOW)
        );


        return ProjectDashboardResponse.builder()

                .totalProjects(totalProjects)

                .projectStatusCount(projectStatusCount)

                .projectPriorityCount(projectPriorityCount)

                .recentProjects(0)

                .upcomingDeadlineProjects(0)

                .build();
    }

}