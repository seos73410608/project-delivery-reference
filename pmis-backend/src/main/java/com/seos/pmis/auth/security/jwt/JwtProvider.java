package com.seos.pmis.auth.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Getter
@Component
public class JwtProvider {

    private static final String TOKEN_TYPE_CLAIM = "tokenType";
    private static final String ACCESS_TOKEN_TYPE = "ACCESS";
    private static final String REFRESH_TOKEN_TYPE = "REFRESH";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private SecretKey secretKey;

    public enum TokenValidationResult {
        VALID,
        EXPIRED,
        INVALID,
        INVALID_TYPE
    }

    @PostConstruct
    protected void init() {

        byte[] keyBytes = Decoders.BASE64.decode(secret);

        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Access Token 생성
     */
    public String generateAccessToken(
            Long userId,
            String username,
            String role) {

        return generateToken(
                userId,
                username,
                role,
                accessTokenExpiration,
                ACCESS_TOKEN_TYPE);
    }

    /**
     * Refresh Token 생성
     */
    public String generateRefreshToken(
            Long userId,
            String username,
            String role) {

        return generateToken(
                userId,
                username,
                role,
                refreshTokenExpiration,
                REFRESH_TOKEN_TYPE);
    }

    /**
     * JWT 생성
     */
    private String generateToken(
            Long userId,
            String username,
            String role,
            long expiration,
            String tokenType) {

        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .claim("role", role)
                .claim(TOKEN_TYPE_CLAIM, tokenType)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Claims 조회
     */
    public Claims getClaims(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 사용자 ID 조회
     */
    public Long getUserId(String token) {
        return getClaims(token).get("userId", Long.class);
    }

    /**
     * 사용자명 조회
     */
    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * 권한 조회
     */
    public String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    /**
     * 토큰 유형 조회
     */
    public String getTokenType(String token) {
        return getClaims(token).get(TOKEN_TYPE_CLAIM, String.class);
    }

    /**
     * 만료 시간 조회
     */
    public Date getExpiration(String token) {
        return getClaims(token).getExpiration();
    }

    /**
     * 토큰 만료 여부
     */
    public boolean isTokenExpired(String token) {

        try {
            return getExpiration(token).before(new Date());

        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * Access Token 검증
     */
    public boolean validateAccessToken(String token) {

        return validateAccessTokenDetailed(token) == TokenValidationResult.VALID;
    }

    /**
     * Refresh Token 검증
     */
    public boolean validateRefreshToken(String refreshToken) {

        return validateRefreshTokenDetailed(refreshToken) == TokenValidationResult.VALID;
    }

    /**
     * Access Token 상세 검증 결과
     */
    public TokenValidationResult validateAccessTokenDetailed(
            String token) {

        return validateTokenType(token, ACCESS_TOKEN_TYPE);
    }

    /**
     * Refresh Token 상세 검증 결과
     */
    public TokenValidationResult validateRefreshTokenDetailed(
            String token) {

        return validateTokenType(token, REFRESH_TOKEN_TYPE);
    }

    /**
     * JWT 서명, 만료 및 토큰 유형 검증
     */
    private TokenValidationResult validateTokenType(
            String token,
            String expectedType) {

        if (token == null || token.isBlank()) {
            return TokenValidationResult.INVALID;
        }

        try {

            Claims claims = getClaims(token);

            String actualType = claims.get(TOKEN_TYPE_CLAIM, String.class);

            if (!expectedType.equals(actualType)) {
                return TokenValidationResult.INVALID_TYPE;
            }

            return TokenValidationResult.VALID;

        } catch (ExpiredJwtException e) {

            Object actualType = e.getClaims().get(TOKEN_TYPE_CLAIM);

            if (!expectedType.equals(actualType)) {
                return TokenValidationResult.INVALID_TYPE;
            }

            return TokenValidationResult.EXPIRED;

        } catch (JwtException | IllegalArgumentException e) {

            log.debug("Invalid JWT", e);

            return TokenValidationResult.INVALID;
        }
    }

    /**
     * 기존 호출부 호환용 메서드
     * Access Token만 유효한 것으로 인정한다.
     */
    @Deprecated
    public boolean validateToken(String token) {

        return validateAccessToken(token);
    }
}
