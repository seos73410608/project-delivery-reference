
package com.seos.pmis.auth.service;

import com.seos.common.exception.BusinessException;
import com.seos.common.exception.code.CommonErrorCode;
import com.seos.pmis.auth.entity.SessionEndReason;
import com.seos.pmis.auth.entity.UserSession;
import com.seos.pmis.auth.entity.UserSessionHistory;
import com.seos.pmis.auth.repository.UserSessionHistoryRepository;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserSessionService {

    private final UserSessionRepository userSessionRepository;
    private final UserSessionHistoryRepository userSessionHistoryRepository;

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
     * 사용자의 활성 세션 목록 조회
     */
    public List<UserSession> getActiveSessions(Long userId) {

        if (userId == null) {
            throw new IllegalArgumentException(
                    "사용자 ID가 필요합니다."
            );
        }

        return userSessionRepository.findAllByUserId(userId);
    }

    /**
     * 사용자가 선택한 활성 세션 종료 및 이력 보존
     */
    @Transactional
    public void terminateSelectedSessions(
            User user,
            List<UUID> sessionIds
    ) {

        terminateSessions(
                user,
                sessionIds,
                SessionEndReason.USER_SELECTED
        );
    }

    /**
     * 현재 로그인한 세션 종료 및 이력 보존
     *
     * 로그아웃 요청 시 인증된 사용자의 현재 세션 하나만 종료한다.
     */
    @Transactional
    public void terminateSession(
            User user,
            UUID sessionId
    ) {

        if (sessionId == null) {
            throw new IllegalArgumentException(
                    "종료할 세션 ID가 필요합니다."
            );
        }

        terminateSessions(
                user,
                List.of(sessionId),
                SessionEndReason.USER_LOGOUT
        );
    }

    /**
     * 활성 세션 종료 공통 처리
     *
     * 1. 사용자 및 세션 ID 검증
     * 2. 본인 소유의 활성 세션인지 확인
     * 3. 종료 이력 저장
     * 4. 활성 세션 삭제
     *
     * 이력 저장과 활성 세션 삭제는 동일 트랜잭션으로 처리한다.
     */
    @Transactional
    public void terminateSessions(
            User user,
            List<UUID> sessionIds,
            SessionEndReason endReason
    ) {

        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException(
                    "유효한 사용자가 필요합니다."
            );
        }

        if (sessionIds == null || sessionIds.isEmpty()
                || sessionIds.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException(
                    "종료할 세션을 선택해야 합니다."
            );
        }

        if (endReason == null) {
            throw new IllegalArgumentException(
                    "세션 종료 사유가 필요합니다."
            );
        }

        Set<UUID> selectedSessionIds = Set.copyOf(sessionIds);

        if (selectedSessionIds.size() != sessionIds.size()) {
            throw new IllegalArgumentException(
                    "중복된 세션 ID를 선택할 수 없습니다."
            );
        }

        List<UserSession> activeSessions =
                userSessionRepository.findAllByUserId(user.getId());

        Map<UUID, UserSession> activeSessionMap = activeSessions.stream()
                .collect(Collectors.toMap(
                        UserSession::getSessionId,
                        Function.identity()
                ));

        List<UserSession> selectedSessions = sessionIds.stream()
                .map(activeSessionMap::get)
                .toList();

        if (selectedSessions.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException(
                    "선택한 세션 중 존재하지 않거나 본인 소유가 아닌 세션이 있습니다."
            );
        }

        LocalDateTime endedAt = LocalDateTime.now();

        List<UserSessionHistory> histories = selectedSessions.stream()
                .map(session -> UserSessionHistory.builder()
                        .sessionId(session.getSessionId())
                        .user(session.getUser())
                        .createdAt(session.getCreatedAt())
                        .lastAccessedAt(session.getLastAccessedAt())
                        .endedAt(endedAt)
                        .endReason(endReason)
                        .build())
                .toList();

        userSessionHistoryRepository.saveAll(histories);
        userSessionRepository.deleteAll(selectedSessions);

        log.info(
                "User sessions terminated. userId={}, count={}, reason={}",
                user.getId(),
                selectedSessions.size(),
                endReason
        );
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
