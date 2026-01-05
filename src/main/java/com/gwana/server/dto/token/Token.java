package com.gwana.server.dto.token;

import com.gwana.server.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Token extends BaseDto {
    private String tokenId;

    private String userId;

    private String accessToken;

    private String refreshToken;

    private String authAccessToken;

    private LocalDateTime accessTokenExpiresAt;

    private LocalDateTime refreshTokenExpiresAt;
}
