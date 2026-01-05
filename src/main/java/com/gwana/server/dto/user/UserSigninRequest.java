package com.gwana.server.dto.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserSigninRequest {
    private final String email;

    private final String password;
}
