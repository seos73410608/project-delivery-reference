
package com.seos.pmis.projectoperation.controller;

import com.seos.common.response.ApiResponse;
import com.seos.pmis.projectoperation.dto.request.ProjectOperationCreateRequest;
import com.seos.pmis.projectoperation.dto.request.ProjectOperationStatusChangeRequest;
import com.seos.pmis.projectoperation.dto.request.ProjectOperationUpdateRequest;
import com.seos.pmis.projectoperation.dto.response.ProjectOperationResponse;
import com.seos.pmis.projectoperation.service.ProjectOperationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "ProjectOperation",
        description = "프로젝트와 운영 서비스 관계 관리 API"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project-operations")
public class ProjectOperationController {

    private final ProjectOperationService projectOperationService;

    /**
     * 프로젝트 운영 관계 생성
     */
    @Operation(
            summary = "프로젝트 운영 관계 생성",
            description = "프로젝트와 운영 서비스를 연결합니다."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProjectOperationResponse> create(
            @Valid @RequestBody ProjectOperationCreateRequest request
    ) {
        return ApiResponse.success(
                "프로젝트 운영 관계가 생성되었습니다.",
                projectOperationService.createResponse(
                        request.getProjectId(),
                        request.getOperationId(),
                        request.getRelationshipType(),
                        request.getEffectiveFrom(),
                        request.getEffectiveTo(),
                        request.getDescription()
                )
        );
    }

    /**
     * 프로젝트 운영 관계 상세 조회
     */
    @Operation(
            summary = "프로젝트 운영 관계 상세 조회",
            description = "ID를 이용하여 프로젝트 운영 관계를 조회합니다."
    )
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    public ApiResponse<ProjectOperationResponse> getDetail(
            @PathVariable Long id
    ) {
        return ApiResponse.success(
                projectOperationService.getDetailResponse(id)
        );
    }

    /**
     * 프로젝트 기준 운영 관계 목록 조회
     */
    @Operation(
            summary = "프로젝트 기준 운영 관계 조회",
            description = "프로젝트 ID에 연결된 운영 관계 목록을 조회합니다."
    )
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/by-project/{projectId}")
    public ApiResponse<List<ProjectOperationResponse>> findByProject(
            @PathVariable Long projectId
    ) {
        return ApiResponse.success(
                projectOperationService.findByProjectResponses(projectId)
        );
    }

    /**
     * 운영 서비스 기준 프로젝트 관계 목록 조회
     */
    @Operation(
            summary = "운영 서비스 기준 프로젝트 관계 조회",
            description = "운영 서비스 ID에 연결된 프로젝트 관계 목록을 조회합니다."
    )
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/by-operation/{operationId}")
    public ApiResponse<List<ProjectOperationResponse>> findByOperation(
            @PathVariable Long operationId
    ) {
        return ApiResponse.success(
                projectOperationService.findByOperationResponses(operationId)
        );
    }

    /**
     * 프로젝트 운영 관계 정보 수정
     */
    @Operation(
            summary = "프로젝트 운영 관계 수정",
            description = "프로젝트 운영 관계 유형, 적용 기간 및 설명을 수정합니다."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ApiResponse<ProjectOperationResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ProjectOperationUpdateRequest request
    ) {
        return ApiResponse.success(
                "프로젝트 운영 관계가 수정되었습니다.",
                projectOperationService.updateResponse(
                        id,
                        request.getRelationshipType(),
                        request.getEffectiveFrom(),
                        request.getEffectiveTo(),
                        request.getDescription()
                )
        );
    }

    /**
     * 프로젝트 운영 관계 상태 변경
     */
    @Operation(
            summary = "프로젝트 운영 관계 상태 변경",
            description = "프로젝트 운영 관계의 상태를 변경합니다."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public ApiResponse<ProjectOperationResponse> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody ProjectOperationStatusChangeRequest request
    ) {
        return ApiResponse.success(
                "프로젝트 운영 관계 상태가 변경되었습니다.",
                projectOperationService.changeStatusResponse(
                        id,
                        request.getStatus()
                )
        );
    }
}
