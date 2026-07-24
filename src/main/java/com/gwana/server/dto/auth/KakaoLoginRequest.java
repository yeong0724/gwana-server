package com.gwana.server.dto.auth;

/**
 * 카카오 로그인 요청 본문. 프론트가 카카오로부터 받은 인가 코드를 전달한다.
 */
public record KakaoLoginRequest(String code) {
}
