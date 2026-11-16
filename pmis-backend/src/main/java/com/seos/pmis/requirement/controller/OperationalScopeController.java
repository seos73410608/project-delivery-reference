
package com.seos.pmis.requirement.controller;

import com.seos.common.response.ApiResponse;
import com.seos.pmis.requirement.dto.request.OperationalScopeCreateRequest;
import com.seos.pmis.requirement.dto.request.OperationalScopeSearchRequest;
import com.seos.pmis.requirement.dto.response.OperationalScopeResponse;
import com.seos.pmis.requirement.service.OperationalScopeCreationService;
import com.seos.pmis.requirement.service.OperationalScopeQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Operational Scope",
        description = "운영 환경 범위 관리 API"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/operational-scopes")
public class OperationalScopeController {

    private final OperationalScopeCreationService operationalScopeCreationService;
    private final OperationalScopeQueryService operationalScopeQueryService;

    /**
     * Operational Scope 생성
     */
    @Operation(
            summary = "Operational Scope 생성",
            description = "신규 운영 환경 범위를 생성합니다."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<OperationalScopeResponse> create(
            @Valid @RequestBody OperationalScopeCreateRequest request
    ) {

        return ApiResponse.success(
                "Operational Scope가 생성되었습니다.",
                operationalScopeCreationService.create(request)
        );
    }

    /**
     * Operational Scope 검색
     */
    @Operation(
            summary = "Operational Scope 검색",
            description = "검색 조건과 페이징 정보를 이용하여 운영 환경 범위를 조회합니다."
    )
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ApiResponse<Page<OperationalScopeResponse>> search(
            @ModelAttribute OperationalScopeSearchRequest request,
            Pageable pageable
    ) {

        return ApiResponse.success(
                operationalScopeQueryService.search(request, pageable)
        );
    }

    /**
     * Operational Scope 상세 조회
     */
    @Operation(
            summary = "Operational Scope 상세 조회",
            description = "ID를 이용하여 운영 환경 범위의 상세 정보를 조회합니다."
    )
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    public ApiResponse<OperationalScopeResponse> getDetail(
            @PathVariable Long id
    ) {

        return ApiResponse.success(
                operationalScopeQueryService.getDetail(id)
        );
    }
}
