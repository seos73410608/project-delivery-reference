
package com.seos.pmis.auth.service;

import com.seos.common.exception.BusinessException;
import com.seos.common.exception.code.CommonErrorCode;
import com.seos.pmis.auth.dto.request.RefreshTokenRequest;
import com.seos.pmis.auth.dto.response.LoginResponse;
import com.seos.pmis.auth.entity.UserSession;
import com.seos.pmis.auth.security.jwt.JwtProvider;
import com.seos.pmis.user.entity.User;
import com.seos.pmis.user.entity.UserRole;
import com.seos.pmis.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

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
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private UserSessionService userSessionService;

    @InjectMocks
    private AuthService authService;

    private User user;

    private UUID sessionId;

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

        sessionId = UUID.randomUUID();
    }

    @Test
    void refresh_AccessToken으로_요청하면_INVALID_TOKEN을_반환한다() {

        String accessToken = "access-token";

        RefreshTokenRequest request = createRequest(accessToken);

        given(jwtProvider.validateRefreshTokenDetailed(accessToken))
                .willReturn(JwtProvider.TokenValidationResult.INVALID_TYPE);

        assertThatThrownBy(() -> authService.refresh(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(CommonErrorCode.INVALID_TOKEN);

        verifyNoInteractions(userSessionService);
    }

    @Test
    void refresh_만료된RefreshToken이면_EXPIRED_TOKEN을_반환한다() {

        String refreshToken = "expired-refresh-token";

        RefreshTokenRequest request = createRequest(refreshToken);

        given(jwtProvider.validateRefreshTokenDetailed(refreshToken))
                .willReturn(JwtProvider.TokenValidationResult.EXPIRED);

        assertThatThrownBy(() -> authService.refresh(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(CommonErrorCode.EXPIRED_TOKEN);

        verifyNoInteractions(userSessionService);
        verifyNoInteractions(userRepository);
    }

    @Test
    void refresh_세션ID가_없는RefreshToken이면_INVALID_TOKEN을_반환한다() {

        String refreshToken = "refresh-token-without-session";

        RefreshTokenRequest request = createRequest(refreshToken);

        given(jwtProvider.validateRefreshTokenDetailed(refreshToken))
                .willReturn(JwtProvider.TokenValidationResult.VALID);

        given(jwtProvider.getSessionId(refreshToken))
                .willThrow(new IllegalArgumentException("sessionId 없음"));

        assertThatThrownBy(() -> authService.refresh(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(CommonErrorCode.INVALID_TOKEN);

        verifyNoInteractions(userSessionService);
        verifyNoInteractions(userRepository);
    }

    @Test
    void refresh_존재하지_않는사용자이면_INVALID_TOKEN을_반환한다() {

        String refreshToken = "valid-refresh-token";

        RefreshTokenRequest request = createRequest(refreshToken);

        given(jwtProvider.validateRefreshTokenDetailed(refreshToken))
                .willReturn(JwtProvider.TokenValidationResult.VALID);

        given(jwtProvider.getSessionId(refreshToken))
                .willReturn(sessionId);

        given(jwtProvider.getUserId(refreshToken))
                .willReturn(999L);

        given(userRepository.findById(999L))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refresh(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(CommonErrorCode.INVALID_TOKEN);

        verifyNoInteractions(userSessionService);
    }

    @Test
    void refresh_비활성화된사용자이면_ACCOUNT_DISABLED를_반환한다() {

        User disabledUser = User.builder()
                .id(1L)
                .username("test-user")
                .password("encoded-password")
                .name("테스트 사용자")
                .email("test@example.com")
                .role(UserRole.ROLE_USER)
                .enabled(false)
                .build();

        String refreshToken = "valid-refresh-token";

        RefreshTokenRequest request = createRequest(refreshToken);

        given(jwtProvider.validateRefreshTokenDetailed(refreshToken))
                .willReturn(JwtProvider.TokenValidationResult.VALID);

        given(jwtProvider.getSessionId(refreshToken))
                .willReturn(sessionId);

        given(jwtProvider.getUserId(refreshToken))
                .willReturn(1L);

        given(userRepository.findById(1L))
                .willReturn(Optional.of(disabledUser));

        assertThatThrownBy(() -> authService.refresh(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(CommonErrorCode.ACCOUNT_DISABLED);

        verifyNoInteractions(userSessionService);
    }

    @Test
    void refresh_정상RefreshToken이면_새로운토큰을_발급한다() {

        String refreshToken = "valid-refresh-token";
        String newAccessToken = "new-access-token";
        String newRefreshToken = "new-refresh-token";

        RefreshTokenRequest request = createRequest(refreshToken);

        Date newExpiration = new Date(
                System.currentTimeMillis() + 1_209_600_000L
        );

        UserSession session = UserSession.builder()
                .sessionId(sessionId)
                .user(user)
                .refreshTokenHash("new-refresh-token-hash")
                .refreshTokenExpiresAt(
                        LocalDateTime.now().plusDays(14)
                )
                .build();

        given(jwtProvider.validateRefreshTokenDetailed(refreshToken))
                .willReturn(JwtProvider.TokenValidationResult.VALID);

        given(jwtProvider.getSessionId(refreshToken))
                .willReturn(sessionId);

        given(jwtProvider.getUserId(refreshToken))
                .willReturn(1L);

        given(userRepository.findById(1L))
                .willReturn(Optional.of(user));

        given(jwtProvider.generateRefreshToken(
                1L,
                "test-user",
                "ROLE_USER",
                sessionId
        )).willReturn(newRefreshToken);

        given(jwtProvider.getExpiration(newRefreshToken))
                .willReturn(newExpiration);

        given(userSessionService.rotateRefreshToken(
                sessionId,
                refreshToken,
                newRefreshToken,
                LocalDateTime.ofInstant(
                        newExpiration.toInstant(),
                        java.time.ZoneId.systemDefault()
                )
        )).willReturn(session);

        given(jwtProvider.generateAccessToken(
                1L,
                "test-user",
                "ROLE_USER",
                sessionId
        )).willReturn(newAccessToken);

        given(jwtProvider.getAccessTokenExpiration())
                .willReturn(120_000L);

        given(jwtProvider.getRefreshTokenExpiration())
                .willReturn(600_000L);

        LoginResponse response = authService.refresh(request);

        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("test-user");
        assertThat(response.getName()).isEqualTo("테스트 사용자");
        assertThat(response.getRole()).isEqualTo("ROLE_USER");
        assertThat(response.getAccessToken()).isEqualTo(newAccessToken);
        assertThat(response.getRefreshToken()).isEqualTo(newRefreshToken);
        assertThat(response.getAccessTokenExpiration()).isEqualTo(120_000L);
        assertThat(response.getRefreshTokenExpiration()).isEqualTo(600_000L);

        verify(userSessionService).rotateRefreshToken(
                sessionId,
                refreshToken,
                newRefreshToken,
                LocalDateTime.ofInstant(
                        newExpiration.toInstant(),
                        java.time.ZoneId.systemDefault()
                )
        );
    }

    @Test
    void refresh_세션에_등록되지_않은토큰이면_INVALID_TOKEN을_반환한다() {

        String refreshToken = "not-registered-refresh-token";

        RefreshTokenRequest request = createRequest(refreshToken);

        given(jwtProvider.validateRefreshTokenDetailed(refreshToken))
                .willReturn(JwtProvider.TokenValidationResult.VALID);

        given(jwtProvider.getSessionId(refreshToken))
                .willReturn(sessionId);

        given(jwtProvider.getUserId(refreshToken))
                .willReturn(1L);

        given(userRepository.findById(1L))
                .willReturn(Optional.of(user));

        given(jwtProvider.generateRefreshToken(
                1L,
                "test-user",
                "ROLE_USER",
                sessionId
        )).willReturn("new-refresh-token");

        given(jwtProvider.getExpiration("new-refresh-token"))
                .willReturn(new Date(
                        System.currentTimeMillis() + 1_209_600_000L
                ));

        given(userSessionService.rotateRefreshToken(
                any(UUID.class),
                anyString(),
                anyString(),
                any(LocalDateTime.class)
        )).willThrow(new BusinessException(CommonErrorCode.INVALID_TOKEN));

        assertThatThrownBy(() -> authService.refresh(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(CommonErrorCode.INVALID_TOKEN);

        verify(jwtProvider, never()).generateAccessToken(
                any(),
                anyString(),
                anyString(),
                any(UUID.class)
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
