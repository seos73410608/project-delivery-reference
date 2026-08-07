package com.seos.pmis.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Refresh Token 재발급 요청 DTO
 */
@Getter
@NoArgsConstructor
public class RefreshTokenRequest {

    /**
     * Refresh Token
     */
    @NotBlank(message = "Refresh Token은 필수입니다.")
    private String refreshToken;
}