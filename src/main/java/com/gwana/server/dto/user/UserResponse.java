package com.gwana.server.dto.user;

import com.gwana.server.common.enums.Role;
import com.gwana.server.dto.BaseDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserResponse extends BaseDto {
    private String userId;

    private String username;

    private String email;

    private String phone;

    private Role role;
}