package com.seos.pmis.company.controller;

import com.seos.common.response.ApiResponse;
import com.seos.pmis.company.dto.request.CompanySearchRequest;
import com.seos.pmis.company.dto.request.CreateCompanyRequest;
import com.seos.pmis.company.dto.request.UpdateCompanyRequest;
import com.seos.pmis.company.dto.response.CompanyResponse;
import com.seos.pmis.company.entity.CompanyStatus;
import com.seos.pmis.company.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Company",
        description = "회사 기준정보 관리 API"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    /**
     * 회사 등록
     */
    @Operation(
            summary = "회사 등록",
            description = "신규 회사를 등록합니다."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CompanyResponse> createCompany(
            @Valid @RequestBody CreateCompanyRequest request
    ) {

        return ApiResponse.success(
                "회사가 등록되었습니다.",
                companyService.create(request)
        );
    }

    /**
     * 회사 검색
     */
    @Operation(
            summary = "회사 검색",
            description = "검색 조건을 이용하여 회사를 조회합니다."
    )
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ApiResponse<Page<CompanyResponse>> searchCompanies(
            @Valid @ModelAttribute CompanySearchRequest request,
            Pageable pageable
    ) {

        return ApiResponse.success(
                companyService.search(request, pageable)
        );
    }

    /**
     * 회사 단건 조회
     */
    @Operation(
            summary = "회사 상세 조회",
            description = "회사 ID를 이용하여 상세 정보를 조회합니다."
    )
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{companyId}")
    public ApiResponse<CompanyResponse> getCompany(
            @Parameter(description = "회사 ID")
            @PathVariable Long companyId
    ) {

        return ApiResponse.success(
                companyService.get(companyId)
        );
    }

    /**
     * 회사 정보 수정
     */
    @Operation(
            summary = "회사 수정",
            description = "회사 정보를 수정합니다."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{companyId}")
    public ApiResponse<CompanyResponse> updateCompany(
            @Parameter(description = "회사 ID")
            @PathVariable Long companyId,
            @Valid @RequestBody UpdateCompanyRequest request
    ) {

        return ApiResponse.success(
                "회사 정보가 수정되었습니다.",
                companyService.update(companyId, request)
        );
    }

    /**
     * 회사 상태 변경
     */
    @Operation(
            summary = "회사 상태 변경",
            description = "회사의 활성 또는 비활성 상태를 변경합니다."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{companyId}/status")
    public ApiResponse<CompanyResponse> changeCompanyStatus(
            @Parameter(description = "회사 ID")
            @PathVariable Long companyId,
            @Parameter(description = "변경할 회사 상태")
            @RequestParam CompanyStatus status
    ) {

        return ApiResponse.success(
                "회사 상태가 변경되었습니다.",
                companyService.changeStatus(companyId, status)
        );
    }
}