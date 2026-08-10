package com.seos.pmis.project.controller;

import com.seos.pmis.common.response.ApiResponse;
import com.seos.pmis.project.dto.request.CreateProjectRequest;
import com.seos.pmis.project.dto.request.ProjectSearchRequest;
import com.seos.pmis.project.dto.request.UpdateProjectRequest;
import com.seos.pmis.project.dto.response.ProjectResponse;
import com.seos.pmis.project.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Project",
        description = "프로젝트 관리 API"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    /**
     * 프로젝트 생성
     */
    @Operation(
            summary = "프로젝트 생성",
            description = "신규 프로젝트를 생성합니다."
    )
    @PreAuthorize("hasRole('PM')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProjectResponse> createProject(
            @Valid @RequestBody CreateProjectRequest request
    ) {

        return ApiResponse.success(
                "프로젝트가 생성되었습니다.",
                projectService.create(request)
        );
    }

    /**
     * 프로젝트 검색
     */
    @Operation(
            summary = "프로젝트 검색",
            description = "검색 조건을 이용하여 프로젝트를 조회합니다."
    )
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ApiResponse<Page<ProjectResponse>> searchProjects(
            @ModelAttribute ProjectSearchRequest request,
            Pageable pageable
    ) {

        return ApiResponse.success(
                projectService.searchProjects(request, pageable)
        );
    }

    /**
     * 프로젝트 수정
     */
    @Operation(
            summary = "프로젝트 수정",
            description = "프로젝트 정보를 수정합니다."
    )
    @PreAuthorize("hasRole('PM')")
    @PutMapping("/{projectId}")
    public ApiResponse<ProjectResponse> updateProject(
            @Parameter(description = "프로젝트 ID")
            @PathVariable Long projectId,
            @Valid @RequestBody UpdateProjectRequest request
    ) {

        return ApiResponse.success(
                "프로젝트가 수정되었습니다.",
                projectService.update(projectId, request)
        );
    }

    /**
     * 프로젝트 삭제
     */
    @Operation(
            summary = "프로젝트 삭제",
            description = "프로젝트를 삭제합니다."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{projectId}")
    public ApiResponse<Void> deleteProject(
            @Parameter(description = "프로젝트 ID")
            @PathVariable Long projectId
    ) {

        projectService.delete(projectId);

        return ApiResponse.success(
                "프로젝트가 삭제되었습니다."
        );
    }
}