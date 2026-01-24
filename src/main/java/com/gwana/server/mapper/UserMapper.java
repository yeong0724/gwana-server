package com.gwana.server.mapper;

import com.gwana.server.dto.socialAccount.SocialAccountRequest;
import com.gwana.server.dto.socialAccount.SocialAccountResponse;
import com.gwana.server.dto.user.SocialUser;
import com.gwana.server.dto.user.UserDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface UserMapper {
	int createUser(UserDto userSignupRequest);

	Optional<SocialUser> findUserByUserId(String userId);

	Optional<UserDto> findUserByEmail(String email);

	Optional<SocialAccountResponse> findSocialAccountByProviderId(Long providerId, String provider);

	int createSocialAccount(SocialAccountRequest socialAccountRequest);
}
