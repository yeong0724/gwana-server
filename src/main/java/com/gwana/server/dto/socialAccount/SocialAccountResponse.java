package com.gwana.server.dto.socialAccount;

import lombok.Data;

@Data
public class SocialAccountResponse {
    String socialAccountId;

    String userId;

    String providerId;

    String provider;
}
