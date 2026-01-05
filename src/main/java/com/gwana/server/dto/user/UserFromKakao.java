package com.gwana.server.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserFromKakao {
    private final String username;

    private final String password;

    private final String email;

    private final String phone;

    private final String provider;

    private final Long providerId;
}
