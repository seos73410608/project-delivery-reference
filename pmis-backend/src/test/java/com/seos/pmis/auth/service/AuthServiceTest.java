
package com.seos.pmis.auth.service;

import com.seos.common.exception.BusinessException;
import com.seos.common.exception.code.CommonErrorCode;
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
import com.seos.pmis.user.entity.User;
import com.seos.pmis.user.entity.UserRole;
import com.seos.pmis.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserSecuritySettingRepository userSecuritySettingRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private UserSessionService userSessionService;

    @Mock
    private LoginAttemptStore loginAttemptStore;

    @Mock
    private ObjectProvider<LoginAttemptStore> loginAttemptStoreProvider;

    @Mock
    private ObjectProvider<LoginAttemptProperties> loginAttemptPropertiesProvider;

    private AuthService authService;

    private User user;

    private UUID sessionId;

    @BeforeEach
    void setUp() {

        authService = new AuthService(
                userRepository,
                userSecuritySettingRepository,
                passwordEncoder,
                jwtProvider,
                userSessionService,
                loginAttemptStoreProvider,
                loginAttemptPropertiesProvider
        );

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

    // ---------------------------------------------------------
    // Login
    // ---------------------------------------------------------

    @Test
    void login_최대세션수_미만이면_정상로그인한다() {

        LoginRequest request = createLoginRequest(
                "test-user",
                "raw-password"
        );

        UserSecuritySetting setting = UserSecuritySetting.builder()
                .user(user)
                .maxSessionCount(5)
                .build();

        given(userRepository.findByUsernameForUpdate("test-user"))
                .willReturn(Optional.of(user));

        given(passwordEncoder.matches(
                "raw-password",
                "encoded-password"
        )).willReturn(true);

        given(userSecuritySettingRepository.findByUserId(1L))
                .willReturn(Optional.of(setting));

        given(userSessionService.getActiveSessions(1L))
                .willReturn(List.of());

        stubSuccessfulSessionCreation();

        LoginResponse response = authService.login(request);

        assertThat(response.getStatus())
                .isEqualTo(LoginResponse.LoginStatus.SUCCESS);

        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");

        verify(userSessionService).createSession(
                eq(user),
                any(UUID.class),
                eq("refresh-token"),
                any(LocalDateTime.class)
        );
    }

    @Test
    void login_최대세션수에_도달하면_세션선택응답을_반환한다() {

        LoginRequest request = createLoginRequest(
                "test-user",
                "raw-password"
        );

        UserSecuritySetting setting = UserSecuritySetting.builder()
                .user(user)
                .maxSessionCount(1)
                .build();

        UserSession activeSession = UserSession.builder()
                .sessionId(sessionId)
                .user(user)
                .build();

        given(userRepository.findByUsernameForUpdate("test-user"))
                .willReturn(Optional.of(user));

        given(passwordEncoder.matches(
                "raw-password",
                "encoded-password"
        )).willReturn(true);

        given(userSecuritySettingRepository.findByUserId(1L))
                .willReturn(Optional.of(setting));

        given(userSessionService.getActiveSessions(1L))
                .willReturn(List.of(activeSession));

        given(loginAttemptStoreProvider.getIfAvailable())
                .willReturn(loginAttemptStore);

        given(loginAttemptPropertiesProvider.getIfAvailable())
                .willReturn(null);

        LoginResponse response = authService.login(request);

        assertThat(response.getStatus())
                .isEqualTo(
                        LoginResponse.LoginStatus.SESSION_SELECTION_REQUIRED
                );

        assertThat(response.getLoginAttemptId()).isNotBlank();
        assertThat(response.getRequiredTerminationCount()).isEqualTo(1);
        assertThat(response.getActiveSessions()).hasSize(1);
        assertThat(response.getActiveSessions().get(0).getSessionId())
                .isEqualTo(sessionId);

        assertThat(response.getAccessToken()).isNull();
        assertThat(response.getRefreshToken()).isNull();

        verify(loginAttemptStore).save(any(LoginAttempt.class));

        verify(userSessionService, never()).createSession(
                any(),
                any(UUID.class),
                anyString(),
                any(LocalDateTime.class)
        );
    }

    @Test
    void completeSessionSelection_선택한세션을_종료하고_신규로그인한다() {

        String loginAttemptId = UUID.randomUUID().toString();

        SessionSelectionRequest request =
                createSessionSelectionRequest(
                        loginAttemptId,
                        List.of(sessionId)
                );

        LoginAttempt attempt = LoginAttempt.builder()
                .loginAttemptId(loginAttemptId)
                .userId(1L)
                .username("test-user")
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();

        UserSecuritySetting setting = UserSecuritySetting.builder()
                .user(user)
                .maxSessionCount(1)
                .build();

        UserSession activeSession = UserSession.builder()
                .sessionId(sessionId)
                .user(user)
                .build();

        given(loginAttemptStoreProvider.getIfAvailable())
                .willReturn(loginAttemptStore);

        given(loginAttemptStore.consume(loginAttemptId))
                .willReturn(Optional.of(attempt));

        given(userRepository.findByUsernameForUpdate("test-user"))
                .willReturn(Optional.of(user));

        given(userSecuritySettingRepository.findByUserId(1L))
                .willReturn(Optional.of(setting));

        given(userSessionService.getActiveSessions(1L))
                .willReturn(List.of(activeSession));

        stubSuccessfulSessionCreation();

        LoginResponse response =
                authService.completeSessionSelection(request);

        assertThat(response.getStatus())
                .isEqualTo(LoginResponse.LoginStatus.SUCCESS);

        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");

        verify(loginAttemptStore).consume(loginAttemptId);

        verify(userSessionService).terminateSelectedSessions(
                user,
                List.of(sessionId)
        );

        verify(userSessionService).createSession(
                eq(user),
                any(UUID.class),
                eq("refresh-token"),
                any(LocalDateTime.class)
        );
    }

    @Test
    void completeSessionSelection_현재활성세션이_최대수보다_적으면_요청을거부한다() {

        String loginAttemptId = UUID.randomUUID().toString();

        SessionSelectionRequest request =
                createSessionSelectionRequest(
                        loginAttemptId,
                        List.of(sessionId)
                );

        LoginAttempt attempt = LoginAttempt.builder()
                .loginAttemptId(loginAttemptId)
                .userId(1L)
                .username("test-user")
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();

        UserSecuritySetting setting = UserSecuritySetting.builder()
                .user(user)
                .maxSessionCount(2)
                .build();

        given(loginAttemptStoreProvider.getIfAvailable())
                .willReturn(loginAttemptStore);

        given(loginAttemptStore.consume(loginAttemptId))
                .willReturn(Optional.of(attempt));

        given(userRepository.findByUsernameForUpdate("test-user"))
                .willReturn(Optional.of(user));

        given(userSecuritySettingRepository.findByUserId(1L))
                .willReturn(Optional.of(setting));

        given(userSessionService.getActiveSessions(1L))
                .willReturn(List.of());

        assertThatThrownBy(() ->
                authService.completeSessionSelection(request)
        )
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(CommonErrorCode.INVALID_REQUEST);

        verify(loginAttemptStore).consume(loginAttemptId);

        verify(userSessionService, never()).terminateSelectedSessions(
                any(),
                any()
        );

        verify(userSessionService, never()).createSession(
                any(),
                any(UUID.class),
                anyString(),
                any(LocalDateTime.class)
        );
    }

    // ---------------------------------------------------------
    // Logout
    // ---------------------------------------------------------

    @Test
    void logout_현재인증세션을_정상적으로종료한다() {

        given(userRepository.findById(1L))
                .willReturn(Optional.of(user));

        authService.logout(1L, sessionId);

        verify(userRepository).findById(1L);

        verify(userSessionService).terminateSession(
                user,
                sessionId
        );
    }

    @Test
    void logout_존재하지않는사용자이면_INVALID_TOKEN을_반환한다() {

        given(userRepository.findById(999L))
                .willReturn(Optional.empty());

        assertThatThrownBy(() ->
                authService.logout(999L, sessionId)
        )
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(CommonErrorCode.INVALID_TOKEN);

        verify(userSessionService, never())
                .terminateSession(any(), any());
    }

    @Test
    void logout_비활성화된사용자이면_ACCOUNT_DISABLED를_반환한다() {

        User disabledUser = User.builder()
                .id(1L)
                .username("test-user")
                .password("encoded-password")
                .name("테스트 사용자")
                .email("test@example.com")
                .role(UserRole.ROLE_USER)
                .enabled(false)
                .build();

        given(userRepository.findById(1L))
                .willReturn(Optional.of(disabledUser));

        assertThatThrownBy(() ->
                authService.logout(1L, sessionId)
        )
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(CommonErrorCode.ACCOUNT_DISABLED);

        verify(userSessionService, never())
                .terminateSession(any(), any());
    }

    @Test
    void logout_사용자ID가_null이면_INVALID_TOKEN을_반환한다() {

        assertThatThrownBy(() ->
                authService.logout(null, sessionId)
        )
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(CommonErrorCode.INVALID_TOKEN);

        verifyNoInteractions(userRepository);
        verifyNoInteractions(userSessionService);
    }

    @Test
    void logout_세션ID가_null이면_INVALID_TOKEN을_반환한다() {

        assertThatThrownBy(() ->
                authService.logout(1L, null)
        )
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(CommonErrorCode.INVALID_TOKEN);

        verifyNoInteractions(userRepository);
        verifyNoInteractions(userSessionService);
    }

    // ---------------------------------------------------------
    // Refresh Token
    // ---------------------------------------------------------

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

    // ---------------------------------------------------------
    // Test Helpers
    // ---------------------------------------------------------

    private LoginRequest createLoginRequest(
            String username,
            String password
    ) {

        LoginRequest request = new LoginRequest();

        ReflectionTestUtils.setField(request, "username", username);
        ReflectionTestUtils.setField(request, "password", password);

        return request;
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

    private SessionSelectionRequest createSessionSelectionRequest(
            String loginAttemptId,
            List<UUID> sessionIds
    ) {

        SessionSelectionRequest request = new SessionSelectionRequest();

        request.setLoginAttemptId(loginAttemptId);
        request.setSessionIds(sessionIds);

        return request;
    }

    private void stubSuccessfulSessionCreation() {

        Date expiration = new Date(
                System.currentTimeMillis() + 1_209_600_000L
        );

        given(jwtProvider.generateAccessToken(
                eq(1L),
                eq("test-user"),
                eq("ROLE_USER"),
                any(UUID.class)
        )).willReturn("access-token");

        given(jwtProvider.generateRefreshToken(
                eq(1L),
                eq("test-user"),
                eq("ROLE_USER"),
                any(UUID.class)
        )).willReturn("refresh-token");

        given(jwtProvider.getExpiration("refresh-token"))
                .willReturn(expiration);

        given(jwtProvider.getAccessTokenExpiration())
                .willReturn(120_000L);

        given(jwtProvider.getRefreshTokenExpiration())
                .willReturn(600_000L);
    }
}
