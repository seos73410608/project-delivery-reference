package com.seos.pmis.auth.controller;

import com.seos.pmis.auth.security.jwt.JwtProvider;
import com.seos.pmis.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final JwtProvider jwtProvider;

    /**
     * Public API
     * 인증 없이 접근 가능
     */
    @GetMapping("/public")
    public ApiResponse<String> publicApi() {

        return ApiResponse.success(
                "Public API",
                "인증 없이 접근 가능합니다."
        );
    }

    /**
     * Private API
     * JWT 인증 필요
     */
    @GetMapping("/private")
    public ApiResponse<String> privateApi() {

        return ApiResponse.success(
                "Private API",
                "JWT 인증 성공."
        );
    }

    /**
     * 테스트용 Access Token 발급
     *
     * ※ feature/auth-login 완료 후 삭제 예정
     */
    @GetMapping("/token")
    public ApiResponse<String> token() {

        String accessToken = jwtProvider.generateAccessToken(
                1L,
                "admin",
                "ROLE_ADMIN"
        );

        return ApiResponse.success(
                "Access Token 발급 성공",
                accessToken
        );
    }

}