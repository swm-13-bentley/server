package com.schedch.mvp.repository;

import com.schedch.mvp.model.Participant;
import com.schedch.mvp.model.Room;
import com.schedch.mvp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    @Query("select distinct p from Participant p" +
            " left join fetch p.scheduleList" +
            " where p.room = :room" +
            " and p.participantName = :participantName")
    List<Participant> findParticipantByRoomAndParticipantNameWithSchedules(@Param("room") Room room,
                                                                           @Param("participantName") String participantName);

    List<Participant> findParticipantByRoomAndParticipantName(Room room, String participantName);

    @Query("select distinct p from Participant p" +
            " left join fetch p.scheduleList" +
            " where p.user = :user" +
            " and p.room = :room")
    Optional<Participant> findByUserAndRoom(@Param("user") User user,
                                            @Param("room") Room room);

    @Query("select distinct p from Participant p" +
            " join fetch p.room" +
            " where p.user.email = :userEmail")
    List<Participant> findAllByUserEmailJoinFetchRoom(@Param("userEmail") String userEmail);

    @Query("select distinct p from Participant p" +
            " join fetch p.scheduleList" +
            " where p.room = :room")
    List<Participant> findAllByRoom(@Param("room") Room room);

    @Query("select distinct p from Participant p" +
            " left join fetch p.scheduleList" +
            " where p.room = :room" +
            " order by p.participantName")
    List<Participant> findAllByRoomJoinFetchSchedules(@Param("room") Room room);

    List<Participant> findAllByIdIn(List<Long> idList);

}
