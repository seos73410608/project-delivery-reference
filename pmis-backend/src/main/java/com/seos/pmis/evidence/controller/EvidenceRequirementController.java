package com.seos.pmis.evidence.controller;

import com.seos.pmis.common.response.ApiResponse;
import com.seos.pmis.evidence.dto.request.EvidenceRequirementCreateRequest;
import com.seos.pmis.evidence.dto.request.EvidenceRequirementSearchRequest;
import com.seos.pmis.evidence.dto.request.EvidenceRequirementUpdateRequest;
import com.seos.pmis.evidence.dto.response.EvidenceRequirementResponse;
import com.seos.pmis.evidence.service.EvidenceRequirementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Evidence Requirement Controller
 *
 * Evidence Requirement에 대한 REST API를 제공한다.
 *
 * API 구조
 *
 * /api/projects/{projectId}/evidence-requirements
 * /api/evidence-requirements/{id}
 * /api/evidence-requirements
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class EvidenceRequirementController {

    private final EvidenceRequirementService evidenceRequirementService;

    /**
     * =========================================================================
     * Evidence Requirement 조회
     * =========================================================================
     */

    /**
     * Project 기준 Evidence Requirement 목록 조회
     *
     * GET
     * /api/projects/{projectId}/evidence-requirements
     */
    @GetMapping(
            "/projects/{projectId}/evidence-requirements"
    )
    public ApiResponse<List<EvidenceRequirementResponse>> findAllByProjectId(
            @PathVariable Long projectId
    ) {
        return ApiResponse.success(
                evidenceRequirementService.findAllByProjectId(
                        projectId
                )
        );
    }

    /**
     * Evidence Requirement 단건 조회
     *
     * GET
     * /api/evidence-requirements/{id}
     */
    @GetMapping("/evidence-requirements/{id}")
    public ApiResponse<EvidenceRequirementResponse> findById(
            @PathVariable Long id
    ) {
        return ApiResponse.success(
                evidenceRequirementService.findById(id)
        );
    }

    /**
     * Evidence Requirement 검색
     *
     * GET
     * /api/evidence-requirements
     *
     * 예:
     *
     * /api/evidence-requirements?projectId=1
     * /api/evidence-requirements?projectId=1&status=MISSING
     * /api/evidence-requirements?keyword=설치
     */
    @GetMapping("/evidence-requirements")
    public ApiResponse<Page<EvidenceRequirementResponse>> search(
            @ModelAttribute EvidenceRequirementSearchRequest request
    ) {
        return ApiResponse.success(
                evidenceRequirementService.search(
                        request
                )
        );
    }

    /**
     * =========================================================================
     * Evidence Requirement 생성
     * =========================================================================
     */

    /**
     * Evidence Requirement 생성
     *
     * POST
     * /api/projects/{projectId}/evidence-requirements
     */
    @PostMapping(
            "/projects/{projectId}/evidence-requirements"
    )
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<EvidenceRequirementResponse> create(
            @PathVariable Long projectId,
            @Valid @RequestBody EvidenceRequirementCreateRequest request
    ) {
        return ApiResponse.success(
                evidenceRequirementService.create(
                        projectId,
                        request
                )
        );
    }

    /**
     * =========================================================================
     * Evidence Requirement 수정
     * =========================================================================
     */

    /**
     * Evidence Requirement 수정
     *
     * PUT
     * /api/evidence-requirements/{id}
     */
    @PutMapping("/evidence-requirements/{id}")
    public ApiResponse<EvidenceRequirementResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody EvidenceRequirementUpdateRequest request
    ) {
        return ApiResponse.success(
                evidenceRequirementService.update(
                        id,
                        request
                )
        );
    }

    /**
     * =========================================================================
     * Evidence Requirement 삭제
     * =========================================================================
     */

    /**
     * Evidence Requirement 삭제
     *
     * DELETE
     * /api/evidence-requirements/{id}
     *
     * Evidence가 등록된 Requirement는
     * Service에서 삭제를 차단한다.
     */
    @DeleteMapping("/evidence-requirements/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id
    ) {
        evidenceRequirementService.delete(id);
    }
}