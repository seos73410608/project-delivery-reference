
package com.seos.pmis.auth.service;

import com.seos.common.exception.BusinessException;
import com.seos.common.exception.code.CommonErrorCode;
import com.seos.pmis.auth.dto.request.RefreshTokenRequest;
import com.seos.pmis.auth.dto.response.LoginResponse;
import com.seos.pmis.auth.entity.RefreshToken;
import com.seos.pmis.auth.security.jwt.JwtProvider;
import com.seos.pmis.user.entity.User;
import com.seos.pmis.user.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private com.seos.pmis.user.repository.UserRepository userRepository;

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("test-user")
                .password("encoded-password")
                .name("테스트 사용자")
                .email("test@example.com")
                .role(UserRole.ROLE_USER)
                .enabled(true)
                .build();
    }

    @Test
    void refresh_AccessToken으로_요청하면_INVALID_TOKEN을_반환한다() {
        // given
        String accessToken = "access-token";

        RefreshTokenRequest request = createRequest(accessToken);

        given(jwtProvider.validateRefreshTokenDetailed(accessToken))
                .willReturn(JwtProvider.TokenValidationResult.INVALID_TYPE);

        // when & then
        assertThatThrownBy(() -> authService.refresh(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(CommonErrorCode.INVALID_TOKEN);

        verifyNoInteractions(refreshTokenService);
    }

    @Test
    void refresh_만료된RefreshToken이면_EXPIRED_TOKEN을_반환한다() {
        // given
        String refreshToken = "expired-refresh-token";

        RefreshTokenRequest request = createRequest(refreshToken);

        given(jwtProvider.validateRefreshTokenDetailed(refreshToken))
                .willReturn(JwtProvider.TokenValidationResult.EXPIRED);

        // when & then
        assertThatThrownBy(() -> authService.refresh(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(CommonErrorCode.EXPIRED_TOKEN);

        verifyNoInteractions(refreshTokenService);
    }

    @Test
    void refresh_DB에_없는RefreshToken이면_INVALID_TOKEN을_반환한다() {
        // given
        String refreshToken = "not-saved-refresh-token";

        RefreshTokenRequest request = createRequest(refreshToken);

        given(jwtProvider.validateRefreshTokenDetailed(refreshToken))
                .willReturn(JwtProvider.TokenValidationResult.VALID);

        given(refreshTokenService.findByToken(refreshToken))
                .willThrow(new BusinessException(CommonErrorCode.INVALID_TOKEN));

        // when & then
        assertThatThrownBy(() -> authService.refresh(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(CommonErrorCode.INVALID_TOKEN);

        verify(jwtProvider, never()).generateAccessToken(
                any(),
                anyString(),
                anyString()
        );
    }

    @Test
    void refresh_DB에서_만료된RefreshToken이면_EXPIRED_TOKEN을_반환한다() {
        // given
        String refreshToken = "db-expired-refresh-token";

        RefreshTokenRequest request = createRequest(refreshToken);

        RefreshToken savedToken = RefreshToken.builder()
                .user(user)
                .token(refreshToken)
                .expiredAt(LocalDateTime.now().minusMinutes(1))
                .build();

        given(jwtProvider.validateRefreshTokenDetailed(refreshToken))
                .willReturn(JwtProvider.TokenValidationResult.VALID);

        given(refreshTokenService.findByToken(refreshToken))
                .willReturn(savedToken);

        // when & then
        assertThatThrownBy(() -> authService.refresh(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(CommonErrorCode.EXPIRED_TOKEN);

        verify(jwtProvider, never()).generateAccessToken(
                any(),
                anyString(),
                anyString()
        );
    }

    @Test
    void refresh_정상RefreshToken이면_새로운토큰을_발급한다() {
        // given
        String refreshToken = "valid-refresh-token";
        String newAccessToken = "new-access-token";
        String newRefreshToken = "new-refresh-token";

        RefreshTokenRequest request = createRequest(refreshToken);

        RefreshToken savedToken = RefreshToken.builder()
                .user(user)
                .token(refreshToken)
                .expiredAt(LocalDateTime.now().plusDays(7))
                .build();

        Date newExpiration = new Date(
                System.currentTimeMillis() + 1_209_600_000L
        );

        given(jwtProvider.validateRefreshTokenDetailed(refreshToken))
                .willReturn(JwtProvider.TokenValidationResult.VALID);

        given(refreshTokenService.findByToken(refreshToken))
                .willReturn(savedToken);

        given(jwtProvider.generateAccessToken(
                1L,
                "test-user",
                "ROLE_USER"
        )).willReturn(newAccessToken);

        given(jwtProvider.generateRefreshToken(
                1L,
                "test-user",
                "ROLE_USER"
        )).willReturn(newRefreshToken);

        given(jwtProvider.getExpiration(newRefreshToken))
                .willReturn(newExpiration);

        given(jwtProvider.getAccessTokenExpiration())
                .willReturn(120_000L);

        given(jwtProvider.getRefreshTokenExpiration())
                .willReturn(600_000L);

        // when
        LoginResponse response = authService.refresh(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("test-user");
        assertThat(response.getName()).isEqualTo("테스트 사용자");
        assertThat(response.getRole()).isEqualTo("ROLE_USER");
        assertThat(response.getAccessToken()).isEqualTo(newAccessToken);
        assertThat(response.getRefreshToken()).isEqualTo(newRefreshToken);
        assertThat(response.getAccessTokenExpiration()).isEqualTo(120_000L);
        assertThat(response.getRefreshTokenExpiration()).isEqualTo(600_000L);

        verify(refreshTokenService).save(
                org.mockito.ArgumentMatchers.eq(user),
                org.mockito.ArgumentMatchers.eq(newRefreshToken),
                any(LocalDateTime.class)
        );
    }

    private RefreshTokenRequest createRequest(String refreshToken) {
        RefreshTokenRequest request = new RefreshTokenRequest();

        ReflectionTestUtils.setField(
                request,
                "refreshToken",
                refreshToken
        );

        return request;
    }
}
