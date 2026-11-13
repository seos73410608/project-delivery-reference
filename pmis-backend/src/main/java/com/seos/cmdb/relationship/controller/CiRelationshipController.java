package com.seos.cmdb.relationship.controller;

import com.seos.cmdb.relationship.dto.request.CreateCiRelationshipRequest;
import com.seos.cmdb.relationship.dto.request.DeactivateCiRelationshipRequest;
import com.seos.cmdb.relationship.dto.request.UpdateCiRelationshipRequest;
import com.seos.cmdb.relationship.dto.response.CiRelationshipResponse;
import com.seos.cmdb.relationship.dto.response.CiRelationshipTypeResponse;
import com.seos.cmdb.relationship.entity.RelationshipDirection;
import com.seos.cmdb.relationship.entity.RelationshipStatus;
import com.seos.cmdb.relationship.service.CiRelationshipService;
import com.seos.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "CI Relationship",
        description = "CMDB Configuration Item 관계 관리 API"
)
@RestController
@RequiredArgsConstructor
public class CiRelationshipController {

    private final CiRelationshipService ciRelationshipService;

    /**
     * Configuration Item 관계 생성
     */
    @Operation(summary = "Configuration Item 관계 생성")
    @PreAuthorize("hasRole('PM')")
    @PostMapping("/api/configuration-items/{ciId}/relationships")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CiRelationshipResponse> create(
            @PathVariable Long ciId,
            @Valid @RequestBody CreateCiRelationshipRequest request
    ) {

        return ApiResponse.success(
                "Configuration Item 관계가 생성되었습니다.",
                ciRelationshipService.create(ciId, request)
        );
    }

    /**
     * Configuration Item 관계 목록 조회
     */
    @Operation(summary = "Configuration Item 관계 목록 조회")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/api/configuration-items/{ciId}/relationships")
    public ApiResponse<List<CiRelationshipResponse>> getAllForCi(
            @PathVariable Long ciId,
            @Parameter(description = "관계 방향")
            @RequestParam(defaultValue = "ALL") RelationshipDirection direction,
            @Parameter(description = "관계 상태")
            @RequestParam(required = false) RelationshipStatus status,
            @Parameter(description = "관계 유형 ID")
            @RequestParam(required = false) Long relationshipTypeId
    ) {

        return ApiResponse.success(
                ciRelationshipService.getAllForCi(
                        ciId,
                        direction,
                        status,
                        relationshipTypeId
                )
        );
    }

    /**
     * Configuration Item 관계 상세 조회
     */
    @Operation(summary = "Configuration Item 관계 상세 조회")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/api/ci-relationships/{id}")
    public ApiResponse<CiRelationshipResponse> getById(
            @PathVariable Long id
    ) {

        return ApiResponse.success(
                ciRelationshipService.getById(id)
        );
    }

    /**
     * Configuration Item 관계 설명 수정
     */
    @Operation(summary = "Configuration Item 관계 설명 수정")
    @PreAuthorize("hasRole('PM')")
    @PutMapping("/api/ci-relationships/{id}")
    public ApiResponse<CiRelationshipResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCiRelationshipRequest request
    ) {

        return ApiResponse.success(
                "Configuration Item 관계가 수정되었습니다.",
                ciRelationshipService.update(id, request)
        );
    }

    /**
     * Configuration Item 관계 비활성화
     */
    @Operation(summary = "Configuration Item 관계 비활성화")
    @PreAuthorize("hasRole('PM')")
    @PatchMapping("/api/ci-relationships/{id}/deactivate")
    public ApiResponse<CiRelationshipResponse> deactivate(
            @PathVariable Long id,
            @Valid @RequestBody DeactivateCiRelationshipRequest request
    ) {

        return ApiResponse.success(
                "Configuration Item 관계가 비활성화되었습니다.",
                ciRelationshipService.deactivate(id, request.getReason())
        );
    }

    /**
     * 활성 Configuration Item 관계 유형 조회
     */
    @Operation(summary = "활성 Configuration Item 관계 유형 조회")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/api/ci-relationship-types")
    public ApiResponse<List<CiRelationshipTypeResponse>> getEnabledRelationshipTypes() {

        return ApiResponse.success(
                ciRelationshipService.getRelationshipTypes(false)
        );
    }

    /**
     * 비활성 유형을 포함한 전체 관계 유형 조회
     */
    @Operation(summary = "전체 Configuration Item 관계 유형 조회")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/ci-relationship-types/all")
    public ApiResponse<List<CiRelationshipTypeResponse>> getAllRelationshipTypes() {

        return ApiResponse.success(
                ciRelationshipService.getRelationshipTypes(true)
        );
    }
}