package com.gwana.server.dto.token;

import lombok.Data;

@Data
public class RefreshTokenRequest {
    String accessToken;
}
