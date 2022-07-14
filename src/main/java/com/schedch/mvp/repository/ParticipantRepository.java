package com.schedch.mvp.repository;

import com.schedch.mvp.model.Participant;
import com.schedch.mvp.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    Optional<Participant> findParticipantByParticipantNameAndRoom(String participantName, Room room);
}
