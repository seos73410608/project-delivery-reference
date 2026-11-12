
package com.seos.pmis.auth.security.filter;

import com.seos.pmis.auth.security.jwt.JwtProvider;
import com.seos.pmis.auth.security.principal.UserPrincipal;
import com.seos.pmis.auth.service.CustomUserDetailsService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private CustomUserDetailsService userDetailsService;

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
    void doFilter_유효한JWT와_활성사용자는_인증된다() throws Exception {

        UserPrincipal userPrincipal = createUserPrincipal(true);

        given(jwtProvider.validateToken("valid-token"))
                .willReturn(true);

        given(jwtProvider.getUsername("valid-token"))
                .willReturn("test-user");

        given(userDetailsService.loadUserByUsername("test-user"))
                .willReturn(userPrincipal);

        MockHttpServletRequest request = createRequest("valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = (req, res) -> {
        };

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNotNull();

        assertThat(SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal())
                .isSameAs(userPrincipal);
    }

    @Test
    void doFilter_유효한JWT와_비활성사용자는_인증되지않는다() throws Exception {

        UserPrincipal userPrincipal = createUserPrincipal(false);

        given(jwtProvider.validateToken("valid-token"))
                .willReturn(true);

        given(jwtProvider.getUsername("valid-token"))
                .willReturn("test-user");

        given(userDetailsService.loadUserByUsername("test-user"))
                .willReturn(userPrincipal);

        MockHttpServletRequest request = createRequest("valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = (req, res) -> {
        };

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNull();
    }

    @Test
    void doFilter_유효하지않은JWT는_인증되지않는다() throws Exception {

        given(jwtProvider.validateToken("invalid-token"))
                .willReturn(false);

        MockHttpServletRequest request = createRequest("invalid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = (req, res) -> {
        };

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNull();

        verifyNoInteractions(userDetailsService);
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
