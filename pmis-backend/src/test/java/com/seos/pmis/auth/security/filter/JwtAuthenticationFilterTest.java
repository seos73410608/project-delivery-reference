package com.seos.pmis.auth.security.filter;

import com.seos.pmis.auth.security.jwt.JwtProvider;
import com.seos.pmis.auth.security.principal.UserPrincipal;
import com.seos.pmis.auth.service.CustomUserDetailsService;
import com.seos.pmis.auth.service.UserSessionService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private UserSessionService userSessionService;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilter_유효한AccessToken과_활성사용자는_인증된다() throws Exception {

        UserPrincipal userPrincipal = createUserPrincipal(true);
        UUID sessionId = UUID.randomUUID();

        given(jwtProvider.validateAccessToken("valid-access-token"))
                .willReturn(true);

        given(jwtProvider.getUserId("valid-access-token"))
                .willReturn(1L);

        given(jwtProvider.getUsername("valid-access-token"))
                .willReturn("test-user");

        given(jwtProvider.getSessionId("valid-access-token"))
                .willReturn(sessionId);

        given(userDetailsService.loadUserByUsername("test-user"))
                .willReturn(userPrincipal);

        given(userSessionService.validateAccessSession(1L, sessionId))
                .willReturn(true);

        MockHttpServletRequest request = createRequest("valid-access-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = (req, res) -> {
        };

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNotNull();

        UserPrincipal authenticatedPrincipal =
                (UserPrincipal) SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal();

        assertThat(authenticatedPrincipal.getUserId())
                .isEqualTo(userPrincipal.getUserId());

        assertThat(authenticatedPrincipal.getUsername())
                .isEqualTo(userPrincipal.getUsername());

        assertThat(authenticatedPrincipal.getSessionId())
                .isEqualTo(sessionId);
    }

    @Test
    void doFilter_유효한AccessToken과_비활성사용자는_인증되지않는다() throws Exception {

        UserPrincipal userPrincipal = createUserPrincipal(false);
        UUID sessionId = UUID.randomUUID();

        given(jwtProvider.validateAccessToken("valid-access-token"))
                .willReturn(true);

        given(jwtProvider.getUserId("valid-access-token"))
                .willReturn(1L);

        given(jwtProvider.getUsername("valid-access-token"))
                .willReturn("test-user");

        given(jwtProvider.getSessionId("valid-access-token"))
                .willReturn(sessionId);

        given(userDetailsService.loadUserByUsername("test-user"))
                .willReturn(userPrincipal);

        MockHttpServletRequest request = createRequest("valid-access-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = (req, res) -> {
        };

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNull();
    }

    @Test
    void doFilter_유효하지않은AccessToken은_인증되지않는다() throws Exception {

        given(jwtProvider.validateAccessToken("invalid-access-token"))
                .willReturn(false);

        MockHttpServletRequest request = createRequest("invalid-access-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = (req, res) -> {
        };

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNull();

        verifyNoInteractions(userDetailsService);
        verifyNoInteractions(userSessionService);
    }

    @Test
    void doFilter_RefreshToken은_인증되지않는다() throws Exception {

        given(jwtProvider.validateAccessToken("valid-refresh-token"))
                .willReturn(false);

        MockHttpServletRequest request = createRequest("valid-refresh-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = (req, res) -> {
        };

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNull();

        verifyNoInteractions(userDetailsService);
        verifyNoInteractions(userSessionService);
    }

    private MockHttpServletRequest createRequest(String token) {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);

        return request;
    }

    private UserPrincipal createUserPrincipal(boolean enabled) {

        return UserPrincipal.builder()
                .userId(1L)
                .username("test-user")
                .password("encoded-password")
                .role("ROLE_USER")
                .enabled(enabled)
                .build();
    }
}