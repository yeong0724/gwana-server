package com.gwana.server.service;

import com.gwana.server.common.exception.TokenException;
import com.gwana.server.dto.token.RefreshToken;
import com.gwana.server.dto.token.TokenResponse;
import com.gwana.server.mapper.RefreshTokenMapper;

import io.hypersistence.tsid.TSID;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HexFormat;

/**
 * JWT 발급/검증 및 Refresh Token 관리.
 * <p>
 * - Access Token : 무상태(서명 검증)로만 인증. DB 에 저장하지 않는다.
 * - Refresh Token : 원문 대신 SHA-256 해시를 user_id 기준 1행으로 저장하고, 재발급 시 회전(rotation)한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
    private static final String CLAIM_USER_ID = "userId";

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expire.access-token}")
    private int accessTokenExpireMinutes;

    @Value("${jwt.expire.refresh-token}")
    private int refreshTokenExpireMinutes;

    private final RefreshTokenMapper refreshTokenMapper;

    /**
     * Access/Refresh Token 을 발급하고 Refresh 해시를 저장(회전)한다.
     */
    @Transactional
    public TokenResponse issueTokens(String userId) {
        String accessToken = createToken(userId, accessTokenExpireMinutes);
        String refreshToken = createToken(userId, refreshTokenExpireMinutes);

        RefreshToken row = RefreshToken.builder()
                .refreshTokenId(TSID.Factory.getTsid().toString())
                .userId(userId)
                .tokenHash(sha256(refreshToken))
                .expiresAt(LocalDateTime.now().plusMinutes(refreshTokenExpireMinutes))
                .build();
        refreshTokenMapper.upsert(row);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(userId)
                .build();
    }

    /**
     * Refresh Token(쿠키) 으로 Access/Refresh 를 재발급한다.
     * 서명·만료·저장된 해시 일치를 모두 검증하며, 성공 시 Refresh 를 회전한다.
     */
    @Transactional
    public TokenResponse reissue(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new TokenException.TokenInvalidException();
        }

        String userId = parseUserId(refreshToken);

        RefreshToken stored = refreshTokenMapper.findByUserId(userId)
                .orElseThrow(TokenException.TokenInvalidException::new);

        if (stored.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenMapper.deleteByUserId(userId);
            throw new TokenException.TokenInvalidException();
        }

        // 저장된 해시와 불일치 → 탈취/재사용 의심. 해당 사용자의 세션을 무효화한다.
        if (!MessageDigest.isEqual(
                stored.getTokenHash().getBytes(StandardCharsets.UTF_8),
                sha256(refreshToken).getBytes(StandardCharsets.UTF_8))) {
            refreshTokenMapper.deleteByUserId(userId);
            throw new TokenException.TokenInvalidException();
        }

        return issueTokens(userId);
    }

    @Transactional
    public void deleteRefreshToken(String userId) {
        refreshTokenMapper.deleteByUserId(userId);
    }

    /**
     * Access Token 서명/만료 검증. 유효하면 true, 그렇지 않으면 JWT 예외를 던진다.
     * (JwtAuthenticationFilter 가 Malformed/Expired 예외를 처리하도록 예외를 전파한다.)
     */
    public Boolean validateToken(String token) {
        Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
        return true;
    }

    /**
     * 인증 필터에서 사용. Access Token 의 userId 클레임을 추출한다(만료 예외는 상위로 전파).
     */
    public String parseUserId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            String userId = claims.get(CLAIM_USER_ID, String.class);
            if (userId == null || userId.isBlank()) {
                throw new TokenException.TokenInvalidException();
            }
            return userId;
        } catch (JwtException jwtException) {
            throw new TokenException.TokenInvalidException();
        }
    }

    private String createToken(String userId, int expireMinutes) {
        Instant now = Instant.now();
        return Jwts.builder()
                .claim(CLAIM_USER_ID, userId)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(Duration.ofMinutes(expireMinutes))))
                .signWith(getSigningKey())
                .compact();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 미지원 환경", e);
        }
    }
}
