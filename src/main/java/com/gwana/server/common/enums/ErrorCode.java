package com.gwana.server.common.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    DEFAULT_ERROR("1000", "알 수 없는 에러가 발생했습니다."),
    PASSWORD_ENCRYPTION_FAILED("1001", "비밀번호 암호화 중 에러가 발생했습니다."),
    USER_ALREADY_EXIST("2000", "사용자가 이미 존재합니다."),
    USER_DOES_NOT_EXIST("2001", "사용자가 존재하지 않습니다."),
    USER_CREATE_FAILED("2002", "사용자 생성에 실패하였습니다."),
    AUTHENTICATION_FAILED("4001", "인증에 실패했습니다. 이메일 또는 비밀번호를 확인해주세요."),
    FORBIDDEN_ERROR("4002", "접근 권한이 없습니다"),
    UNAUTHORIZED_ERROR("4003", "인증이 유효하지 않습니다. 로그인해주세요."),
    INVALID_TOKEN("4004", "유효한 토큰이 아닙니다. 로그인해주세요."),
    EXPIRED_TOKEN_ERROR("4005", "로그인이 만료되었습니다. 다시 로그인해주세요."),
    ACCESS_DENIED("3000", "해당 기능에 접근이 제한됩니다.");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return this.code;
    }
}
