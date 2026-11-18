
package com.seos.pmis.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class LoginResponse {

    /**
     * 로그인 처리 상태
     */
    private LoginStatus status;

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

    /**
     * 로그인 진행 ID
     * 세션 선택이 필요한 경우 사용
     */
    private String loginAttemptId;

    /**
     * 종료해야 하는 기존 세션 수
     */
    private Integer requiredTerminationCount;

    /**
     * 현재 활성 세션 목록
     */
    private List<ActiveSessionResponse> activeSessions;

    public enum LoginStatus {
        SUCCESS,
        SESSION_SELECTION_REQUIRED
    }

    /**
     * 기존 활성 세션 정보
     */
    @Getter
    @Builder
    public static class ActiveSessionResponse {

        private UUID sessionId;

        private java.time.LocalDateTime createdAt;

        private java.time.LocalDateTime lastAccessedAt;
    }
}
