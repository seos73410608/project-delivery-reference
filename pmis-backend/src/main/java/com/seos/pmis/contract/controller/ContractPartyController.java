package com.seos.pmis.contract.controller;

import com.seos.common.response.ApiResponse;
import com.seos.pmis.contract.dto.request.CreateContractPartyRequest;
import com.seos.pmis.contract.dto.response.ContractPartyResponse;
import com.seos.pmis.contract.service.ContractPartyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Contract Party", description = "계약 당사자 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping
public class ContractPartyController {

    private final ContractPartyService contractPartyService;

    @Operation(summary = "계약별 당사자 목록 조회")
    @GetMapping("/api/contracts/{contractId}/parties")
    @PreAuthorize("hasAnyRole('USER', 'PM', 'ADMIN')")
    public ApiResponse<List<ContractPartyResponse>> getByContract(
            @PathVariable Long contractId
    ) {
        return ApiResponse.success(
                contractPartyService.getByContract(contractId)
        );
    }

    @Operation(summary = "회사별 계약 당사자 이력 조회")
    @GetMapping("/api/companies/{companyId}/contract-parties")
    @PreAuthorize("hasAnyRole('USER', 'PM', 'ADMIN')")
    public ApiResponse<List<ContractPartyResponse>> getByCompany(
            @PathVariable Long companyId
    ) {
        return ApiResponse.success(
                contractPartyService.getByCompany(companyId)
        );
    }

    @Operation(summary = "계약 당사자 등록")
    @PostMapping("/api/contracts/{contractId}/parties")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ContractPartyResponse> addParty(
            @PathVariable Long contractId,
            @Valid @RequestBody CreateContractPartyRequest request
    ) {
        return ApiResponse.success(
                contractPartyService.addParty(contractId, request)
        );
    }

    @Operation(summary = "계약 당사자 종료")
    @PatchMapping("/api/contracts/{contractId}/parties/{partyId}/close")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ContractPartyResponse> closeParty(
            @PathVariable Long contractId,
            @PathVariable Long partyId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate effectiveTo
    ) {
        return ApiResponse.success(
                contractPartyService.closeParty(
                        contractId,
                        partyId,
                        effectiveTo
                )
        );
    }

    @Operation(summary = "주요 계약 당사자 여부 변경")
    @PatchMapping("/api/contracts/{contractId}/parties/{partyId}/primary")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ContractPartyResponse> changePrimary(
            @PathVariable Long contractId,
            @PathVariable Long partyId,
            @RequestParam boolean primary
    ) {
        return ApiResponse.success(
                contractPartyService.changePrimary(
                        contractId,
                        partyId,
                        primary
                )
        );
    }
}