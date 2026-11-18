package com.seos.pmis.auth.entity;

import com.seos.common.entity.BaseEntity;
import com.seos.pmis.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@Table(name = "user_security_settings")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSecuritySetting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Builder.Default
    @Column(name = "max_session_count", nullable = false)
    private Integer maxSessionCount = 5;

    public void changeMaxSessionCount(Integer maxSessionCount) {
        if (maxSessionCount == null || maxSessionCount < 1) {
            throw new IllegalArgumentException(
                    "최대 세션 수는 1 이상이어야 합니다."
            );
        }

        this.maxSessionCount = maxSessionCount;
    }
}