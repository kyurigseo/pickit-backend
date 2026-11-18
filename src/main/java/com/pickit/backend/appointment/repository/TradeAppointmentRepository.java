package com.pickit.backend.appointment.repository;

import com.pickit.backend.appointment.entity.TradeAppointment;
import com.pickit.backend.chat.entity.ChatRoom;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeAppointmentRepository extends JpaRepository<TradeAppointment, Long> {

    Optional<TradeAppointment> findByRoom(ChatRoom room);

    boolean existsByRoom(ChatRoom room);
}
