package com.seos.pmis.contract.controller;

import com.seos.common.response.ApiResponse;
import com.seos.pmis.contract.dto.request.ContractSearchRequest;
import com.seos.pmis.contract.dto.request.CreateContractRequest;
import com.seos.pmis.contract.dto.request.UpdateContractRequest;
import com.seos.pmis.contract.dto.response.ContractResponse;
import com.seos.pmis.contract.entity.ContractStatus;
import com.seos.pmis.contract.service.ContractService;
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
        name = "Contract",
        description = "계약 기준정보 관리 API"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contracts")
public class ContractController {

    private final ContractService contractService;

    /**
     * 계약 등록
     */
    @Operation(
            summary = "계약 등록",
            description = "신규 계약을 등록합니다."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ContractResponse> createContract(
            @Valid @RequestBody CreateContractRequest request
    ) {

        return ApiResponse.success(
                "계약이 등록되었습니다.",
                contractService.create(request)
        );
    }

    /**
     * 계약 검색
     */
    @Operation(
            summary = "계약 검색",
            description = "검색 조건을 이용하여 계약 목록을 조회합니다."
    )
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ApiResponse<Page<ContractResponse>> searchContracts(
            @Valid @ModelAttribute ContractSearchRequest request,
            Pageable pageable
    ) {

        return ApiResponse.success(
                contractService.search(request, pageable)
        );
    }

    /**
     * 계약 단건 조회
     */
    @Operation(
            summary = "계약 상세 조회",
            description = "계약 ID를 이용하여 상세 정보를 조회합니다."
    )
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{contractId}")
    public ApiResponse<ContractResponse> getContract(
            @Parameter(description = "계약 ID")
            @PathVariable Long contractId
    ) {

        return ApiResponse.success(
                contractService.get(contractId)
        );
    }

    /**
     * 계약 정보 수정
     */
    @Operation(
            summary = "계약 수정",
            description = "계약 정보를 수정합니다. 계약 코드는 변경할 수 없습니다."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{contractId}")
    public ApiResponse<ContractResponse> updateContract(
            @Parameter(description = "계약 ID")
            @PathVariable Long contractId,
            @Valid @RequestBody UpdateContractRequest request
    ) {

        return ApiResponse.success(
                "계약 정보가 수정되었습니다.",
                contractService.update(contractId, request)
        );
    }

    /**
     * 계약 상태 변경
     */
    @Operation(
            summary = "계약 상태 변경",
            description = "계약 상태를 변경합니다."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{contractId}/status")
    public ApiResponse<ContractResponse> changeContractStatus(
            @Parameter(description = "계약 ID")
            @PathVariable Long contractId,
            @Parameter(description = "변경할 계약 상태")
            @RequestParam ContractStatus status
    ) {

        return ApiResponse.success(
                "계약 상태가 변경되었습니다.",
                contractService.changeStatus(contractId, status)
        );
    }
}