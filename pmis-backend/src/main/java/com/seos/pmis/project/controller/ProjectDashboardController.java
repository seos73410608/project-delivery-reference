package com.seos.pmis.project.controller;

import com.seos.pmis.common.response.ApiResponse;
import com.seos.pmis.project.dto.response.ProjectDashboardResponse;
import com.seos.pmis.project.service.ProjectDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Project Dashboard",
        description = "프로젝트 Dashboard API"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects/dashboard")
public class ProjectDashboardController {

    private final ProjectDashboardService projectDashboardService;


    /**
     * 프로젝트 Dashboard 조회
     */
    @Operation(
            summary = "프로젝트 Dashboard 조회",
            description = "프로젝트 현황 통계를 조회합니다."
    )
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ApiResponse<ProjectDashboardResponse> getDashboard() {

        return ApiResponse.success(
                projectDashboardService.getDashboard()
        );
    }
}