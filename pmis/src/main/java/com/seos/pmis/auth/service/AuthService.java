package com.seos.pmis.auth.service;

import com.seos.pmis.auth.dto.request.LoginRequest;
import com.seos.pmis.auth.dto.request.RefreshTokenRequest;
import com.seos.pmis.auth.dto.response.LoginResponse;
import com.seos.pmis.auth.entity.RefreshToken;
import com.seos.pmis.auth.security.jwt.JwtProvider;
import com.seos.pmis.common.exception.BusinessException;
import com.seos.pmis.common.exception.code.CommonErrorCode;
import com.seos.pmis.user.entity.User;
import com.seos.pmis.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtProvider jwtProvider;

    private final RefreshTokenService refreshTokenService;

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

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            throw new BusinessException(
                    CommonErrorCode.INVALID_USERNAME_OR_PASSWORD
            );
        }

        String accessToken = jwtProvider.generateAccessToken(
                user.getId(),
                user.getUsername(),
                user.getRole().name()
        );

        String refreshToken = jwtProvider.generateRefreshToken(
                user.getId(),
                user.getUsername(),
                user.getRole().name()
        );

        Date expiration = jwtProvider.getExpiration(refreshToken);

        refreshTokenService.save(
                user,
                refreshToken,
                LocalDateTime.ofInstant(
                        expiration.toInstant(),
                        ZoneId.systemDefault()
                )
        );

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

    /**
     * Access Token / Refresh Token 재발급
     */
    public LoginResponse refresh(RefreshTokenRequest request) {

        if (!jwtProvider.validateRefreshToken(request.getRefreshToken())) {
            throw new BusinessException(CommonErrorCode.INVALID_TOKEN);
        }

        RefreshToken savedToken =
                refreshTokenService.findByToken(request.getRefreshToken());

        if (savedToken.isExpired()) {
            throw new BusinessException(CommonErrorCode.EXPIRED_TOKEN);
        }

        User user = savedToken.getUser();

        String newAccessToken = jwtProvider.generateAccessToken(
                user.getId(),
                user.getUsername(),
                user.getRole().name()
        );

        String newRefreshToken = jwtProvider.generateRefreshToken(
                user.getId(),
                user.getUsername(),
                user.getRole().name()
        );

        Date expiration = jwtProvider.getExpiration(newRefreshToken);

        refreshTokenService.save(
                user,
                newRefreshToken,
                LocalDateTime.ofInstant(
                        expiration.toInstant(),
                        ZoneId.systemDefault()
                )
        );

        return LoginResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .role(user.getRole().name())
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .accessTokenExpiration(jwtProvider.getAccessTokenExpiration())
                .refreshTokenExpiration(jwtProvider.getRefreshTokenExpiration())
                .build();
    }
}