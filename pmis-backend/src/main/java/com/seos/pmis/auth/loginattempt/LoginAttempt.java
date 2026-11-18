package com.seos.pmis.auth.loginattempt;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@Getter
@Builder
@Jacksonized
public class LoginAttempt {

    /**
     * 로그인 시도 식별자
     */
    private String loginAttemptId;

    /**
     * 사용자 ID
     */
    private Long userId;

    /**
     * 로그인 ID
     */
    private String username;

    /**
     * 생성 시각
     */
    private LocalDateTime createdAt;

    /**
     * 만료 시각
     */
    private LocalDateTime expiresAt;
}
