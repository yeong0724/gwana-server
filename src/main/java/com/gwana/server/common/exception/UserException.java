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
}
