
package com.seos.pmis.auth.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtProviderTest {

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
    void validateToken_returnsTrue_forValidToken() {
        String token = createToken(
                new Date(System.currentTimeMillis() + 60_000)
        );

        assertTrue(jwtProvider.validateToken(token));
    }

    @Test
    void validateToken_returnsFalse_forExpiredToken() {
        String token = createToken(
                new Date(System.currentTimeMillis() - 60_000)
        );

        assertFalse(jwtProvider.validateToken(token));
    }

    @Test
    void validateToken_returnsFalse_forInvalidSignature() {
        String token = createTokenWithDifferentKey();

        assertFalse(jwtProvider.validateToken(token));
    }

    private String createToken(Date expiration) {
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

    private String createTokenWithDifferentKey() {
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
                .issuedAt(now)
                .expiration(new Date(now.getTime() + 60_000))
                .signWith(otherKey)
                .compact();
    }
}
