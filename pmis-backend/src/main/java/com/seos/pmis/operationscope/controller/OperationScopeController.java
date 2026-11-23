
package com.seos.pmis.operationscope.controller;

import com.seos.common.response.ApiResponse;
import com.seos.pmis.operationscope.dto.request.OperationScopeCreateRequest;
import com.seos.pmis.operationscope.dto.request.OperationScopeStatusChangeRequest;
import com.seos.pmis.operationscope.dto.request.OperationScopeUpdateRequest;
import com.seos.pmis.operationscope.dto.response.OperationScopeResponse;
import com.seos.pmis.operationscope.service.OperationScopeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Operation Scope",
        description = "운영 서비스와 운영 범위 관계 관리 API"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/operation-scopes")
public class OperationScopeController {

    private final OperationScopeService operationScopeService;

    /**
     * 운영 서비스와 운영 범위 연결
     */
    @Operation(
            summary = "운영 서비스와 운영 범위 연결",
            description = "운영 서비스와 운영 범위를 연결하여 관계를 생성합니다."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<OperationScopeResponse> create(
            @Valid @RequestBody OperationScopeCreateRequest request
    ) {
        return ApiResponse.success(
                "운영 서비스와 운영 범위가 연결되었습니다.",
                operationScopeService.createResponse(
                        request.getOperationId(),
                        request.getOperationalScopeId(),
                        request.getEffectiveFrom(),
                        request.getEffectiveTo(),
                        request.getDescription()
                )
        );
    }

    /**
     * 운영 범위 관계 상세 조회
     */
    @Operation(
            summary = "운영 범위 관계 상세 조회",
            description = "ID를 이용하여 운영 서비스와 운영 범위의 연결 정보를 조회합니다."
    )
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    public ApiResponse<OperationScopeResponse> getDetail(
            @PathVariable Long id
    ) {
        return ApiResponse.success(
                operationScopeService.getDetailResponse(id)
        );
    }

    /**
     * 운영 서비스 기준 운영 범위 관계 조회
     */
    @Operation(
            summary = "운영 서비스 기준 운영 범위 조회",
            description = "운영 서비스 ID에 연결된 운영 범위 관계 목록을 조회합니다."
    )
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/by-operation/{operationId}")
    public ApiResponse<List<OperationScopeResponse>> findByOperation(
            @PathVariable Long operationId
    ) {
        return ApiResponse.success(
                operationScopeService.findResponsesByOperation(operationId)
        );
    }

    /**
     * 운영 범위 기준 운영 서비스 관계 조회
     */
    @Operation(
            summary = "운영 범위 기준 운영 서비스 조회",
            description = "운영 범위 ID에 연결된 운영 서비스 관계 목록을 조회합니다."
    )
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/by-operational-scope/{operationalScopeId}")
    public ApiResponse<List<OperationScopeResponse>> findByOperationalScope(
            @PathVariable Long operationalScopeId
    ) {
        return ApiResponse.success(
                operationScopeService.findResponsesByOperationalScope(
                        operationalScopeId
                )
        );
    }

    /**
     * 운영 범위 관계 정보 수정
     */
    @Operation(
            summary = "운영 범위 관계 수정",
            description = "운영 범위 관계의 적용 기간과 설명을 수정합니다."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ApiResponse<OperationScopeResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody OperationScopeUpdateRequest request
    ) {
        return ApiResponse.success(
                "운영 범위 관계가 수정되었습니다.",
                operationScopeService.updateResponse(
                        id,
                        request.getEffectiveFrom(),
                        request.getEffectiveTo(),
                        request.getDescription()
                )
        );
    }

    /**
     * 운영 범위 관계 상태 변경
     */
    @Operation(
            summary = "운영 범위 관계 상태 변경",
            description = "운영 범위 관계의 상태를 변경합니다."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public ApiResponse<OperationScopeResponse> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody OperationScopeStatusChangeRequest request
    ) {
        return ApiResponse.success(
                "운영 범위 관계 상태가 변경되었습니다.",
                operationScopeService.changeStatusResponse(
                        id,
                        request.getStatus()
                )
        );
    }
}
