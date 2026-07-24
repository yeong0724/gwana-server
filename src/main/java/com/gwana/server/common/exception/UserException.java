package com.gwana.server.common.exception;

import com.gwana.server.common.enums.ErrorCode;

public class UserException extends CommonException {

    public UserException(ErrorCode errorCode) {
        super(errorCode);
    }

    public static class UserAlreadyExistException extends UserException {
        public UserAlreadyExistException() {
            super(ErrorCode.USER_ALREADY_EXIST);
        }
    }

    public static class UserCreateException extends UserException {
        public UserCreateException() {
            super(ErrorCode.USER_CREATE_FAILED);
        }
    }

    public static class UserNotExistException extends UserException {
        public UserNotExistException() {
            super(ErrorCode.USER_DOES_NOT_EXIST);
        }
    }

    /** 카카오가 이메일을 제공하지 않음(미동의). 재동의 후 재시도 안내 */
    public static class KakaoEmailRequiredException extends UserException {
        public KakaoEmailRequiredException() {
            super(ErrorCode.KAKAO_EMAIL_REQUIRED);
        }
    }

    /** 동일 이메일 계정이 이미 존재 → 기존 계정으로 로그인 유도 */
    public static class EmailAlreadyRegisteredException extends UserException {
        public EmailAlreadyRegisteredException() {
            super(ErrorCode.EMAIL_ALREADY_REGISTERED);
        }
    }
}
