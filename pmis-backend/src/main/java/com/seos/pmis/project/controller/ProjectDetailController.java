package com.seos.pmis.project.controller;

import com.seos.pmis.common.response.ApiResponse;
import com.seos.pmis.project.dto.response.ProjectDetailResponse;
import com.seos.pmis.project.service.ProjectDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Project Detail",
        description = "프로젝트 상세 관리 API"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class ProjectDetailController {

    private final ProjectDetailService projectDetailService;

    /**
     * 프로젝트 상세 조회
     */
    @Operation(
            summary = "프로젝트 상세 조회",
            description = "프로젝트 ID로 프로젝트 상세 정보를 조회합니다."
    )
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{projectId}/detail")
    public ApiResponse<ProjectDetailResponse> getProjectDetail(
            @Parameter(description = "프로젝트 ID")
            @PathVariable Long projectId
    ) {

        return ApiResponse.success(
                projectDetailService.getProjectDetail(projectId)
        );
    }
}