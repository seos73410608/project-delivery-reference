
package com.seos.pmis.auth.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtProviderTest {

    private static final String TOKEN_TYPE_CLAIM = "tokenType";
    private static final String ACCESS_TOKEN_TYPE = "ACCESS";
    private static final String REFRESH_TOKEN_TYPE = "REFRESH";

    private JwtProvider jwtProvider;
    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        // 테스트 전용 키를 사용한다. 실제 설정의 JWT 시크릿은 사용하지 않는다.
        byte[] keyBytes = new byte[32];
        for (int i = 0; i < keyBytes.length; i++) {
            keyBytes[i] = (byte) (i + 1);
        }

        secretKey = Keys.hmacShaKeyFor(keyBytes);

        jwtProvider = new JwtProvider();
        ReflectionTestUtils.setField(jwtProvider, "secretKey", secretKey);
    }

    @Test
    void validateAccessToken_returnsValid_forAccessToken() {
        String token = createToken(
                ACCESS_TOKEN_TYPE,
                new Date(System.currentTimeMillis() + 60_000));

        assertTrue(jwtProvider.validateAccessToken(token));
        assertEquals(
                JwtProvider.TokenValidationResult.VALID,
                jwtProvider.validateAccessTokenDetailed(token));
    }

    @Test
    void validateRefreshToken_returnsValid_forRefreshToken() {
        String token = createToken(
                REFRESH_TOKEN_TYPE,
                new Date(System.currentTimeMillis() + 60_000));

        assertTrue(jwtProvider.validateRefreshToken(token));
        assertEquals(
                JwtProvider.TokenValidationResult.VALID,
                jwtProvider.validateRefreshTokenDetailed(token));
    }

    @Test
    void validateAccessToken_returnsInvalidType_forRefreshToken() {
        String token = createToken(
                REFRESH_TOKEN_TYPE,
                new Date(System.currentTimeMillis() + 60_000));

        assertFalse(jwtProvider.validateAccessToken(token));
        assertEquals(
                JwtProvider.TokenValidationResult.INVALID_TYPE,
                jwtProvider.validateAccessTokenDetailed(token));
    }

    @Test
    void validateRefreshToken_returnsInvalidType_forAccessToken() {
        String token = createToken(
                ACCESS_TOKEN_TYPE,
                new Date(System.currentTimeMillis() + 60_000));

        assertFalse(jwtProvider.validateRefreshToken(token));
        assertEquals(
                JwtProvider.TokenValidationResult.INVALID_TYPE,
                jwtProvider.validateRefreshTokenDetailed(token));
    }

    @Test
    void validateAccessToken_returnsExpired_forExpiredAccessToken() {
        String token = createToken(
                ACCESS_TOKEN_TYPE,
                new Date(System.currentTimeMillis() - 60_000));

        assertEquals(
                JwtProvider.TokenValidationResult.EXPIRED,
                jwtProvider.validateAccessTokenDetailed(token));
    }

    @Test
    void validateRefreshToken_returnsExpired_forExpiredRefreshToken() {
        String token = createToken(
                REFRESH_TOKEN_TYPE,
                new Date(System.currentTimeMillis() - 60_000));

        assertEquals(
                JwtProvider.TokenValidationResult.EXPIRED,
                jwtProvider.validateRefreshTokenDetailed(token));
    }

    @Test
    void validateAccessToken_returnsInvalid_forInvalidSignature() {
        String token = createTokenWithDifferentKey(ACCESS_TOKEN_TYPE);

        assertEquals(
                JwtProvider.TokenValidationResult.INVALID,
                jwtProvider.validateAccessTokenDetailed(token));
    }

    @Test
    void validateAccessToken_returnsInvalidType_forTokenWithoutTypeClaim() {
        String token = createTokenWithoutType(
                new Date(System.currentTimeMillis() + 60_000));

        assertEquals(
                JwtProvider.TokenValidationResult.INVALID_TYPE,
                jwtProvider.validateAccessTokenDetailed(token));
    }

    @Test
    void validateAccessToken_returnsInvalid_forBlankToken() {
        assertEquals(
                JwtProvider.TokenValidationResult.INVALID,
                jwtProvider.validateAccessTokenDetailed(" "));
    }

    private String createToken(String tokenType, Date expiration) {
        Date now = new Date();

        return Jwts.builder()
                .subject("test-user")
                .claim("userId", 1L)
                .claim("role", "ROLE_USER")
                .claim(TOKEN_TYPE_CLAIM, tokenType)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    private String createTokenWithoutType(Date expiration) {
        Date now = new Date();

        return Jwts.builder()
                .subject("test-user")
                .claim("userId", 1L)
                .claim("role", "ROLE_USER")
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    private String createTokenWithDifferentKey(String tokenType) {
        byte[] otherKeyBytes = new byte[32];
        for (int i = 0; i < otherKeyBytes.length; i++) {
            otherKeyBytes[i] = (byte) (i + 2);
        }

        SecretKey otherKey = Keys.hmacShaKeyFor(otherKeyBytes);
        Date now = new Date();

        return Jwts.builder()
                .subject("test-user")
                .claim("userId", 1L)
                .claim("role", "ROLE_USER")
                .claim(TOKEN_TYPE_CLAIM, tokenType)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + 60_000))
                .signWith(otherKey)
                .compact();
    }
}
