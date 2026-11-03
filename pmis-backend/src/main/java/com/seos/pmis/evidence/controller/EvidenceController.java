package com.seos.pmis.evidence.controller;

import com.seos.pmis.common.response.ApiResponse;
import com.seos.pmis.evidence.dto.request.EvidenceCreateRequest;
import com.seos.pmis.evidence.dto.request.EvidenceSearchRequest;
import com.seos.pmis.evidence.dto.request.EvidenceUpdateRequest;
import com.seos.pmis.evidence.dto.request.EvidenceVerificationRequest;
import com.seos.pmis.evidence.dto.request.EvidenceVersionCreateRequest;
import com.seos.pmis.evidence.dto.request.EvidenceVersionUploadRequest;
import com.seos.pmis.evidence.dto.response.EvidenceResponse;
import com.seos.pmis.evidence.dto.response.EvidenceVersionResponse;
import com.seos.pmis.evidence.dto.response.EvidenceVerificationHistoryResponse;
import com.seos.pmis.evidence.enums.EvidenceType;
import com.seos.pmis.evidence.service.EvidenceService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Evidence Controller
 *
 * Evidence에 대한 REST API를 제공한다.
 *
 * API 구조
 *
 * /api/evidence-requirements/{requirementId}/evidence
 * /api/evidence/{id}
 * /api/evidence
 * /api/evidence/{id}/verification
 * /api/evidence/{id}/versions
 * /api/evidence/{id}/versions/upload
 * /api/evidence/{id}/versions/{versionNumber}
 * /api/evidence/{id}/versions/{versionNumber}/verification-history
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class EvidenceController {

    private final EvidenceService evidenceService;

    // =========================================================================
    // Evidence 조회
    // =========================================================================

    /**
     * Project 기준 Evidence 목록 조회
     * GET /api/projects/{projectId}/evidence
     */
    @GetMapping("/projects/{projectId}/evidence")
    public ApiResponse<List<EvidenceResponse>> findAllByProjectId(
            @PathVariable Long projectId
    ) {
        return ApiResponse.success(
                evidenceService.findAllByProjectId(projectId)
        );
    }

    /**
     * Requirement 기준 Evidence 목록 조회
     * GET /api/evidence-requirements/{requirementId}/evidence
     */
    @GetMapping("/evidence-requirements/{requirementId}/evidence")
    public ApiResponse<List<EvidenceResponse>> findAllByRequirementId(
            @PathVariable Long requirementId
    ) {
        return ApiResponse.success(
                evidenceService.findAllByRequirementId(requirementId)
        );
    }

    /**
     * Project + WBS 기준 Evidence 목록 조회
     * GET /api/projects/{projectId}/wbs/{wbsId}/evidence
     */
    @GetMapping("/projects/{projectId}/wbs/{wbsId}/evidence")
    public ApiResponse<List<EvidenceResponse>> findAllByProjectIdAndWbsId(
            @PathVariable Long projectId,
            @PathVariable Long wbsId
    ) {
        return ApiResponse.success(
                evidenceService.findAllByProjectIdAndWbsId(
                        projectId,
                        wbsId
                )
        );
    }

    /**
     * Evidence 단건 조회
     * GET /api/evidence/{id}
     */
    @GetMapping("/evidence/{id}")
    public ApiResponse<EvidenceResponse> findById(
            @PathVariable Long id
    ) {
        return ApiResponse.success(
                evidenceService.findById(id)
        );
    }

    /**
     * Evidence 검색
     * GET /api/evidence
     */
    @GetMapping("/evidence")
    public ApiResponse<Page<EvidenceResponse>> search(
            @ModelAttribute EvidenceSearchRequest request
    ) {
        return ApiResponse.success(
                evidenceService.search(request)
        );
    }

    // =========================================================================
    // Evidence 생성
    // =========================================================================

    /**
     * Evidence 생성
     * POST /api/evidence-requirements/{requirementId}/evidence
     */
    @PostMapping("/evidence-requirements/{requirementId}/evidence")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<EvidenceResponse> create(
            @PathVariable Long requirementId,
            @Valid @RequestBody EvidenceCreateRequest request
    ) {
        return ApiResponse.success(
                evidenceService.create(requirementId, request)
        );
    }

    // =========================================================================
    // Evidence 버전 관리
    // =========================================================================

    /**
     * Evidence 신규 버전 생성 (기존 JSON 방식)
     * POST /api/evidence/{id}/versions
     */
    @PostMapping("/evidence/{id}/versions")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<EvidenceVersionResponse> createVersion(
            @PathVariable Long id,
            @Valid @RequestBody EvidenceVersionCreateRequest request
    ) {
        return ApiResponse.success(
                evidenceService.createVersion(id, request)
        );
    }

    /**
     * Evidence 신규 버전 파일 업로드
     * POST /api/evidence/{id}/versions/upload
     *
     * Content-Type: multipart/form-data
     *
     * Multipart fields:
     * - file: 업로드 파일
     * - evidenceType: Evidence 유형 (EvidenceType enum 값)
     * - title: 버전 제목
     * - description: 설명 (선택)
     *
     * 파일 바이너리는 Automation Runtime에 저장하고,
     * PMIS에는 Runtime file_id와 버전 메타데이터를 저장한다.
     */
    @PostMapping(
            value = "/evidence/{id}/versions/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<EvidenceVersionResponse> uploadVersion(
            @PathVariable Long id,

            @RequestPart("file")
            MultipartFile file,

            @RequestParam("evidenceType")
            EvidenceType evidenceType,

            @RequestParam("title")
            String title,

            @RequestParam(value = "description", required = false)
            String description
    ) {
        EvidenceVersionUploadRequest request =
                new EvidenceVersionUploadRequest();

        request.setEvidenceType(evidenceType);
        request.setTitle(title);
        request.setDescription(description);

        return ApiResponse.success(
                evidenceService.uploadVersion(id, file, request)
        );
    }

    /**
     * Evidence 버전 목록 조회
     * GET /api/evidence/{id}/versions
     */
    @GetMapping("/evidence/{id}/versions")
    public ApiResponse<List<EvidenceVersionResponse>> findAllVersions(
            @PathVariable Long id
    ) {
        return ApiResponse.success(
                evidenceService.findAllVersions(id)
        );
    }

    /**
     * Evidence 특정 버전 상세 조회
     * GET /api/evidence/{id}/versions/{versionNumber}
     */
    @GetMapping("/evidence/{id}/versions/{versionNumber}")
    public ApiResponse<EvidenceVersionResponse> findVersion(
            @PathVariable Long id,
            @PathVariable Integer versionNumber
    ) {
        return ApiResponse.success(
                evidenceService.findVersion(id, versionNumber)
        );
    }

    /**
     * Evidence 특정 버전의 검증 이력 조회
     * GET /api/evidence/{id}/versions/{versionNumber}/verification-history
     */
    @GetMapping(
            "/evidence/{id}/versions/{versionNumber}/verification-history"
    )
    public ApiResponse<List<EvidenceVerificationHistoryResponse>>
    findVersionVerificationHistory(
            @PathVariable Long id,
            @PathVariable Integer versionNumber
    ) {
        return ApiResponse.success(
                evidenceService.findVersionVerificationHistory(
                        id,
                        versionNumber
                )
        );
    }

    // =========================================================================
    // Evidence 수정
    // =========================================================================

    /**
     * Evidence 수정
     * PUT /api/evidence/{id}
     */
    @PutMapping("/evidence/{id}")
    public ApiResponse<EvidenceResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody EvidenceUpdateRequest request
    ) {
        return ApiResponse.success(
                evidenceService.update(id, request)
        );
    }

    // =========================================================================
    // Evidence 삭제
    // =========================================================================

    /**
     * Evidence 삭제
     * DELETE /api/evidence/{id}
     *
     * Inspection Item에서 사용 중인 Evidence는
     * Service에서 삭제를 차단한다.
     */
    @DeleteMapping("/evidence/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        evidenceService.delete(id);
    }

    // =========================================================================
    // Evidence Verification
    // =========================================================================

    /**
     * Evidence 검증 상태 변경
     * PATCH /api/evidence/{id}/verification
     */
    @PatchMapping("/evidence/{id}/verification")
    public ApiResponse<EvidenceResponse> verify(
            @PathVariable Long id,
            @Valid @RequestBody EvidenceVerificationRequest request
    ) {
        return ApiResponse.success(
                evidenceService.verify(id, request)
        );
    }

    /**
     * 특정 Evidence Version 검증
     * PATCH /api/evidence/{id}/versions/{versionNumber}/verification
     */
    @PatchMapping(
            "/evidence/{id}/versions/{versionNumber}/verification"
    )
    public ApiResponse<EvidenceResponse> verifyVersion(
            @PathVariable Long id,
            @PathVariable @Min(1) Integer versionNumber,
            @Valid @RequestBody EvidenceVerificationRequest request
    ) {
        return ApiResponse.success(
                evidenceService.verifyVersion(id, versionNumber, request)
        );
    }
}