package com.gwana.server.common.config;

import com.gwana.server.common.exception.CustomAccessDeniedHandler;
import com.gwana.server.common.exception.CustomAuthenticationEntryPoint;
import com.gwana.server.common.security.CustomUserDetailsService;
import com.gwana.server.common.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService customUserDetailsService;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Value("${app.base-url}")
    private String appBaseUrl;

    public WebSecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            CustomUserDetailsService customUserDetailsService,
            CustomAccessDeniedHandler customAccessDeniedHandler,
            CustomAuthenticationEntryPoint customAuthenticationEntryPoint
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customUserDetailsService = customUserDetailsService;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
    }

    /**
     * SecurityFilterChain 정의 (인증, 인가, 세션, 예외 처리, jwtFilter 설정)
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화 (REST API 서버에서 주로 사용)
                .cors(cors -> cors.configurationSource(this.corsConfigurationSource())) // CORS 설정 활성화
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .userDetailsService(customUserDetailsService)
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(customAccessDeniedHandler)
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // jwt 기반 인증이므로 Session 사용 안함
                )
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                            "/",
                            "/error",
                            // 인증 불필요: 로그인/토큰재발급/카카오 SSO 로그아웃 리다이렉트
                            "/auth/kakao/login",
                            "/auth/token/refresh",
                            "/auth/oauth2/logout/kakao",
                            "/user/**",
                            "/product/**",
                            "/mypage/search/review/list"
                    ).permitAll()
                    .requestMatchers("/auth/logout").authenticated() // 로그아웃은 인증 필요
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated() // 나머지 요청에 대해서는 인증 처리
                )
                .oauth2Login(oauth2 -> oauth2.failureUrl("/login?error=true"))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build(); // FilterChain 객체 반환
    }

    /**
     * CORS 설정
     */
    public CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration configuration = new CorsConfiguration();

            configuration.setAllowedHeaders(Collections.singletonList("*"));
            configuration.setAllowedMethods(Collections.singletonList("*"));

            // 명시적으로 허용할 Origin 지정
            configuration.setAllowedOrigins(Arrays.asList(
                    appBaseUrl,
                    "http://192.168.45.7:3000"
            ));

            configuration.setAllowCredentials(true);
            return configuration;
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
