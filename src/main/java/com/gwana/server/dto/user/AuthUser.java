package com.gwana.server.dto.user;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class AuthUser extends User {
    private final String userId;
    private final String email;
    private final String phone;

    public AuthUser(
            String userId,
            String username,
            String password,
            String email,
            String phone,
            Collection<? extends GrantedAuthority> authorities
    ) {
        super(username, password, authorities);
        this.userId = userId;
        this.email = email;
        this.phone = phone;
    }

}