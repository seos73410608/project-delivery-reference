package com.seos.pmis.operation.controller;

import com.seos.common.response.ApiResponse;
import com.seos.pmis.operation.dto.request.OperationCreateRequest;
import com.seos.pmis.operation.dto.request.OperationStatusChangeRequest;
import com.seos.pmis.operation.dto.request.OperationUpdateRequest;
import com.seos.pmis.operation.dto.response.OperationParticipantResponse;
import com.seos.pmis.operation.dto.response.OperationResponse;
import com.seos.pmis.operation.service.OperationParticipantService;
import com.seos.pmis.operation.service.OperationService;
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

@Tag(
        name = "Operation",
        description = "운영 서비스 관리 API"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/operations")
public class OperationController {

    private final OperationService operationService;
    private final OperationParticipantService participantService;

    /**
     * 운영 서비스 생성
     */
    @Operation(
            summary = "운영 서비스 생성",
            description = "새로운 운영 서비스를 생성합니다."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<OperationResponse> create(
            @Valid @RequestBody OperationCreateRequest request
    ) {
        return ApiResponse.success(
                "운영 서비스가 생성되었습니다.",
                operationService.createResponse(
                        request.getOperationCode(),
                        request.getOperationName(),
                        request.getOperationType(),
                        request.getManagerUserId(),
                        request.getDescription(),
                        request.getEffectiveFrom(),
                        request.getEffectiveTo()
                )
        );
    }

    /**
     * 운영 코드 기준 운영 서비스 조회
     */
    @Operation(
            summary = "운영 코드 기준 조회",
            description = "운영 코드로 운영 서비스 정보를 조회합니다."
    )
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/by-code/{operationCode}")
    public ApiResponse<OperationResponse> findByCode(
            @PathVariable String operationCode
    ) {
        return ApiResponse.success(
                operationService.findResponseByCode(operationCode)
        );
    }

    /**
     * 운영 서비스 상세 조회
     */
    @Operation(
            summary = "운영 서비스 상세 조회",
            description = "ID를 이용하여 운영 서비스 정보를 조회합니다."
    )
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    public ApiResponse<OperationResponse> getDetail(
            @PathVariable Long id
    ) {
        return ApiResponse.success(
                operationService.getDetailResponse(id)
        );
    }

    /**
     * 인계 대상 참여자 목록 조회
     */
    @Operation(
            summary = "인계 대상 참여자 조회",
            description = "운영 서비스의 OPERATOR 및 SERVICE_PROVIDER 참여자를 조회합니다. "
                    + "예정일을 지정하면 해당 날짜에 유효한 참여자만 반환합니다."
    )
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}/participants")
    public ApiResponse<List<OperationParticipantResponse>> findHandoverEligibleParticipants(
            @PathVariable Long id,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate plannedHandoverDate
    ) {
        return ApiResponse.success(
                participantService.findHandoverEligibleParticipants(
                        id,
                        plannedHandoverDate
                )
        );
    }

    /**
     * 전체 운영 서비스 조회
     */
    @Operation(
            summary = "전체 운영 서비스 조회",
            description = "등록된 전체 운영 서비스 목록을 조회합니다."
    )
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ApiResponse<List<OperationResponse>> findAll() {
        return ApiResponse.success(
                operationService.findAllResponses()
        );
    }

    /**
     * 운영 서비스 정보 수정
     */
    @Operation(
            summary = "운영 서비스 수정",
            description = "운영 서비스의 이름, 유형, 담당자, 설명 및 적용 기간을 수정합니다."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ApiResponse<OperationResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody OperationUpdateRequest request
    ) {
        return ApiResponse.success(
                "운영 서비스가 수정되었습니다.",
                operationService.updateResponse(
                        id,
                        request.getOperationName(),
                        request.getOperationType(),
                        request.getManagerUserId(),
                        request.getDescription(),
                        request.getEffectiveFrom(),
                        request.getEffectiveTo()
                )
        );
    }

    /**
     * 운영 서비스 상태 변경
     */
    @Operation(
            summary = "운영 서비스 상태 변경",
            description = "운영 서비스의 상태를 변경합니다."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public ApiResponse<OperationResponse> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody OperationStatusChangeRequest request
    ) {
        return ApiResponse.success(
                "운영 서비스 상태가 변경되었습니다.",
                operationService.changeStatusResponse(
                        id,
                        request.getStatus()
                )
        );
    }
}