package com.gwana.server.common.exception;

import static com.gwana.server.common.enums.ErrorCode.*;

import com.gwana.server.common.enums.ErrorCode;
import com.gwana.server.common.utils.ApiResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientResponseException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice {
    @ExceptionHandler(CommonException.class)
    protected ApiResponse<?> handleCommonException(CommonException commonException) {
        ErrorCode errorCode = commonException.getErrorCode();
        return ApiResponse.fail(errorCode);
    }

    @ExceptionHandler(BadCredentialsException.class)
    protected ApiResponse<?> handleBadCredentialsException() {
        return ApiResponse.fail(AUTHENTICATION_FAILED);
    }

    @ExceptionHandler(MalformedJwtException.class)
    protected ResponseEntity<?> handleMalformedJwtException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.fail(INVALID_TOKEN));
    }

    @ExceptionHandler(ExpiredJwtException.class)
    protected ResponseEntity<?> handleExpiredJwtException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.fail(EXPIRED_TOKEN_ERROR));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    protected ResponseEntity<?> handleUsernameNotFoundException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.fail(USER_DOES_NOT_EXIST));
    }

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<?> handleRuntimeException(RuntimeException runtimeException) {
        log.error("RuntimeException :", runtimeException);
        return ResponseEntity.status(500).body(ApiResponse.fail(DEFAULT_ERROR));
    }

    @ExceptionHandler(MyBatisSystemException.class)
    public ResponseEntity<?> handleMyBatisException(MyBatisSystemException e) {
        log.error("MyBatisSystemException :", e);
        return ResponseEntity.status(500).body(ApiResponse.fail(DEFAULT_ERROR));
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<?> handleMyBatisException(PaymentException paymentException) {
        String code = paymentException.getCode();
        String message = paymentException.getMessage();
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.generalFail(code, message));
    }
}
