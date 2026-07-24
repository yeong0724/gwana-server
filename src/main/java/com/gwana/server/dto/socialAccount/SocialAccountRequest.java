package com.gwana.server.dto.socialAccount;

import com.gwana.server.dto.BaseDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SocialAccountRequest extends BaseDto {
    private String socialAccountId;

    private String userId;

    private Long providerId;

    private String provider;

    /** 소셜 provider access token (로그아웃/연동 해제용) */
    private String accessToken;
}
