package com.seos.pmis.auth.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
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

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private SecretKey secretKey;

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
            String role
    ) {
        return generateToken(
                userId,
                username,
                role,
                accessTokenExpiration
        );
    }

    /**
     * Refresh Token 생성
     */
    public String generateRefreshToken(
            Long userId,
            String username,
            String role
    ) {
        return generateToken(
                userId,
                username,
                role,
                refreshTokenExpiration
        );
    }

    /**
     * JWT 생성
     */
    private String generateToken(
            Long userId,
            String username,
            String role,
            long expiration
    ) {

        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .claim("role", role)
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
     * username 조회
     */
    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * userId 조회
     */
    public Long getUserId(String token) {
        return getClaims(token).get("userId", Long.class);
    }

    /**
     * role 조회
     */
    public String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    /**
     * 토큰 검증
     */
    public boolean validateToken(String token) {

        try {

            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);

            return true;

        } catch (SecurityException e) {
            log.debug("Invalid JWT Signature", e);

        } catch (MalformedJwtException e) {
            log.debug("Malformed JWT", e);

        } catch (ExpiredJwtException e) {
            log.debug("Expired JWT", e);

        } catch (UnsupportedJwtException e) {
            log.debug("Unsupported JWT", e);

        } catch (IllegalArgumentException e) {
            log.debug("JWT claims string is empty", e);
        }

        return false;
    }
}