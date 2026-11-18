package com.pickit.backend.chat.repository;

import com.pickit.backend.chat.entity.ChatRoom;
import com.pickit.backend.post.entity.ExchangePost;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByPostAndBuyerId(ExchangePost post, Long buyerId);
}
