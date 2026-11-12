package com.seos.cmdb.ci.controller;

import com.seos.cmdb.ci.dto.request.CreateCiTypeRequest;
import com.seos.cmdb.ci.dto.request.UpdateCiTypeRequest;
import com.seos.cmdb.ci.dto.response.CiTypeResponse;
import com.seos.cmdb.ci.service.CiTypeService;
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
        name = "CI Type",
        description = "CMDB CI 유형 관리 API"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ci-types")
public class CiTypeController {

    private final CiTypeService ciTypeService;

    /**
     * CI Type 생성
     */
    @Operation(
            summary = "CI Type 생성",
            description = "신규 CI Type을 생성합니다."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CiTypeResponse> create(
            @Valid @RequestBody CreateCiTypeRequest request
    ) {

        return ApiResponse.success(
                "CI Type이 생성되었습니다.",
                ciTypeService.create(request)
        );
    }

    /**
     * 전체 CI Type 조회
     */
    @Operation(
            summary = "CI Type 전체 조회",
            description = "활성화 여부와 관계없이 모든 CI Type을 조회합니다."
    )
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ApiResponse<List<CiTypeResponse>> getAll() {

        return ApiResponse.success(
                ciTypeService.getAll()
        );
    }

    /**
     * CI Type 단건 조회
     */
    @Operation(
            summary = "CI Type 상세 조회",
            description = "CI Type ID로 상세 정보를 조회합니다."
    )
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    public ApiResponse<CiTypeResponse> getById(
            @Parameter(description = "CI Type ID")
            @PathVariable Long id
    ) {

        return ApiResponse.success(
                ciTypeService.getById(id)
        );
    }

    /**
     * CI Type 수정
     */
    @Operation(
            summary = "CI Type 수정",
            description = "CI Type 정보를 수정합니다. 코드는 변경할 수 없습니다."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ApiResponse<CiTypeResponse> update(
            @Parameter(description = "CI Type ID")
            @PathVariable Long id,
            @Valid @RequestBody UpdateCiTypeRequest request
    ) {

        return ApiResponse.success(
                "CI Type이 수정되었습니다.",
                ciTypeService.update(id, request)
        );
    }

    /**
     * CI Type 활성화
     */
    @Operation(
            summary = "CI Type 활성화",
            description = "비활성화된 CI Type을 다시 활성화합니다."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/enable")
    public ApiResponse<CiTypeResponse> enable(
            @Parameter(description = "CI Type ID")
            @PathVariable Long id
    ) {

        return ApiResponse.success(
                "CI Type이 활성화되었습니다.",
                ciTypeService.enable(id)
        );
    }

    /**
     * CI Type 비활성화
     */
    @Operation(
            summary = "CI Type 비활성화",
            description = "CI Type을 비활성화합니다."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/disable")
    public ApiResponse<Void> disable(
            @Parameter(description = "CI Type ID")
            @PathVariable Long id
    ) {

        ciTypeService.disable(id);

        return ApiResponse.success(
                "CI Type이 비활성화되었습니다."
        );
    }

    /**
     * CI Type 삭제
     */
    @Operation(
            summary = "CI Type 삭제",
            description = "하위 Type 및 CI 참조가 없는 CI Type을 삭제합니다."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @Parameter(description = "CI Type ID")
            @PathVariable Long id
    ) {

        ciTypeService.delete(id);

        return ApiResponse.success(
                "CI Type이 삭제되었습니다."
        );
    }
}