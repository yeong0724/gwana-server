package com.gwana.server.dto.auth;

import com.gwana.server.dto.token.TokenResponse;
import com.gwana.server.dto.user.SocialUser;

/**
 * 로그인 처리 결과(인증된 사용자 + 발급 토큰)를 컨트롤러로 전달하는 내부 모델.
 */
public record LoginResult(SocialUser user, TokenResponse tokens) {
}
