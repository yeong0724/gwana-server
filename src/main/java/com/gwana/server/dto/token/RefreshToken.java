package com.gwana.server.dto.token;

import com.gwana.server.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * Refresh Token 저장 엔티티.
 * <p>
 * Access Token 은 무상태(Stateless) 검증하므로 DB 에 저장하지 않는다.
 * Refresh Token 은 원문 대신 SHA-256 해시({@link #tokenHash})만 저장하여
 * DB 유출 시에도 토큰 자체가 노출되지 않도록 한다. 사용자당 1행(user_id UNIQUE)을 유지하며
 * 재발급(회전) 시 해시를 교체한다.
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RefreshToken extends BaseDto {
    private String refreshTokenId;

    private String userId;

    /** Refresh Token 원문의 SHA-256 hex 해시 */
    private String tokenHash;

    private LocalDateTime expiresAt;
}
