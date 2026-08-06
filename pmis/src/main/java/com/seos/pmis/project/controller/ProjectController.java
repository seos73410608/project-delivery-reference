package com.seos.pmis.project.controller;

import com.seos.pmis.common.response.ApiResponse;
import com.seos.pmis.project.dto.request.CreateProjectRequest;
import com.seos.pmis.project.dto.request.UpdateProjectRequest;
import com.seos.pmis.project.dto.response.ProjectResponse;
import com.seos.pmis.project.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    /**
     * 프로젝트 생성
     */
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
     * 프로젝트 단건 조회
     */
    @GetMapping("/{projectId}")
    public ApiResponse<ProjectResponse> getProject(
            @PathVariable Long projectId
    ) {

        return ApiResponse.success(
                projectService.getProject(projectId)
        );
    }

    /**
     * 프로젝트 목록 조회
     */
    @GetMapping
    public ApiResponse<Page<ProjectResponse>> getProjects(
            Pageable pageable
    ) {

        return ApiResponse.success(
                projectService.getProjects(pageable)
        );
    }

    /**
     * 프로젝트 수정
     */
    @PutMapping("/{projectId}")
    public ApiResponse<ProjectResponse> updateProject(
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
    @DeleteMapping("/{projectId}")
    public ApiResponse<Void> deleteProject(
            @PathVariable Long projectId
    ) {

        projectService.delete(projectId);

        return ApiResponse.success(
                "프로젝트가 삭제되었습니다."
        );
    }

}