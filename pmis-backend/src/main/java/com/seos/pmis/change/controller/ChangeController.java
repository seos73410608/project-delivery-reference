package com.seos.pmis.change.controller;

import com.seos.pmis.change.dto.request.ChangeApprovalRequest;
import com.seos.pmis.change.dto.request.ChangeCreateRequest;
import com.seos.pmis.change.dto.request.ChangeSearchRequest;
import com.seos.pmis.change.dto.request.ChangeStatusUpdateRequest;
import com.seos.pmis.change.dto.request.ChangeUpdateRequest;
import com.seos.pmis.change.dto.response.ChangeResponse;
import com.seos.pmis.change.service.ChangeService;
import com.seos.pmis.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChangeController {

    private final ChangeService changeService;

    /**
     * 변경 요청 단건 조회
     *
     * GET /api/changes/{id}
     */
    @GetMapping("/changes/{id}")
    public ApiResponse<ChangeResponse> getChange(
            @PathVariable Long id
    ) {
        return ApiResponse.success(
                changeService.getChange(id)
        );
    }

    /**
     * 변경 요청 전체 검색
     *
     * GET /api/changes
     */
    @GetMapping("/changes")
    public ApiResponse<Page<ChangeResponse>> searchChanges(
            @ModelAttribute ChangeSearchRequest request
    ) {
        return ApiResponse.success(
                changeService.searchChanges(request)
        );
    }

    /**
     * 프로젝트별 변경 요청 조회
     *
     * GET /api/projects/{projectId}/changes
     */
    @GetMapping("/projects/{projectId}/changes")
    public ApiResponse<Page<ChangeResponse>> getProjectChanges(
            @PathVariable Long projectId,
            @ModelAttribute ChangeSearchRequest request
    ) {
        return ApiResponse.success(
                changeService.getProjectChanges(projectId, request)
        );
    }

    /**
     * 변경 요청 생성
     *
     * POST /api/projects/{projectId}/changes
     */
    @PostMapping("/projects/{projectId}/changes")
    public ApiResponse<ChangeResponse> createChange(
            @PathVariable Long projectId,
            @Valid @RequestBody ChangeCreateRequest request
    ) {
        return ApiResponse.success(
                changeService.createChange(projectId, request)
        );
    }

    /**
     * 변경 요청 수정
     *
     * PUT /api/changes/{id}
     */
    @PutMapping("/changes/{id}")
    public ApiResponse<ChangeResponse> updateChange(
            @PathVariable Long id,
            @Valid @RequestBody ChangeUpdateRequest request
    ) {
        return ApiResponse.success(
                changeService.updateChange(id, request)
        );
    }

    /**
     * 변경 요청 삭제
     *
     * DELETE /api/changes/{id}
     */
    @DeleteMapping("/changes/{id}")
    public ApiResponse<Void> deleteChange(
            @PathVariable Long id
    ) {
        changeService.deleteChange(id);

        return ApiResponse.success(null);
    }

    /**
     * 변경 요청 상태 변경
     *
     * PATCH /api/changes/{id}/status
     */
    @PatchMapping("/changes/{id}/status")
    public ApiResponse<ChangeResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody ChangeStatusUpdateRequest request
    ) {
        return ApiResponse.success(
                changeService.updateStatus(id, request)
        );
    }

    /**
     * 변경 요청 승인 / 반려
     *
     * PATCH /api/changes/{id}/approval
     */
    @PatchMapping("/changes/{id}/approval")
    public ApiResponse<ChangeResponse> updateApproval(
            @PathVariable Long id,
            @Valid @RequestBody ChangeApprovalRequest request
    ) {
        return ApiResponse.success(
                changeService.updateApproval(id, request)
        );
    }
}