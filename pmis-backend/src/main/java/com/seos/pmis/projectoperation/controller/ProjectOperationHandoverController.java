
package com.seos.pmis.projectoperation.controller;

import com.seos.common.response.ApiResponse;
import com.seos.pmis.auth.security.principal.UserPrincipal;
import com.seos.pmis.projectoperation.dto.request.ProjectOperationHandoverInitialCreateRequest;
import com.seos.pmis.projectoperation.dto.request.ProjectOperationHandoverOperatorChangeCreateRequest;
import com.seos.pmis.projectoperation.dto.request.ProjectOperationHandoverReHandoverCreateRequest;
import com.seos.pmis.projectoperation.dto.response.ProjectOperationHandoverResponse;
import com.seos.pmis.projectoperation.service.ProjectOperationHandoverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(
        name = "ProjectOperationHandover",
        description = "프로젝트 운영 인수인계 관리 API"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project-operation-handovers")
public class ProjectOperationHandoverController {

    private final ProjectOperationHandoverService handoverService;

    /**
     * 최초 인수인계 생성
     */
    @Operation(summary = "최초 인수인계 생성")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/initial")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProjectOperationHandoverResponse> createInitial(
            @Valid @RequestBody ProjectOperationHandoverInitialCreateRequest request) {

        return ApiResponse.success(
                handoverService.createInitialResponse(
                        request.getProjectOperationId(),
                        request.getToParticipantId(),
                        request.getPlannedHandoverDate(),
                        request.getNotes()
                )
        );
    }

    /**
     * 운영자 변경 인수인계 생성
     */
    @Operation(summary = "운영자 변경 인수인계 생성")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/operator-change")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProjectOperationHandoverResponse> createOperatorChange(
            @Valid @RequestBody ProjectOperationHandoverOperatorChangeCreateRequest request) {

        return ApiResponse.success(
                handoverService.createOperatorChangeResponse(
                        request.getProjectOperationId(),
                        request.getFromParticipantId(),
                        request.getToParticipantId(),
                        request.getPlannedHandoverDate(),
                        request.getNotes()
                )
        );
    }

    /**
     * 동일 운영자 재인수인계 생성
     */
    @Operation(summary = "재인수인계 생성")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/re-handover")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProjectOperationHandoverResponse> createReHandover(
            @Valid @RequestBody ProjectOperationHandoverReHandoverCreateRequest request) {

        return ApiResponse.success(
                handoverService.createReHandoverResponse(
                        request.getProjectOperationId(),
                        request.getFromParticipantId(),
                        request.getToParticipantId(),
                        request.getPlannedHandoverDate(),
                        request.getNotes()
                )
        );
    }

    /**
     * 인수인계 상세 조회
     */
    @Operation(summary = "인수인계 상세 조회")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    public ApiResponse<ProjectOperationHandoverResponse> getDetail(
            @PathVariable Long id) {

        return ApiResponse.success(
                handoverService.getDetailResponse(id)
        );
    }

    /**
     * 프로젝트 운영 관계별 인수인계 이력 조회
     */
    @Operation(summary = "프로젝트 운영 관계별 인수인계 이력 조회")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/by-project-operation/{projectOperationId}")
    public ApiResponse<List<ProjectOperationHandoverResponse>> getHistory(
            @PathVariable Long projectOperationId) {

        return ApiResponse.success(
                handoverService.getHistoryResponses(projectOperationId)
        );
    }

    /**
     * 인수인계 요청
     */
    @Operation(summary = "인수인계 요청")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/request")
    public ApiResponse<ProjectOperationHandoverResponse> request(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        return ApiResponse.success(
                handoverService.requestResponse(
                        id,
                        principal.getUserId(),
                        LocalDateTime.now()
                )
        );
    }

    /**
     * 인수인계 진행 시작
     */
    @Operation(summary = "인수인계 진행 시작")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/start")
    public ApiResponse<ProjectOperationHandoverResponse> start(
            @PathVariable Long id) {

        return ApiResponse.success(
                handoverService.startResponse(id)
        );
    }

    /**
     * 인수인계 검토 요청
     */
    @Operation(summary = "인수인계 검토 요청")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/submit-for-review")
    public ApiResponse<ProjectOperationHandoverResponse> submitForReview(
            @PathVariable Long id) {

        return ApiResponse.success(
                handoverService.submitForReviewResponse(id)
        );
    }

    /**
     * 인수인계 승인
     */
    @Operation(summary = "인수인계 승인")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/accept")
    public ApiResponse<ProjectOperationHandoverResponse> accept(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        return ApiResponse.success(
                handoverService.acceptResponse(
                        id,
                        principal.getUserId(),
                        LocalDateTime.now()
                )
        );
    }

    /**
     * 인수인계 완료
     */
    @Operation(summary = "인수인계 완료")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/complete")
    public ApiResponse<ProjectOperationHandoverResponse> complete(
            @PathVariable Long id) {

        return ApiResponse.success(
                handoverService.completeResponse(
                        id,
                        LocalDateTime.now()
                )
        );
    }

    /**
     * 인수인계 반려
     */
    @Operation(summary = "인수인계 반려")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/reject")
    public ApiResponse<ProjectOperationHandoverResponse> reject(
            @PathVariable Long id) {

        return ApiResponse.success(
                handoverService.rejectResponse(id)
        );
    }

    /**
     * 인수인계 취소
     */
    @Operation(summary = "인수인계 취소")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/cancel")
    public ApiResponse<ProjectOperationHandoverResponse> cancel(
            @PathVariable Long id) {

        return ApiResponse.success(
                handoverService.cancelResponse(id)
        );
    }
}
