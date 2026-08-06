package com.seos.pmis.auth.controller;

import com.seos.pmis.auth.security.jwt.JwtProvider;
import com.seos.pmis.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Test API",
        description = "JWT 인증 테스트 API"
)
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final JwtProvider jwtProvider;

    /**
     * Public API
     * 인증 없이 접근 가능
     */
    @Operation(
            summary = "Public API",
            description = "인증 없이 접근 가능한 테스트 API입니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공"
    )
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
    @Operation(
            summary = "Private API",
            description = "JWT 인증이 필요한 테스트 API입니다."
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "인증 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            schema = @Schema()
                    )
            )
    })
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
    @Operation(
            summary = "테스트용 Access Token 발급",
            description = "Swagger 테스트를 위한 ADMIN Access Token을 발급합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "토큰 발급 성공"
    )
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