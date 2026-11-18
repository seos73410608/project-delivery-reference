
package com.seos.pmis.auth.entity;

import com.seos.common.entity.BaseEntity;
import com.seos.pmis.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Builder
@Table(name = "user_sessions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSession extends BaseEntity {

    /**
     * 세션 PK
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 외부 세션 식별자
     */
    @Column(name = "session_id", nullable = false, unique = true)
    private UUID sessionId;

    /**
     * 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Refresh Token 해시
     */
    @Column(name = "refresh_token_hash", nullable = false, length = 255)
    private String refreshTokenHash;

    /**
     * Refresh Token 만료 시간
     */
    @Column(name = "refresh_token_expires_at", nullable = false)
    private LocalDateTime refreshTokenExpiresAt;

    /**
     * 마지막 접근 시간
     */
    @Column(name = "last_accessed_at")
    private LocalDateTime lastAccessedAt;

    /**
     * Refresh Token 갱신
     */
    public void rotateRefreshToken(
            String refreshTokenHash,
            LocalDateTime refreshTokenExpiresAt
    ) {
        this.refreshTokenHash = refreshTokenHash;
        this.refreshTokenExpiresAt = refreshTokenExpiresAt;
    }

    /**
     * 마지막 접근 시간 갱신
     */
    public void updateLastAccessedAt(LocalDateTime lastAccessedAt) {
        this.lastAccessedAt = lastAccessedAt;
    }

    /**
     * Refresh Token 만료 여부
     */
    public boolean isRefreshTokenExpired() {
        return refreshTokenExpiresAt.isBefore(LocalDateTime.now());
    }
}
