package com.seos.pmis.auth.service;

import com.seos.pmis.auth.entity.RefreshToken;
import com.seos.pmis.auth.repository.RefreshTokenRepository;
import com.seos.pmis.common.exception.BusinessException;
import com.seos.pmis.common.exception.code.CommonErrorCode;
import com.seos.pmis.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Refresh Token 저장
     */
    public RefreshToken save(
            User user,
            String token,
            LocalDateTime expiredAt
    ) {

        RefreshToken refreshToken = refreshTokenRepository
                .findByUser(user)
                .orElse(
                        RefreshToken.builder()
                                .user(user)
                                .build()
                );

        refreshToken.updateToken(token, expiredAt);

        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Refresh Token 조회
     */
    @Transactional(readOnly = true)
    public RefreshToken findByToken(String token) {

        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() ->
                        new BusinessException(
                                CommonErrorCode.INVALID_TOKEN
                        )
                );
    }

    /**
     * 사용자 Refresh Token 조회
     */
    @Transactional(readOnly = true)
    public RefreshToken findByUser(User user) {

        return refreshTokenRepository.findByUser(user)
                .orElseThrow(() ->
                        new BusinessException(
                                CommonErrorCode.INVALID_TOKEN
                        )
                );
    }

    /**
     * Refresh Token 삭제
     */
    public void delete(User user) {
        refreshTokenRepository.deleteByUser(user);
    }

    /**
     * Token 삭제
     */
    public void delete(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

    /**
     * Refresh Token 만료 여부 확인
     */
    @Transactional(readOnly = true)
    public void validate(String token) {

        RefreshToken refreshToken = findByToken(token);

        if (refreshToken.isExpired()) {
            throw new BusinessException(
                    CommonErrorCode.EXPIRED_TOKEN
            );
        }
    }
}