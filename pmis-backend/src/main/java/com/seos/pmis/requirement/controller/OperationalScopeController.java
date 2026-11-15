package com.seos.pmis.requirement.controller;

import com.seos.common.response.ApiResponse;
import com.seos.pmis.requirement.dto.request.OperationalScopeCreateRequest;
import com.seos.pmis.requirement.dto.response.OperationalScopeResponse;
import com.seos.pmis.requirement.service.OperationalScopeCreationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
}