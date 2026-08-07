package com.seos.pmis.auth.entity;

import com.seos.pmis.common.entity.BaseEntity;
import com.seos.pmis.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@Table(name = "refresh_tokens")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RefreshToken extends BaseEntity {

    /**
     * Refresh Token PK
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 사용자
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /**
     * Refresh Token
     */
    @Column(nullable = false, length = 500)
    private String token;

    /**
     * 만료 시간
     */
    @Column(nullable = false)
    private LocalDateTime expiredAt;

    /**
     * Refresh Token 갱신
     */
    public void updateToken(
            String token,
            LocalDateTime expiredAt
    ) {
        this.token = token;
        this.expiredAt = expiredAt;
    }

    /**
     * 만료 여부
     */
    public boolean isExpired() {
        return expiredAt.isBefore(LocalDateTime.now());
    }
}