package com.seos.pmis.auth.controller;

import com.seos.pmis.auth.dto.request.LoginRequest;
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
}