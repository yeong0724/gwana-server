package com.gwana.server.common.exception;

import com.gwana.server.common.enums.ErrorCode;

public class TokenException extends CommonException {

    public TokenException(ErrorCode errorCode) {
        super(errorCode);
    }

    public static class TokenInvalidException extends TokenException {
        public TokenInvalidException() {
            super(ErrorCode.INVALID_TOKEN);
        }
    }

    public static class TokenExpiredException extends TokenException {
        public TokenExpiredException() {
            super(ErrorCode.EXPIRED_TOKEN_ERROR);
        }
    }
}
