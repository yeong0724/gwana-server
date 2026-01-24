package com.gwana.server.dto.user;

import com.gwana.server.common.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    // 로그인 정보
    private String accessToken;
    private String provider;

    // 계정 정보
    private String customerKey;
    private String email;
    private String username;
    private String phone;
    private String profileImage;
    private String zonecode;
    private String roadAddress;
    private String detailAddress;
    private Role role;
}
