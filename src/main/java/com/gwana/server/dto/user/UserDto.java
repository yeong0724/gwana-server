package com.gwana.server.dto.user;

import com.gwana.server.common.enums.Role;
import com.gwana.server.dto.BaseDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true) // BaseDto 필드도 equals / hashCode 에 포함
public class UserDto extends BaseDto {
	private String userId;

	private String customerKey;

	private String username;

	private String password;

	private String email;

	private String phone;

	private Role role;
}