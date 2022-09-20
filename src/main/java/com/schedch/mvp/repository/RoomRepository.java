package com.schedch.mvp.repository;

import com.schedch.mvp.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByUuid(String roomUuid);

    @Query("select distinct r from Room r" +
            " join fetch r.roomDates" +
            " where r.id in :idList")
    List<Room> findAllInIdListJoinFetchRoomDates(@Param("idList") List<Long> idList);

    @Query("select distinct r from Room r" +
            " join fetch r.participantList" +
            " where r.id in :idList")
    List<Room> findAllInIdListJoinFetchParticipantList(@Param("idList") List<Long> idList);
}
