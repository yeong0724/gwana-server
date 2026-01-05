package com.gwana.server.service;

import com.gwana.server.client.KakaoTokenHttpClient;
import com.gwana.server.client.KakaoUserHttpClient;
import com.gwana.server.common.exception.TokenException;
import com.gwana.server.dto.token.Token;
import com.gwana.server.dto.token.TokenResponse;
import com.gwana.server.dto.user.UserDto;
import com.gwana.server.dto.user.UserFromKakao;
import com.gwana.server.mapper.TokenMapper;

import io.hypersistence.tsid.TSID;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Service
public class TokenService {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expire.access-token}")
    private int accessTokenExpireHour;

    @Value("${jwt.expire.refresh-token}")
    private int refreshTokenExpireHour;

    private final TokenMapper tokenMapper;
    private final UserService userService;
    private final KakaoTokenHttpClient kakaoTokenHttpClient;
    private final KakaoUserHttpClient kakaoUserHttpClient;

    public TokenService(
            TokenMapper tokenMapper,
            UserService userService,
            KakaoTokenHttpClient kakaoTokenHttpClient,
            KakaoUserHttpClient kakaoUserHttpClient
    ) {
        this.tokenMapper = tokenMapper;
        this.userService = userService;
        this.kakaoTokenHttpClient = kakaoTokenHttpClient;
        this.kakaoUserHttpClient = kakaoUserHttpClient;
    }

    @Transactional
    public TokenResponse insertToken(String userId, String authAccessToken) {
        String accessToken = getToken(userId, "ACCESS");
        String refreshToken = getToken(userId, "REFRESH");

        LocalDateTime now = LocalDateTime.now();
        Token token = Token.builder()
                .tokenId(TSID.Factory.getTsid().toString())
                .userId(userId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .authAccessToken(authAccessToken)
                .accessTokenExpiresAt(getAccessTokenExpiredAt(now))
                .refreshTokenExpiresAt(getRefreshTokenExpiredAt(now))
                .build();

        Optional<Token> tokenOptional = tokenMapper.findTokenByUserId(userId);
        if (tokenOptional.isPresent()) {
            tokenMapper.deleteTokenByUserId(userId);
        }

        tokenMapper.createToken(token);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public UserDto findUserByAccessToken(String accessToken) {
        Claims claims = parseClaims(accessToken);
        Object userId = claims.get("userId");

        if (ObjectUtils.isEmpty(userId)) {
            throw new TokenException.TokenInvalidException();
        }

        return userService.findUserByUserId((String) userId);
    }

    public String getAccessTokenByCode(String code) {
        return kakaoTokenHttpClient.getAccessTokenByCode(code);
    }

    public UserFromKakao findUserFromKakao(String accessToken) {
        return kakaoUserHttpClient.findUserFromKakao(accessToken);
    }

    @Transactional
    public TokenResponse refreshToken(String accessToken, String refreshTokenFromCookie) {
        Token token = this.getTokenByAccessToken(accessToken);

        if (!token.getRefreshToken().equals(refreshTokenFromCookie)) {
            throw new TokenException.TokenInvalidException();
        }

        if (!validateToken(refreshTokenFromCookie)) {
            throw new TokenException.TokenInvalidException();
        }

        return reissueAccessToken(refreshTokenFromCookie, token.getAuthAccessToken());
    }

    @Transactional
    public Token getTokenByAccessToken(String accessToken) {
        return tokenMapper.findTokenByAccessToken(accessToken)
                .orElseThrow(TokenException.TokenInvalidException::new);
    }

    @Transactional
    public void deleteTokenByAccessToken(String accessToken) {
        tokenMapper.deleteTokenByAccessToken(accessToken);
    }

    private TokenResponse reissueAccessToken(String refreshToken, String authAccessToken) {
        Claims claims = parseClaims(refreshToken);
        String userId = (String) claims.get("userId");
        String newAccessToken = getToken(userId, "ACCESS");
        String newRefreshToken = getToken(userId, "REFRESH");

        tokenMapper.deleteTokenByUserId(userId);

        LocalDateTime now = LocalDateTime.now();
        Token newToken = Token.builder()
                .tokenId(TSID.Factory.getTsid().toString())
                .userId(userId)
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .authAccessToken(authAccessToken)
                .accessTokenExpiresAt(getAccessTokenExpiredAt(now))
                .refreshTokenExpiresAt(getRefreshTokenExpiredAt(now))
                .build();

        tokenMapper.createToken(newToken);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    private String getToken(String userId, String tokenType) {
        Date now = new Date();
        Instant instant = now.toInstant();

        int expireHour = Objects.equals(tokenType, "ACCESS") ? accessTokenExpireHour : refreshTokenExpireHour;

        return Jwts.builder()
                .claim("userId", userId)
                .issuedAt(now)
                .expiration(Date.from(instant.plus(Duration.ofMinutes(expireHour))))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * accessToken 유효하다면 true를 반환, 그렇지 않다면 Exception 발생
     */
    public Boolean validateToken(String accessToken) {
        Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(accessToken);
        return true;
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())  // setSigningKey -> verifyWith + SecretKey 사용
                    .build()
                    .parseSignedClaims(accessToken)  // parseClaimsJws -> parseSignedClaims
                    .getPayload();
        } catch (ExpiredJwtException expiredJwtException) {
            return expiredJwtException.getClaims();
        }
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private LocalDateTime getAccessTokenExpiredAt(LocalDateTime now) {
        return now.plusMinutes(accessTokenExpireHour);
    }

    private LocalDateTime getRefreshTokenExpiredAt(LocalDateTime now) {
        return now.plusMinutes(refreshTokenExpireHour);
    }
}
