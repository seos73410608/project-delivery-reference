package com.seos.pmis.evidence.controller;

import com.seos.pmis.common.response.ApiResponse;
import com.seos.pmis.evidence.dto.request.InspectionCreateRequest;
import com.seos.pmis.evidence.dto.request.InspectionItemCreateRequest;
import com.seos.pmis.evidence.dto.request.InspectionItemResultUpdateRequest;
import com.seos.pmis.evidence.dto.request.InspectionItemUpdateRequest;
import com.seos.pmis.evidence.dto.request.InspectionStatusUpdateRequest;
import com.seos.pmis.evidence.dto.request.InspectionUpdateRequest;
import com.seos.pmis.evidence.dto.response.InspectionItemResponse;
import com.seos.pmis.evidence.dto.response.InspectionResponse;
import com.seos.pmis.evidence.enums.InspectionStatus;
import com.seos.pmis.evidence.service.InspectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Inspection Controller
 *
 * Inspection 및 Inspection Item에 대한
 * REST API를 제공한다.
 *
 * API 구조
 *
 * /api/projects/{projectId}/inspections
 * /api/inspections/{id}
 * /api/inspections/{inspectionId}/items
 * /api/inspection-items/{itemId}/result
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class InspectionController {

    private final InspectionService inspectionService;

    /**
     * =========================================================================
     * Inspection 조회
     * =========================================================================
     */

    /**
     * Project 기준 Inspection 목록 조회
     *
     * GET
     * /api/projects/{projectId}/inspections
     */
    @GetMapping("/projects/{projectId}/inspections")
    public ApiResponse<List<InspectionResponse>> findAllByProjectId(
            @PathVariable Long projectId
    ) {
        return ApiResponse.success(
                inspectionService.findAllByProjectId(projectId)
        );
    }

    /**
     * Project + Status 기준 Inspection 조회
     *
     * GET
     * /api/projects/{projectId}/inspections/status/{status}
     *
     * 예:
     * /api/projects/1/inspections/status/IN_PROGRESS
     */
    @GetMapping(
            "/projects/{projectId}/inspections/status/{status}"
    )
    public ApiResponse<List<InspectionResponse>> findAllByProjectIdAndStatus(
            @PathVariable Long projectId,
            @PathVariable InspectionStatus status
    ) {
        return ApiResponse.success(
                inspectionService.findAllByProjectIdAndStatus(
                        projectId,
                        status
                )
        );
    }

    /**
     * Inspection 단건 조회
     *
     * GET
     * /api/inspections/{id}
     */
    @GetMapping("/inspections/{id}")
    public ApiResponse<InspectionResponse> findById(
            @PathVariable Long id
    ) {
        return ApiResponse.success(
                inspectionService.findById(id)
        );
    }

    /**
     * =========================================================================
     * Inspection 생성
     * =========================================================================
     */

    /**
     * Inspection 생성
     *
     * POST
     * /api/projects/{projectId}/inspections
     */
    @PostMapping("/projects/{projectId}/inspections")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<InspectionResponse> create(
            @PathVariable Long projectId,
            @Valid @RequestBody InspectionCreateRequest request
    ) {
        return ApiResponse.success(
                inspectionService.create(
                        projectId,
                        request
                )
        );
    }

    /**
     * =========================================================================
     * Inspection 수정
     * =========================================================================
     */

    /**
     * Inspection 기본 정보 수정
     *
     * PUT
     * /api/inspections/{id}
     */
    @PutMapping("/inspections/{id}")
    public ApiResponse<InspectionResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody InspectionUpdateRequest request
    ) {
        return ApiResponse.success(
                inspectionService.update(
                        id,
                        request
                )
        );
    }

    /**
     * =========================================================================
     * Inspection 상태 변경
     * =========================================================================
     */

    /**
     * Inspection 상태 변경
     *
     * PATCH
     * /api/inspections/{id}/status
     *
     * 예:
     *
     * {
     *     "status": "IN_PROGRESS"
     * }
     *
     * 또는
     *
     * {
     *     "status": "PASSED",
     *     "resultRemark": "전체 검사 항목 확인 완료"
     * }
     */
    @PatchMapping("/inspections/{id}/status")
    public ApiResponse<InspectionResponse> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody InspectionStatusUpdateRequest request
    ) {
        return ApiResponse.success(
                inspectionService.changeStatus(
                        id,
                        request
                )
        );
    }

    /**
     * =========================================================================
     * Inspection Item 조회
     * =========================================================================
     */

    /**
     * Inspection Item 목록 조회
     *
     * GET
     * /api/inspections/{inspectionId}/items
     */
    @GetMapping("/inspections/{inspectionId}/items")
    public ApiResponse<List<InspectionItemResponse>> findAllItems(
            @PathVariable Long inspectionId
    ) {
        return ApiResponse.success(
                inspectionService.findAllItems(
                        inspectionId
                )
        );
    }

    /**
     * Inspection Item 단건 조회
     *
     * GET
     * /api/inspection-items/{itemId}
     */
    @GetMapping("/inspection-items/{itemId}")
    public ApiResponse<InspectionItemResponse> findItemById(
            @PathVariable Long itemId
    ) {
        return ApiResponse.success(
                inspectionService.findItemById(
                        itemId
                )
        );
    }

    /**
     * =========================================================================
     * Inspection Item 생성
     * =========================================================================
     */

    /**
     * Inspection Item 생성
     *
     * POST
     * /api/inspections/{inspectionId}/items
     */
    @PostMapping("/inspections/{inspectionId}/items")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<InspectionItemResponse> createItem(
            @PathVariable Long inspectionId,
            @Valid @RequestBody InspectionItemCreateRequest request
    ) {
        return ApiResponse.success(
                inspectionService.createItem(
                        inspectionId,
                        request
                )
        );
    }

    /**
     * =========================================================================
     * Inspection Item 수정
     * =========================================================================
     */

    /**
     * Inspection Item 수정
     *
     * PUT
     * /api/inspection-items/{itemId}
     */
    @PutMapping("/inspection-items/{itemId}")
    public ApiResponse<InspectionItemResponse> updateItem(
            @PathVariable Long itemId,
            @Valid @RequestBody InspectionItemUpdateRequest request
    ) {
        return ApiResponse.success(
                inspectionService.updateItem(
                        itemId,
                        request
                )
        );
    }

    /**
     * =========================================================================
     * Inspection Item 결과 변경
     * =========================================================================
     */

    /**
     * Inspection Item 결과 변경
     *
     * PATCH
     * /api/inspection-items/{itemId}/result
     *
     * 예:
     *
     * {
     *     "result": "PASSED",
     *     "remark": "설치 상태 및 설정값 확인 완료"
     * }
     */
    @PatchMapping("/inspection-items/{itemId}/result")
    public ApiResponse<InspectionItemResponse> changeItemResult(
            @PathVariable Long itemId,
            @Valid @RequestBody InspectionItemResultUpdateRequest request
    ) {
        return ApiResponse.success(
                inspectionService.changeItemResult(
                        itemId,
                        request
                )
        );
    }
}