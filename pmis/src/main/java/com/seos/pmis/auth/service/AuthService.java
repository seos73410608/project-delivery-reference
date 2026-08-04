package com.seos.pmis.auth.service;

import com.seos.pmis.auth.dto.request.LoginRequest;
import com.seos.pmis.auth.dto.response.LoginResponse;
import com.seos.pmis.auth.security.jwt.JwtProvider;
import com.seos.pmis.common.exception.BusinessException;
import com.seos.pmis.common.exception.code.CommonErrorCode;
import com.seos.pmis.user.entity.User;
import com.seos.pmis.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtProvider jwtProvider;

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
}