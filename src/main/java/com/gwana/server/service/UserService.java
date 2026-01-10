package com.gwana.server.service;

import com.gwana.server.common.enums.Role;
import com.gwana.server.common.exception.UserException;
import com.gwana.server.dto.socialAccount.SocialAccountRequest;
import com.gwana.server.dto.socialAccount.SocialAccountResponse;
import com.gwana.server.dto.user.SocialUser;
import com.gwana.server.dto.user.UserDto;
import com.gwana.server.dto.user.UserSignupRequest;
import com.gwana.server.mapper.UserMapper;
import io.hypersistence.tsid.TSID;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserService {
	private final UserMapper userMapper;

	public UserService(UserMapper userMapper) {
		this.userMapper = userMapper;
	}

	@Transactional
	public UserDto createUser(UserSignupRequest userSignupRequest) {
		if (userMapper.findUserByEmail(userSignupRequest.getEmail()).isPresent()) {
			throw new UserException.UserAlreadyExistException();
		}

		UserDto user = RequestToDto(userSignupRequest);
		int result =  userMapper.createUser(user);

		if (result <= 0) {
			throw new UserException.UserCreateException();
		}

		return user;
	}

	@Transactional
	public UserDto createUserByKakao(UserSignupRequest userSignupRequest) {
		return userMapper.findUserByEmail(userSignupRequest.getEmail())
				.orElseGet(() -> {
					UserDto user = RequestToDto(userSignupRequest);
					int result = userMapper.createUser(user);

					if (result <= 0) {
						throw new UserException.UserCreateException();
					}

					return user;
				});
	}

	@Transactional
	public void createKakaoIfNoAccountInfo(SocialAccountRequest socialAccountRequest) {
		Long providerId = socialAccountRequest.getProviderId();
		String provider = socialAccountRequest.getProvider();

		Optional<SocialAccountResponse> socialAccount = userMapper.findSocialAccountByProviderId(providerId, provider);

		if (socialAccount.isEmpty()) {
			int result = userMapper.createSocialAccount(socialAccountRequest);

			if (result <= 0) throw new UserException.UserCreateException();
		}
	}

	@Transactional
	public UserDto findUserByEmail(String email) {
		return userMapper.findUserByEmail(email)
				.orElseThrow(UserException.UserNotExistException::new);
	}

	@Transactional
	public SocialUser findUserByUserId(String userId) {
		return userMapper.findUserByUserId(userId)
				.orElseThrow(UserException.UserNotExistException::new);
	}

	private UserDto RequestToDto(UserSignupRequest userSignupRequest) {
		return UserDto.builder()
				.userId(TSID.Factory.getTsid().toString())
				.username(userSignupRequest.getUsername())
				.password(userSignupRequest.getPassword())
				.email(userSignupRequest.getEmail())
				.phone(userSignupRequest.getPhone())
				.role(Role.GENERAL)
				.build();
	}
}
