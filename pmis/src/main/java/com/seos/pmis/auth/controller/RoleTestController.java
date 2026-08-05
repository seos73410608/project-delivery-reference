package com.seos.pmis.auth.controller;

import com.seos.pmis.common.response.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/role")
public class RoleTestController {

    /**
     * 로그인 사용자
     */
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
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ApiResponse<String> admin() {

        return ApiResponse.success(
                "ADMIN 권한",
                "관리자만 접근 가능합니다."
        );
    }
}