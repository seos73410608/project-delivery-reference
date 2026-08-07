package com.seos.pmis.auth.controller;

import com.seos.pmis.auth.dto.request.LoginRequest;
import com.seos.pmis.auth.dto.request.RefreshTokenRequest;
import com.seos.pmis.auth.dto.response.LoginResponse;
import com.seos.pmis.auth.service.AuthService;
import com.seos.pmis.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Authentication",
        description = "인증 및 JWT 토큰 관리 API"
)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 로그인
     */
    @Operation(
            summary = "로그인",
            description = "아이디와 비밀번호를 이용하여 로그인하고 Access Token 및 Refresh Token을 발급합니다."
    )
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        LoginResponse response = authService.login(request);

        return ApiResponse.success(
                "로그인에 성공했습니다.",
                response
        );
    }

    /**
     * Access Token / Refresh Token 재발급
     */
    @Operation(
            summary = "토큰 재발급",
            description = "Refresh Token을 이용하여 Access Token과 Refresh Token을 재발급합니다."
    )
    @PostMapping("/refresh")
    public ApiResponse<LoginResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request
    ) {

        LoginResponse response = authService.refresh(request);

        return ApiResponse.success(
                "토큰이 재발급되었습니다.",
                response
        );
    }

}