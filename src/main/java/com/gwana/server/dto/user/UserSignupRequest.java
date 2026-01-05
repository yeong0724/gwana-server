package com.gwana.server.dto.user;

import com.gwana.server.common.annotation.PasswordEncryption;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class UserSignupRequest {
    private final String username;

    @PasswordEncryption
    private final String password;

    private final String email;

    private final String phone;
}
