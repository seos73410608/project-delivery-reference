package com.seos.pmis.auth.controller;

import com.seos.pmis.auth.dto.request.LoginRequest;
import com.seos.pmis.auth.dto.request.RefreshTokenRequest;
import com.seos.pmis.auth.dto.response.LoginResponse;
import com.seos.pmis.auth.service.AuthService;
import com.seos.pmis.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 로그인
     */
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