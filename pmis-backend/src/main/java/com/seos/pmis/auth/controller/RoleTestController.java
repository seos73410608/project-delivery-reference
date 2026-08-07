package com.seos.pmis.auth.controller;

import com.seos.pmis.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Role Test",
        description = "JWT 권한(Role Hierarchy) 테스트 API"
)
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/role")
public class RoleTestController {

    /**
     * 로그인 사용자
     */
    @Operation(
            summary = "인증 확인",
            description = "로그인한 사용자라면 누구나 접근 가능합니다."
    )
    @GetMapping("/authenticated")
    public ApiResponse<String> authenticated() {

        return ApiResponse.success(
                "인증 성공",
                "로그인한 사용자입니다."
        );
    }

    /**
     * USER 이상
     * ROLE_USER
     * ROLE_PM
     * ROLE_ADMIN
     */
    @Operation(
            summary = "USER 권한 테스트",
            description = "USER 이상(Role Hierarchy 적용) 권한을 확인합니다."
    )
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public ApiResponse<String> user() {

        return ApiResponse.success(
                "USER 권한",
                "USER 이상 접근 가능합니다."
        );
    }

    /**
     * PM 이상
     * ROLE_PM
     * ROLE_ADMIN
     */
    @Operation(
            summary = "PM 권한 테스트",
            description = "PM 이상(Role Hierarchy 적용) 권한을 확인합니다."
    )
    @PreAuthorize("hasRole('PM')")
    @GetMapping("/pm")
    public ApiResponse<String> pm() {

        return ApiResponse.success(
                "PM 권한",
                "PM 이상 접근 가능합니다."
        );
    }

    /**
     * ADMIN
     * ROLE_ADMIN
     */
    @Operation(
            summary = "ADMIN 권한 테스트",
            description = "ADMIN 권한만 접근 가능합니다."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ApiResponse<String> admin() {

        return ApiResponse.success(
                "ADMIN 권한",
                "관리자만 접근 가능합니다."
        );
    }

}