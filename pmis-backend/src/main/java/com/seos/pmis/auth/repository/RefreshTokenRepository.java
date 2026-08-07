package com.seos.pmis.auth.repository;

import com.seos.pmis.auth.entity.RefreshToken;
import com.seos.pmis.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    /**
     * 사용자로 Refresh Token 조회
     */
    Optional<RefreshToken> findByUser(User user);

    /**
     * Token 문자열로 조회
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * 사용자의 Refresh Token 삭제
     */
    void deleteByUser(User user);

    /**
     * Token으로 삭제
     */
    void deleteByToken(String token);

    /**
     * 사용자별 Refresh Token 존재 여부
     */
    boolean existsByUser(User user);
}