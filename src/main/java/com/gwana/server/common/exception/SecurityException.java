package com.gwana.server.common.exception;

import com.gwana.server.common.enums.ErrorCode;

public class SecurityException extends CommonException {
    public SecurityException(ErrorCode errorCode) {
        super(errorCode);
    }

    public static class PasswordEncryptionException extends SecurityException {
        public PasswordEncryptionException() {
            super(ErrorCode.PASSWORD_ENCRYPTION_FAILED);
        }
    }
}
