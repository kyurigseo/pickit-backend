package com.pickit.backend.chat.websocket;

import com.pickit.backend.auth.util.JwtProvider;
import java.util.Collections;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * STOMP CONNECT 프레임의 Authorization 헤더(Bearer 액세스 토큰)를 검증한다.
 * 검증에 성공하면 세션의 사용자(Principal)를 로그인 유저 ID로 설정하고, 실패하면 연결을 거부한다.
 */
@Component
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtProvider jwtProvider;

    public StompAuthChannelInterceptor(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new MessageDeliveryException("인증 토큰이 필요합니다.");
            }
            String token = authHeader.substring(7);
            if (!jwtProvider.validateToken(token)) {
                throw new MessageDeliveryException("유효하지 않은 토큰입니다.");
            }
            Long userId = jwtProvider.getUserIdFromToken(token);
            accessor.setUser(new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList()));
        }
        return message;
    }
}
