package com.pickit.backend.verification.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 로그인 연동 전 개발 테스트용 필터. popup-list-demo 프로필에서만 켜진다.
 * X-Dev-User-Id 헤더의 회원 ID를 로그인 사용자로 넣어 실제 API 경로를 그대로 호출하게 한다.
 * 로그인 기능이 연결되면 이 필터를 삭제한다. 운영 환경에서 이 프로필을 켜면 안 된다.
 */
@Profile("popup-list-demo")
@Component
public class DevUserFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-Dev-User-Id";

    private static final Logger log = LoggerFactory.getLogger(DevUserFilter.class);

    public DevUserFilter() {
        log.warn("개발용 {} 로그인 필터가 켜져 있습니다. 운영 환경에서는 popup-list-demo 프로필을 끄세요.", HEADER);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader(HEADER);
        if (header == null || header.isBlank()) {
            chain.doFilter(request, response);
            return;
        }
        long userId;
        try {
            userId = Long.parseLong(header.strip());
        } catch (NumberFormatException exception) {
            userId = 0;
        }
        if (userId <= 0) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, HEADER + "는 양의 정수여야 합니다.");
            return;
        }
        Principal principal = new DevUserPrincipal(userId);
        chain.doFilter(new HttpServletRequestWrapper(request) {
            @Override
            public Principal getUserPrincipal() {
                return principal;
            }
        }, response);
    }

    private record DevUserPrincipal(Long userId) implements VerificationPrincipal {

        @Override
        public Long getUserId() {
            return userId;
        }

        @Override
        public String getName() {
            return "dev-user-" + userId;
        }
    }
}
