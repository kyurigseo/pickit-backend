package com.pickit.backend.chat.controller;

import com.pickit.backend.chat.dto.request.CreateChatRoomRequest;
import com.pickit.backend.chat.dto.response.CreateChatRoomResponse;
import com.pickit.backend.chat.service.ChatRoomService;
import com.pickit.backend.common.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat-rooms")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    public ChatRoomController(ChatRoomService chatRoomService) {
        this.chatRoomService = chatRoomService;
    }

    @PostMapping
    public ApiResponse<CreateChatRoomResponse> createRoom(
            @Valid @RequestBody CreateChatRoomRequest request,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(chatRoomService.createRoom(userId, request.postId()));
    }
}
