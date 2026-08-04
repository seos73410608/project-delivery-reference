package com.seos.pmis.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {

    /**
     * 사용자 ID
     */
    private Long userId;

    /**
     * 로그인 ID
     */
    private String username;

    /**
     * 사용자 이름
     */
    private String name;

    /**
     * 권한
     */
    private String role;

    /**
     * Access Token
     */
    private String accessToken;

    /**
     * Refresh Token
     */
    private String refreshToken;

    /**
     * Access Token 만료 시간(ms)
     */
    private Long accessTokenExpiration;

    /**
     * Refresh Token 만료 시간(ms)
     */
    private Long refreshTokenExpiration;

}