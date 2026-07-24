package com.gwana.server.mapper;

import com.gwana.server.dto.socialAccount.SocialAccountRequest;
import com.gwana.server.dto.user.SocialUser;
import com.gwana.server.dto.user.UserDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface UserMapper {
	int createUser(UserDto userSignupRequest);

	Optional<SocialUser> findUserByUserId(@Param("userId") String userId);

	Optional<UserDto> findUserByEmail(@Param("email") String email);

	/** (provider, providerId) 로 연결된 사용자 조회 — 소셜 로그인 계정 식별의 기준 */
	Optional<SocialUser> findUserByProvider(@Param("provider") String provider,
											@Param("providerId") Long providerId);

	int createSocialAccount(SocialAccountRequest socialAccountRequest);

	/** 로그아웃/연동해제용 소셜 access token 갱신 */
	void updateSocialAccessToken(@Param("socialAccountId") String socialAccountId,
								 @Param("accessToken") String accessToken);

	/** 로그아웃 시 사용할 소셜 access token 조회 (없으면 null) */
	String findSocialAccessToken(@Param("userId") String userId,
								 @Param("provider") String provider);
}
