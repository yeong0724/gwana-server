package com.gwana.server.common.exception;

import com.gwana.server.common.enums.ErrorCode;
import lombok.Getter;

/**
 * PASSWORD_ENCRYPTION_FAILED
 *
 */
@Getter
public class CommonException extends RuntimeException {
    private final ErrorCode errorCode;

    public CommonException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
