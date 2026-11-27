package com.seos.pmis.company.controller;

import com.seos.common.response.ApiResponse;
import com.seos.pmis.company.dto.request.CreateOrganizationRequest;
import com.seos.pmis.company.dto.request.UpdateOrganizationRequest;
import com.seos.pmis.company.dto.response.OrganizationResponse;
import com.seos.pmis.company.entity.OrganizationStatus;
import com.seos.pmis.company.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(
        name = "Organization",
        description = "조직 기준정보 관리 API"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/companies/{companyId}/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;

    /**
     * 회사별 조직 목록 조회
     */
    @Operation(
            summary = "조직 목록 조회",
            description = "회사에 소속된 전체 조직을 조회합니다."
    )
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ApiResponse<List<OrganizationResponse>> getOrganizations(
            @Parameter(description = "회사 ID")
            @PathVariable Long companyId
    ) {

        return ApiResponse.success(
                organizationService.getOrganizations(companyId)
        );
    }

    /**
     * 조직 상세 조회
     */
    @Operation(
            summary = "조직 상세 조회",
            description = "회사 ID와 조직 ID를 이용하여 상세 정보를 조회합니다."
    )
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{organizationId}")
    public ApiResponse<OrganizationResponse> getOrganization(
            @Parameter(description = "회사 ID")
            @PathVariable Long companyId,
            @Parameter(description = "조직 ID")
            @PathVariable Long organizationId
    ) {

        return ApiResponse.success(
                organizationService.get(companyId, organizationId)
        );
    }

    /**
     * 조직 등록
     */
    @Operation(
            summary = "조직 등록",
            description = "회사에 신규 조직을 등록합니다."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<OrganizationResponse> createOrganization(
            @Parameter(description = "회사 ID")
            @PathVariable Long companyId,
            @Valid @RequestBody CreateOrganizationRequest request
    ) {

        return ApiResponse.success(
                "조직이 등록되었습니다.",
                organizationService.create(companyId, request)
        );
    }

    /**
     * 조직 정보 수정
     */
    @Operation(
            summary = "조직 수정",
            description = "조직 정보를 수정합니다."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{organizationId}")
    public ApiResponse<OrganizationResponse> updateOrganization(
            @Parameter(description = "회사 ID")
            @PathVariable Long companyId,
            @Parameter(description = "조직 ID")
            @PathVariable Long organizationId,
            @Valid @RequestBody UpdateOrganizationRequest request
    ) {

        return ApiResponse.success(
                "조직 정보가 수정되었습니다.",
                organizationService.update(
                        companyId,
                        organizationId,
                        request
                )
        );
    }

    /**
     * 조직 상태 변경
     */
    @Operation(
            summary = "조직 상태 변경",
            description = "조직의 활성 또는 비활성 상태를 변경합니다."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{organizationId}/status")
    public ApiResponse<OrganizationResponse> changeOrganizationStatus(
            @Parameter(description = "회사 ID")
            @PathVariable Long companyId,
            @Parameter(description = "조직 ID")
            @PathVariable Long organizationId,
            @Parameter(description = "변경할 조직 상태")
            @RequestParam OrganizationStatus status
    ) {

        return ApiResponse.success(
                "조직 상태가 변경되었습니다.",
                organizationService.changeStatus(
                        companyId,
                        organizationId,
                        status
                )
        );
    }
}