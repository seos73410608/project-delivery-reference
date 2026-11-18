
package com.seos.pmis.auth.security.filter;

import com.seos.pmis.auth.security.jwt.JwtProvider;
import com.seos.pmis.auth.security.principal.UserPrincipal;
import com.seos.pmis.auth.service.CustomUserDetailsService;
import com.seos.pmis.auth.service.UserSessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService userDetailsService;
    private final UserSessionService userSessionService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {

            String token = resolveToken(request);

            if (StringUtils.hasText(token)
                    && jwtProvider.validateAccessToken(token)) {

                authenticate(token, request);
            }

        } catch (Exception e) {

            log.debug("JWT authentication failed: {}", e.getMessage());

            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    /**
     * JWT 및 활성 세션 검증 후 인증 처리
     */
    private void authenticate(
            String token,
            HttpServletRequest request
    ) {

        // JWT 사용자 및 세션 정보 조회
        Long userId = jwtProvider.getUserId(token);
        String username = jwtProvider.getUsername(token);
        UUID sessionId = jwtProvider.getSessionId(token);

        // 사용자 정보 조회
        UserDetails userDetails =
                userDetailsService.loadUserByUsername(username);

        if (!(userDetails instanceof UserPrincipal principal)) {
            throw new IllegalStateException(
                    "인증 정보가 UserPrincipal 형식이 아닙니다."
            );
        }

        // JWT 사용자 ID와 실제 사용자 ID 일치 여부 확인
        if (!userId.equals(principal.getUserId())) {
            throw new IllegalStateException(
                    "JWT 사용자 정보가 일치하지 않습니다."
            );
        }

        // 계정 활성화 여부 확인
        if (!principal.isEnabled()) {
            throw new IllegalStateException(
                    "비활성화된 사용자입니다."
            );
        }

        // DB에서 해당 사용자의 활성 세션 확인
        if (!userSessionService.validateAccessSession(userId, sessionId)) {
            throw new IllegalStateException(
                    "유효하지 않거나 종료된 세션입니다."
            );
        }

        // 세션 정보를 포함한 인증 Principal 생성
        UserPrincipal authenticatedPrincipal = UserPrincipal.builder()
                .userId(principal.getUserId())
                .username(principal.getUsername())
                .password(principal.getPassword())
                .role(principal.getRole())
                .sessionId(sessionId)
                .enabled(principal.isEnabled())
                .build();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        authenticatedPrincipal,
                        null,
                        authenticatedPrincipal.getAuthorities()
                );

        authentication.setDetails(
                new WebAuthenticationDetailsSource()
                        .buildDetails(request)
        );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);
    }

    /**
     * Authorization 헤더에서 Bearer Token 추출
     */
    private String resolveToken(HttpServletRequest request) {

        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken)
                && bearerToken.startsWith("Bearer ")) {

            return bearerToken.substring(7);
        }

        return null;
    }
}
