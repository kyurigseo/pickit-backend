package com.pickit.backend.config;

import com.pickit.backend.auth.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        // 배포 확인용 헬스체크와, 400·500 오류 응답을 만드는 /error는 로그인 없이 연다.
                        .requestMatchers("/api/health", "/error").permitAll()
                        // WebSocket 핸드셰이크는 브라우저가 헤더를 못 붙이므로 열어두고, 토큰은 STOMP CONNECT에서 검증한다.
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/api/auth/logout").authenticated()
                        .requestMatchers("/api/auth/**").permitAll()
                        // 팝업 조회는 비로그인도 가능 (찜·관심사·위치 인증은 로그인 필요)
                        .requestMatchers(HttpMethod.GET, "/api/popups", "/api/popups/*", "/api/categories",
                                "/images/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
