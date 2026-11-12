package com.seos.cmdb.ci.controller;

import com.seos.cmdb.ci.dto.request.ChangeConfigurationItemStatusRequest;
import com.seos.cmdb.ci.dto.request.CreateConfigurationItemRequest;
import com.seos.cmdb.ci.dto.request.UpdateConfigurationItemRequest;
import com.seos.cmdb.ci.dto.response.ConfigurationItemResponse;
import com.seos.cmdb.ci.service.ConfigurationItemService;
import com.seos.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Configuration Item",
        description = "CMDB Configuration Item 관리 API"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/configuration-items")
public class ConfigurationItemController {

    private final ConfigurationItemService configurationItemService;

    /**
     * Configuration Item 생성
     */
    @Operation(summary = "Configuration Item 생성")
    @PreAuthorize("hasRole('PM')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ConfigurationItemResponse> create(
            @Valid @RequestBody CreateConfigurationItemRequest request
    ) {

        return ApiResponse.success(
                "Configuration Item이 생성되었습니다.",
                configurationItemService.create(request)
        );
    }

    /**
     * 전체 Configuration Item 조회
     */
    @Operation(summary = "전체 Configuration Item 조회")
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ApiResponse<List<ConfigurationItemResponse>> getAll() {

        return ApiResponse.success(
                configurationItemService.getAll()
        );
    }

    /**
     * Configuration Item 단건 조회
     */
    @Operation(summary = "Configuration Item 상세 조회")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    public ApiResponse<ConfigurationItemResponse> getById(
            @PathVariable Long id
    ) {

        return ApiResponse.success(
                configurationItemService.getById(id)
        );
    }

    /**
     * Configuration Item 기본 정보 수정
     */
    @Operation(summary = "Configuration Item 기본 정보 수정")
    @PreAuthorize("hasRole('PM')")
    @PutMapping("/{id}")
    public ApiResponse<ConfigurationItemResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateConfigurationItemRequest request
    ) {

        return ApiResponse.success(
                "Configuration Item이 수정되었습니다.",
                configurationItemService.update(id, request)
        );
    }

    /**
     * Configuration Item 상태 변경
     */
    @Operation(summary = "Configuration Item 상태 변경")
    @PreAuthorize("hasRole('PM')")
    @PatchMapping("/{id}/status")
    public ApiResponse<ConfigurationItemResponse> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody ChangeConfigurationItemStatusRequest request
    ) {

        return ApiResponse.success(
                "Configuration Item 상태가 변경되었습니다.",
                configurationItemService.changeStatus(id, request)
        );
    }
}