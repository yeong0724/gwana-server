package com.gwana.server.dto.user;

import com.gwana.server.common.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SocialUser {
    private String userId;

    private String username;

    private String password;

    private String email;

    private String phone;

    private Role role;

    private String socialAccountId;

    private String provider;
}
