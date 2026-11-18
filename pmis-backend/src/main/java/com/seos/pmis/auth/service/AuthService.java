
package com.seos.pmis.auth.service;

import com.seos.pmis.auth.dto.request.LoginRequest;
import com.seos.pmis.auth.dto.request.RefreshTokenRequest;
import com.seos.pmis.auth.dto.response.LoginResponse;
import com.seos.pmis.auth.entity.UserSession;
import com.seos.pmis.auth.security.jwt.JwtProvider;
import com.seos.common.exception.BusinessException;
import com.seos.common.exception.code.CommonErrorCode;
import com.seos.pmis.user.entity.User;
import com.seos.pmis.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final UserSessionService userSessionService;

    /**
     * 로그인
     */
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new BusinessException(CommonErrorCode.USER_NOT_FOUND)
                );

        if (!Boolean.TRUE.equals(user.getEnabled())) {
            throw new BusinessException(CommonErrorCode.ACCOUNT_DISABLED);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(
                    CommonErrorCode.INVALID_USERNAME_OR_PASSWORD
            );
        }

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

    private LoginResponse createLoginResponse(
            User user,
            String accessToken,
            String refreshToken
    ) {
        return LoginResponse.builder()
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
