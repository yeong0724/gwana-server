package com.gwana.server.dto.payment;

import lombok.Data;

@Data
public class TossErrorResponse {
    private String code;
    private String message;
}
