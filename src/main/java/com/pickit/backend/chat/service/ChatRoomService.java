package com.pickit.backend.chat.service;

import com.pickit.backend.chat.dto.response.CreateChatRoomResponse;
import com.pickit.backend.chat.entity.ChatRoom;
import com.pickit.backend.chat.repository.ChatRoomRepository;
import com.pickit.backend.post.entity.ExchangePost;
import com.pickit.backend.post.repository.ExchangePostRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ExchangePostRepository exchangePostRepository;

    public ChatRoomService(ChatRoomRepository chatRoomRepository, ExchangePostRepository exchangePostRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.exchangePostRepository = exchangePostRepository;
    }

    /** 교환글에서 채팅을 시작한다. 같은 글에 이미 만든 방이 있으면 그 방을 반환한다. */
    @Transactional
    public CreateChatRoomResponse createRoom(Long buyerId, Long postId) {
        ExchangePost post = exchangePostRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "교환글을 찾을 수 없습니다."));

        if (post.getUserId().equals(buyerId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "본인의 교환글에는 채팅방을 만들 수 없습니다.");
        }

        return chatRoomRepository.findByPostAndBuyerId(post, buyerId)
                .map(room -> new CreateChatRoomResponse(room.getRoomId(), false))
                .orElseGet(() -> {
                    try {
                        ChatRoom room = chatRoomRepository.saveAndFlush(new ChatRoom(post, post.getUserId(), buyerId));
                        return new CreateChatRoomResponse(room.getRoomId(), true);
                    } catch (DataIntegrityViolationException e) {
                        // 동시 요청으로 UK(post_id, buyer_id)에 걸린 경우 먼저 만들어진 방을 반환한다.
                        ChatRoom existing = chatRoomRepository.findByPostAndBuyerId(post, buyerId)
                                .orElseThrow(() -> e);
                        return new CreateChatRoomResponse(existing.getRoomId(), false);
                    }
                });
    }
}
