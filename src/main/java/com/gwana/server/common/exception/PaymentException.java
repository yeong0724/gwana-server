package com.gwana.server.common.exception;

import lombok.Getter;

@Getter
public class PaymentException extends RuntimeException {
    private final String code;
    private final String message;

    public PaymentException(String code, String message) {
        this.code = code;
        this.message = message;
    }
}


