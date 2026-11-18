
package com.seos.pmis.auth.service;

import com.seos.common.exception.BusinessException;
import com.seos.common.exception.code.CommonErrorCode;
import com.seos.pmis.auth.entity.UserSession;
import com.seos.pmis.auth.repository.UserSessionRepository;
import com.seos.pmis.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserSessionService {

    private final UserSessionRepository userSessionRepository;

    /**
     * 신규 세션 생성
     *
     * Refresh Token 원문은 저장하지 않고 SHA-256 해시를 저장한다.
     */
    @Transactional
    public UserSession createSession(
            User user,
            UUID sessionId,
            String refreshToken,
            LocalDateTime refreshTokenExpiresAt
    ) {

        if (user == null || sessionId == null
                || refreshToken == null || refreshToken.isBlank()
                || refreshTokenExpiresAt == null) {
            throw new IllegalArgumentException(
                    "세션 생성에 필요한 값이 올바르지 않습니다."
            );
        }

        if (userSessionRepository.existsBySessionId(sessionId)) {
            throw new IllegalStateException(
                    "이미 존재하는 세션 ID입니다."
            );
        }

        UserSession session = UserSession.builder()
                .sessionId(sessionId)
                .user(user)
                .refreshTokenHash(hashToken(refreshToken))
                .refreshTokenExpiresAt(refreshTokenExpiresAt)
                .lastAccessedAt(LocalDateTime.now())
                .build();

        return userSessionRepository.save(session);
    }

    /**
     * Access Token의 활성 세션 검증
     */
    public boolean validateAccessSession(
            Long userId,
            UUID sessionId
    ) {

        if (userId == null || sessionId == null) {
            return false;
        }

        UserSession session = userSessionRepository
                .findBySessionId(sessionId)
                .orElse(null);

        if (session == null) {
            return false;
        }

        if (!userId.equals(session.getUser().getId())) {
            log.debug(
                    "Session owner mismatch. userId={}, sessionId={}",
                    userId,
                    sessionId
            );

            return false;
        }

        if (session.isRefreshTokenExpired()) {
            log.debug(
                    "Session expired. userId={}, sessionId={}",
                    userId,
                    sessionId
            );

            return false;
        }

        return true;
    }

    /**
     * Refresh Token 검증 및 회전
     *
     * 기존 토큰 해시와 전달된 토큰의 해시를 비교한다.
     * 일치하면 신규 토큰 해시와 만료 시각으로 갱신한다.
     */
    @Transactional
    public UserSession rotateRefreshToken(
            UUID sessionId,
            String currentRefreshToken,
            String newRefreshToken,
            LocalDateTime newRefreshTokenExpiresAt
    ) {

        if (sessionId == null
                || currentRefreshToken == null
                || currentRefreshToken.isBlank()
                || newRefreshToken == null
                || newRefreshToken.isBlank()
                || newRefreshTokenExpiresAt == null) {

            throw new BusinessException(
                    CommonErrorCode.INVALID_TOKEN
            );
        }

        UserSession session = userSessionRepository
                .findBySessionId(sessionId)
                .orElseThrow(() ->
                        new BusinessException(
                                CommonErrorCode.INVALID_TOKEN
                        )
                );

        if (session.isRefreshTokenExpired()) {
            throw new BusinessException(
                    CommonErrorCode.EXPIRED_TOKEN
            );
        }

        String currentHash = hashToken(currentRefreshToken);

        if (!MessageDigest.isEqual(
                session.getRefreshTokenHash()
                        .getBytes(StandardCharsets.UTF_8),
                currentHash.getBytes(StandardCharsets.UTF_8)
        )) {
            throw new BusinessException(
                    CommonErrorCode.INVALID_TOKEN
            );
        }

        session.rotateRefreshToken(
                hashToken(newRefreshToken),
                newRefreshTokenExpiresAt
        );

        session.updateLastAccessedAt(LocalDateTime.now());

        return session;
    }

    /**
     * Refresh Token SHA-256 해시 생성
     */
    private String hashToken(String token) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException e) {

            throw new IllegalStateException(
                    "SHA-256 알고리즘을 사용할 수 없습니다.",
                    e
            );
        }
    }
}
