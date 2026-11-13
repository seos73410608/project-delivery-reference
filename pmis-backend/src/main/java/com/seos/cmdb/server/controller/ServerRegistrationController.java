package com.seos.cmdb.server.controller;

import com.seos.cmdb.server.dto.request.CreateServerRequest;
import com.seos.cmdb.server.dto.response.ServerRegistrationResponse;
import com.seos.cmdb.server.service.ServerRegistrationService;
import com.seos.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/servers")
@RequiredArgsConstructor
public class ServerRegistrationController {

    private final ServerRegistrationService serverRegistrationService;

    /**
     * 서버 등록
     */
    @PostMapping
    @PreAuthorize("hasRole('PM')")
    public ResponseEntity<ApiResponse<ServerRegistrationResponse>> register(
            @Valid @RequestBody CreateServerRequest request
    ) {

        ServerRegistrationResponse response =
                serverRegistrationService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "서버가 등록되었습니다.",
                        response
                ));
    }
}