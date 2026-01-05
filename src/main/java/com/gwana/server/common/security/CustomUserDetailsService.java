package com.gwana.server.common.security;

import com.gwana.server.common.exception.UserException;
import com.gwana.server.dto.user.AuthUser;
import com.gwana.server.dto.user.UserDto;
import com.gwana.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserService userService;

    @Override
    public AuthUser loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            UserDto user = userService.findUserByEmail(email);

            return new AuthUser(
                    user.getUserId(),
                    user.getUsername(),
                    user.getPassword(),
                    user.getEmail(),
                    user.getPhone(),
                    List.of(new SimpleGrantedAuthority(user.getRole().toString()))
            );
        } catch (UserException.UserNotExistException userException) {
            throw new UsernameNotFoundException(userException.getMessage());
        }
    }
}
