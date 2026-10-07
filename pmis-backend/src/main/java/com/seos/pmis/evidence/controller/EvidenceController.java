package com.seos.pmis.evidence.controller;

import com.seos.pmis.common.response.ApiResponse;
import com.seos.pmis.evidence.dto.request.EvidenceCreateRequest;
import com.seos.pmis.evidence.dto.request.EvidenceSearchRequest;
import com.seos.pmis.evidence.dto.request.EvidenceUpdateRequest;
import com.seos.pmis.evidence.dto.request.EvidenceVerificationRequest;
import com.seos.pmis.evidence.dto.response.EvidenceResponse;
import com.seos.pmis.evidence.service.EvidenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class EvidenceController {

    private final EvidenceService evidenceService;

    /**
     * =========================================================================
     * Evidence 조회
     * =========================================================================
     */

    /**
     * Project 기준 Evidence 목록 조회
     *
     * GET
     * /api/projects/{projectId}/evidence
     */
    @GetMapping("/projects/{projectId}/evidence")
    public ApiResponse<List<EvidenceResponse>> findAllByProjectId(
            @PathVariable Long projectId
    ) {
        return ApiResponse.success(
                evidenceService.findAllByProjectId(
                        projectId
                )
        );
    }

    /**
     * Requirement 기준 Evidence 목록 조회
     *
     * GET
     * /api/evidence-requirements/{requirementId}/evidence
     */
    @GetMapping(
            "/evidence-requirements/{requirementId}/evidence"
    )
    public ApiResponse<List<EvidenceResponse>> findAllByRequirementId(
            @PathVariable Long requirementId
    ) {
        return ApiResponse.success(
                evidenceService.findAllByRequirementId(
                        requirementId
                )
        );
    }

    /**
     * Project + WBS 기준 Evidence 목록 조회
     *
     * GET
     * /api/projects/{projectId}/wbs/{wbsId}/evidence
     */
    @GetMapping(
            "/projects/{projectId}/wbs/{wbsId}/evidence"
    )
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
     *
     * GET
     * /api/evidence/{id}
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
     *
     * GET
     * /api/evidence
     *
     * 예:
     *
     * /api/evidence?projectId=1
     * /api/evidence?requirementId=1
     * /api/evidence?verificationStatus=APPROVED
     * /api/evidence?keyword=설치
     */
    @GetMapping("/evidence")
    public ApiResponse<Page<EvidenceResponse>> search(
            @ModelAttribute EvidenceSearchRequest request
    ) {
        return ApiResponse.success(
                evidenceService.search(
                        request
                )
        );
    }

    /**
     * =========================================================================
     * Evidence 생성
     * =========================================================================
     */

    /**
     * Evidence 생성
     *
     * POST
     * /api/evidence-requirements/{requirementId}/evidence
     */
    @PostMapping(
            "/evidence-requirements/{requirementId}/evidence"
    )
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<EvidenceResponse> create(
            @PathVariable Long requirementId,
            @Valid @RequestBody EvidenceCreateRequest request
    ) {
        return ApiResponse.success(
                evidenceService.create(
                        requirementId,
                        request
                )
        );
    }

    /**
     * =========================================================================
     * Evidence 수정
     * =========================================================================
     */

    /**
     * Evidence 수정
     *
     * PUT
     * /api/evidence/{id}
     */
    @PutMapping("/evidence/{id}")
    public ApiResponse<EvidenceResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody EvidenceUpdateRequest request
    ) {
        return ApiResponse.success(
                evidenceService.update(
                        id,
                        request
                )
        );
    }

    /**
     * =========================================================================
     * Evidence 삭제
     * =========================================================================
     */

    /**
     * Evidence 삭제
     *
     * DELETE
     * /api/evidence/{id}
     *
     * Inspection Item에서 사용 중인 Evidence는
     * Service에서 삭제를 차단한다.
     */
    @DeleteMapping("/evidence/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id
    ) {
        evidenceService.delete(id);
    }

    /**
     * =========================================================================
     * Evidence Verification
     * =========================================================================
     */

    /**
     * Evidence Verification
     *
     * PATCH
     * /api/evidence/{id}/verification
     *
     * Request Body:
     *
     * {
     *     "status": "APPROVED",
     *     "remark": "설치 결과 및 설정값 확인 완료"
     * }
     *
     * Client는 status와 remark만 전달한다.
     *
     * verifiedBy / verifiedAt은
     * Backend에서 현재 인증 사용자와 현재 시각을
     * 기준으로 자동 처리한다.
     */
    @PatchMapping("/evidence/{id}/verification")
    public ApiResponse<EvidenceResponse> verify(
            @PathVariable Long id,
            @Valid @RequestBody EvidenceVerificationRequest request
    ) {
        return ApiResponse.success(
                evidenceService.verify(
                        id,
                        request
                )
        );
    }
}