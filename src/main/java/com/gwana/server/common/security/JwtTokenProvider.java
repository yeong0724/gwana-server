package com.gwana.server.common.security;

import com.gwana.server.common.enums.Role;
import com.gwana.server.dto.user.AuthUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * SecurityContextHolder의 Context는 getAuthentication 메서드에서 UsernamePasswordAuthenticationToken 객체를 생성할때 세팅되는 값이다.
 *
 * >> new UsernamePasswordAuthenticationToken(principal, userId, authorities)
 * - principal: 사용자에 대한 정보
 * - userId: 사용자 PK
 * - authorities: ROLE(권한)
 */
@Component
public class JwtTokenProvider {
    public AuthUser getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증된 사용자가 아닌 경우
        if (authentication == null || authentication.getPrincipal().equals("anonymousUser")) {
            return null;
        }

        return (AuthUser) authentication.getPrincipal();
    }

    public Role getRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = authentication.getAuthorities().stream().findFirst().orElseThrow(RuntimeException::new).getAuthority();
        return Role.valueOf(role.replace("ROLE_", ""));
    }
}
