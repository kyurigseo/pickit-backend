package com.pickit.backend.chat.repository;

import com.pickit.backend.chat.entity.ChatMessage;
import com.pickit.backend.chat.entity.ChatRoom;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Optional<ChatMessage> findFirstByRoomOrderByMessageIdDesc(ChatRoom room);
}
