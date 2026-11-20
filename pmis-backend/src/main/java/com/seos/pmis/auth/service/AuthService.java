
package com.seos.pmis.auth.service;

import com.seos.pmis.auth.dto.request.LoginRequest;
import com.seos.pmis.auth.dto.request.RefreshTokenRequest;
import com.seos.pmis.auth.dto.request.SessionSelectionRequest;
import com.seos.pmis.auth.dto.response.LoginResponse;
import com.seos.pmis.auth.entity.UserSecuritySetting;
import com.seos.pmis.auth.entity.UserSession;
import com.seos.pmis.auth.loginattempt.LoginAttempt;
import com.seos.pmis.auth.loginattempt.LoginAttemptProperties;
import com.seos.pmis.auth.loginattempt.LoginAttemptStore;
import com.seos.pmis.auth.repository.UserSecuritySettingRepository;
import com.seos.pmis.auth.security.jwt.JwtProvider;
import com.seos.common.exception.BusinessException;
import com.seos.common.exception.code.CommonErrorCode;
import com.seos.pmis.user.entity.User;
import com.seos.pmis.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private static final long DEFAULT_LOGIN_ATTEMPT_TTL_SECONDS = 300L;

    private final UserRepository userRepository;
    private final UserSecuritySettingRepository userSecuritySettingRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final UserSessionService userSessionService;
    private final ObjectProvider<LoginAttemptStore> loginAttemptStoreProvider;
    private final ObjectProvider<LoginAttemptProperties> loginAttemptPropertiesProvider;

    /**
     * 로그인
     */
    public LoginResponse login(LoginRequest request) {

        // 동일 사용자의 동시 로그인 요청 직렬화
        User user = userRepository.findByUsernameForUpdate(request.getUsername())
                .orElseThrow(() ->
                        new BusinessException(CommonErrorCode.USER_NOT_FOUND)
                );

        validateUserCredentials(user, request.getPassword());

        UserSecuritySetting setting = userSecuritySettingRepository
                .findByUserId(user.getId())
                .orElseThrow(() ->
                        new BusinessException(CommonErrorCode.INTERNAL_SERVER_ERROR)
                );

        List<UserSession> activeSessions =
                userSessionService.getActiveSessions(user.getId());

        int maxSessionCount = setting.getMaxSessionCount();

        if (activeSessions.size() >= maxSessionCount) {
            return createSessionSelectionResponse(
                    user,
                    activeSessions,
                    maxSessionCount
            );
        }

        return createSession(user);
    }

    /**
     * 사용자가 선택한 기존 세션을 종료하고 신규 로그인을 완료한다.
     */
    public LoginResponse completeSessionSelection(
            SessionSelectionRequest request
    ) {

        LoginAttemptStore loginAttemptStore = getLoginAttemptStore();

        // Redis GETDEL 기반 1회성 소비
        LoginAttempt attempt = loginAttemptStore
                .consume(request.getLoginAttemptId())
                .orElseThrow(() ->
                        new BusinessException(CommonErrorCode.INVALID_REQUEST)
                );

        LocalDateTime now = LocalDateTime.now();

        if (!request.getLoginAttemptId().equals(attempt.getLoginAttemptId())
                || attempt.getExpiresAt() == null
                || !attempt.getExpiresAt().isAfter(now)
                || attempt.getUserId() == null
                || attempt.getUsername() == null) {

            throw new BusinessException(CommonErrorCode.INVALID_REQUEST);
        }

        // 사용자 단위 동시성 제어
        User user = userRepository.findByUsernameForUpdate(attempt.getUsername())
                .orElseThrow(() ->
                        new BusinessException(CommonErrorCode.USER_NOT_FOUND)
                );

        if (!user.getId().equals(attempt.getUserId())) {
            throw new BusinessException(CommonErrorCode.INVALID_REQUEST);
        }

        if (!Boolean.TRUE.equals(user.getEnabled())) {
            throw new BusinessException(CommonErrorCode.ACCOUNT_DISABLED);
        }

        UserSecuritySetting setting = userSecuritySettingRepository
                .findByUserId(user.getId())
                .orElseThrow(() ->
                        new BusinessException(CommonErrorCode.INTERNAL_SERVER_ERROR)
                );

        List<UserSession> activeSessions =
                userSessionService.getActiveSessions(user.getId());

        if (activeSessions.size() < setting.getMaxSessionCount()) {
            throw new BusinessException(CommonErrorCode.INVALID_REQUEST);
        }

        int requiredTerminationCount =
                activeSessions.size() - setting.getMaxSessionCount() + 1;

        if (request.getSessionIds().size() != requiredTerminationCount) {
            throw new BusinessException(CommonErrorCode.INVALID_REQUEST);
        }

        // 선택 세션 소유권·활성 상태 검증 및 이력 저장 후 삭제
        userSessionService.terminateSelectedSessions(
                user,
                request.getSessionIds()
        );

        // 신규 세션 생성
        return createSession(user);
    }

    /**
     * Access Token / Refresh Token 재발급
     */
    public LoginResponse refresh(RefreshTokenRequest request) {

        String refreshToken = request.getRefreshToken();

        JwtProvider.TokenValidationResult validationResult =
                jwtProvider.validateRefreshTokenDetailed(refreshToken);

        if (validationResult == JwtProvider.TokenValidationResult.EXPIRED) {
            throw new BusinessException(CommonErrorCode.EXPIRED_TOKEN);
        }

        if (validationResult != JwtProvider.TokenValidationResult.VALID) {
            throw new BusinessException(CommonErrorCode.INVALID_TOKEN);
        }

        UUID sessionId;

        try {
            sessionId = jwtProvider.getSessionId(refreshToken);
        } catch (Exception e) {
            throw new BusinessException(CommonErrorCode.INVALID_TOKEN);
        }

        String newRefreshToken;

        try {
            Long userId = jwtProvider.getUserId(refreshToken);

            User user = userRepository.findById(userId)
                    .orElseThrow(() ->
                            new BusinessException(CommonErrorCode.INVALID_TOKEN)
                    );

            if (!Boolean.TRUE.equals(user.getEnabled())) {
                throw new BusinessException(CommonErrorCode.ACCOUNT_DISABLED);
            }

            newRefreshToken = jwtProvider.generateRefreshToken(
                    user.getId(),
                    user.getUsername(),
                    user.getRole().name(),
                    sessionId
            );

            LocalDateTime newExpiration = toLocalDateTime(
                    jwtProvider.getExpiration(newRefreshToken)
            );

            UserSession session = userSessionService.rotateRefreshToken(
                    sessionId,
                    refreshToken,
                    newRefreshToken,
                    newExpiration
            );

            return createLoginResponse(
                    session.getUser(),
                    jwtProvider.generateAccessToken(
                            user.getId(),
                            user.getUsername(),
                            user.getRole().name(),
                            sessionId
                    ),
                    newRefreshToken
            );

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(CommonErrorCode.INVALID_TOKEN);
        }
    }

    /**
     * 현재 로그인한 세션 로그아웃
     *
     * 인증된 사용자의 현재 세션만 종료한다.
     * 종료 이력 저장 및 활성 세션 삭제는 UserSessionService에서 처리한다.
     */
    public void logout(Long userId, UUID sessionId) {

        if (userId == null || sessionId == null) {
            throw new BusinessException(CommonErrorCode.INVALID_TOKEN);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new BusinessException(CommonErrorCode.INVALID_TOKEN)
                );

        if (!Boolean.TRUE.equals(user.getEnabled())) {
            throw new BusinessException(CommonErrorCode.ACCOUNT_DISABLED);
        }

        userSessionService.terminateSession(user, sessionId);
    }

    /**
     * 사용자 계정 및 비밀번호 검증
     */
    private void validateUserCredentials(User user, String rawPassword) {

        if (!Boolean.TRUE.equals(user.getEnabled())) {
            throw new BusinessException(CommonErrorCode.ACCOUNT_DISABLED);
        }

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BusinessException(
                    CommonErrorCode.INVALID_USERNAME_OR_PASSWORD
            );
        }
    }

    /**
     * 세션 선택 필요 응답 생성
     */
    private LoginResponse createSessionSelectionResponse(
            User user,
            List<UserSession> activeSessions,
            int maxSessionCount
    ) {

        LoginAttemptStore store = getLoginAttemptStore();

        LocalDateTime now = LocalDateTime.now();
        long ttlSeconds = getLoginAttemptTtlSeconds();

        String loginAttemptId = UUID.randomUUID().toString();

        LoginAttempt attempt = LoginAttempt.builder()
                .loginAttemptId(loginAttemptId)
                .userId(user.getId())
                .username(user.getUsername())
                .createdAt(now)
                .expiresAt(now.plusSeconds(ttlSeconds))
                .build();

        store.save(attempt);

        int requiredTerminationCount =
                activeSessions.size() - maxSessionCount + 1;

        List<LoginResponse.ActiveSessionResponse> sessionResponses =
                activeSessions.stream()
                        .map(session ->
                                LoginResponse.ActiveSessionResponse.builder()
                                        .sessionId(session.getSessionId())
                                        .createdAt(session.getCreatedAt())
                                        .lastAccessedAt(session.getLastAccessedAt())
                                        .build()
                        )
                        .toList();

        return LoginResponse.builder()
                .status(LoginResponse.LoginStatus.SESSION_SELECTION_REQUIRED)
                .userId(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .role(user.getRole().name())
                .loginAttemptId(loginAttemptId)
                .requiredTerminationCount(requiredTerminationCount)
                .activeSessions(sessionResponses)
                .build();
    }

    /**
     * 신규 세션 및 토큰 생성
     */
    private LoginResponse createSession(User user) {

        UUID sessionId = UUID.randomUUID();

        String accessToken = jwtProvider.generateAccessToken(
                user.getId(),
                user.getUsername(),
                user.getRole().name(),
                sessionId
        );

        String refreshToken = jwtProvider.generateRefreshToken(
                user.getId(),
                user.getUsername(),
                user.getRole().name(),
                sessionId
        );

        LocalDateTime refreshTokenExpiresAt = toLocalDateTime(
                jwtProvider.getExpiration(refreshToken)
        );

        userSessionService.createSession(
                user,
                sessionId,
                refreshToken,
                refreshTokenExpiresAt
        );

        return createLoginResponse(user, accessToken, refreshToken);
    }

    /**
     * Redis 로그인 시도 저장소 조회
     */
    private LoginAttemptStore getLoginAttemptStore() {

        LoginAttemptStore store =
                loginAttemptStoreProvider.getIfAvailable();

        if (store == null) {
            throw new BusinessException(
                    CommonErrorCode.INTERNAL_SERVER_ERROR
            );
        }

        return store;
    }

    /**
     * 로그인 시도 TTL 조회
     */
    private long getLoginAttemptTtlSeconds() {

        LoginAttemptProperties properties =
                loginAttemptPropertiesProvider.getIfAvailable();

        if (properties == null || properties.getTtlSeconds() <= 0) {
            return DEFAULT_LOGIN_ATTEMPT_TTL_SECONDS;
        }

        return properties.getTtlSeconds();
    }

    /**
     * 로그인 성공 응답 생성
     */
    private LoginResponse createLoginResponse(
            User user,
            String accessToken,
            String refreshToken
    ) {

        return LoginResponse.builder()
                .status(LoginResponse.LoginStatus.SUCCESS)
                .userId(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .role(user.getRole().name())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiration(jwtProvider.getAccessTokenExpiration())
                .refreshTokenExpiration(jwtProvider.getRefreshTokenExpiration())
                .build();
    }

    private LocalDateTime toLocalDateTime(Date date) {

        return LocalDateTime.ofInstant(
                date.toInstant(),
                ZoneId.systemDefault()
        );
    }
}
