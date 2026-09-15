package com.seos.pmis.risk.controller;

import com.seos.pmis.common.response.ApiResponse;
import com.seos.pmis.risk.dto.request.RiskCreateRequest;
import com.seos.pmis.risk.dto.request.RiskSearchRequest;
import com.seos.pmis.risk.dto.request.RiskStatusUpdateRequest;
import com.seos.pmis.risk.dto.request.RiskUpdateRequest;
import com.seos.pmis.risk.dto.response.RiskResponse;
import com.seos.pmis.risk.service.RiskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RiskController {

    private final RiskService riskService;

    /**
     * Risk 단건 조회
     */
    @GetMapping("/risks/{riskId}")
    public ApiResponse<RiskResponse> getRisk(
            @PathVariable Long riskId
    ) {
        return ApiResponse.success(
                riskService.getRisk(riskId)
        );
    }

    /**
     * Risk 수정
     *
     * Risk Key와 Project ID는 수정하지 않는다.
     */
    @PutMapping("/risks/{riskId}")
    public ApiResponse<RiskResponse> updateRisk(
            @PathVariable Long riskId,
            @Valid @RequestBody RiskUpdateRequest request
    ) {
        return ApiResponse.success(
                riskService.updateRisk(
                        riskId,
                        request
                )
        );
    }

    /**
     * Risk 삭제
     */
    @DeleteMapping("/risks/{riskId}")
    public ApiResponse<Void> deleteRisk(
            @PathVariable Long riskId
    ) {
        riskService.deleteRisk(riskId);

        return ApiResponse.success(null);
    }

    /**
     * Project별 Risk 목록 조회
     *
     * projectId는 URL Path Parameter를 기준으로
     * Project 범위를 강제한다.
     */
    @GetMapping("/projects/{projectId}/risks")
    public ApiResponse<Page<RiskResponse>> getProjectRisks(
            @PathVariable Long projectId,
            RiskSearchRequest request
    ) {
        return ApiResponse.success(
                riskService.getProjectRisks(
                        projectId,
                        request
                )
        );
    }

    /**
     * Project별 Risk 등록
     *
     * Risk Key는 Backend에서 자동 생성한다.
     */
    @PostMapping("/projects/{projectId}/risks")
    public ApiResponse<RiskResponse> createRisk(
            @PathVariable Long projectId,
            @Valid @RequestBody RiskCreateRequest request
    ) {
        return ApiResponse.success(
                riskService.createRisk(
                        projectId,
                        request
                )
        );
    }

    /**
     * Risk 전체 검색
     */
    @GetMapping("/risks")
    public ApiResponse<Page<RiskResponse>> searchRisks(
            RiskSearchRequest request
    ) {
        return ApiResponse.success(
                riskService.searchRisks(request)
        );
    }

    /**
     * Risk 상태 변경
     */
    @PatchMapping("/risks/{riskId}/status")
    public ApiResponse<RiskResponse> updateStatus(
            @PathVariable Long riskId,
            @Valid @RequestBody RiskStatusUpdateRequest request
    ) {
        return ApiResponse.success(
                riskService.updateStatus(
                        riskId,
                        request
                )
        );
    }
}