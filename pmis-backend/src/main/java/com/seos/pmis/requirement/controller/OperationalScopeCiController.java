
package com.seos.pmis.requirement.controller;

import com.seos.common.response.ApiResponse;
import com.seos.pmis.requirement.dto.response.OperationalScopeCiResponse;
import com.seos.pmis.requirement.service.OperationalScopeCiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Operational Scope CI",
        description = "Operational Scope와 Configuration Item 연결 관리 API"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/operational-scopes/{scopeId}/cis")
public class OperationalScopeCiController {

    private final OperationalScopeCiService operationalScopeCiService;

    /**
     * Configuration Item 연결
     */
    @Operation(
            summary = "Operational Scope에 CI 연결",
            description = "Operational Scope에 Configuration Item을 연결합니다."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{ciId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<OperationalScopeCiResponse> connect(
            @PathVariable Long scopeId,
            @PathVariable Long ciId
    ) {

        return ApiResponse.success(
                "Configuration Item이 Operational Scope에 연결되었습니다.",
                operationalScopeCiService.connect(scopeId, ciId)
        );
    }

    /**
     * 연결된 Configuration Item 목록 조회
     */
    @Operation(
            summary = "Operational Scope의 CI 목록 조회",
            description = "Operational Scope에 연결된 Configuration Item 목록을 조회합니다."
    )
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ApiResponse<List<OperationalScopeCiResponse>> getAll(
            @PathVariable Long scopeId
    ) {

        return ApiResponse.success(
                operationalScopeCiService.getAllByScopeId(scopeId)
        );
    }

    /**
     * Configuration Item 연결 상세 조회
     */
    @Operation(
            summary = "Operational Scope-CI 연결 상세 조회",
            description = "Operational Scope와 Configuration Item의 연결 정보를 조회합니다."
    )
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{ciId}")
    public ApiResponse<OperationalScopeCiResponse> getDetail(
            @PathVariable Long scopeId,
            @PathVariable Long ciId
    ) {

        return ApiResponse.success(
                operationalScopeCiService.getByScopeIdAndCiId(
                        scopeId,
                        ciId
                )
        );
    }

    /**
     * Configuration Item 연결 해제
     */
    @Operation(
            summary = "Operational Scope-CI 연결 해제",
            description = "Operational Scope와 Configuration Item의 연결을 해제합니다."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{ciId}")
    public ApiResponse<Void> disconnect(
            @PathVariable Long scopeId,
            @PathVariable Long ciId
    ) {

        operationalScopeCiService.disconnect(scopeId, ciId);

        return ApiResponse.success(
                "Configuration Item 연결이 해제되었습니다."
        );
    }
}
