package com.gwana.server.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI(Swagger) 문서 설정.
 * - Swagger UI:  /swagger-ui.html
 * - OpenAPI JSON: /v3/api-docs
 *
 * 인증이 필요한 API 는 컨트롤러/메서드에 @SecurityRequirement(name = "bearerAuth") 를 붙여 표시하고,
 * Swagger UI 의 "Authorize" 버튼으로 Access Token 을 넣어 호출한다.
 */
@Configuration
public class OpenApiConfig {

    public static final String BEARER_AUTH = "bearerAuth";

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI gwanaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Gwana Commerce API")
                        .description("관아수제차 커머스 백엔드 API 문서")
                        .version("v1"))
                .servers(List.of(new Server().url("http://localhost:" + serverPort).description("Local")))
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("발급받은 Access Token 을 입력한다. (Authorization: Bearer {token})")));
    }
}
