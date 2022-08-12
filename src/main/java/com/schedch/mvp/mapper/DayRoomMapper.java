package com.schedch.mvp.mapper;

import com.schedch.mvp.dto.room.DayRoomReq;
import com.schedch.mvp.dto.room.DayRoomRes;
import com.schedch.mvp.model.Participant;
import com.schedch.mvp.model.Room;
import com.schedch.mvp.model.RoomDate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface DayRoomMapper {
    DayRoomMapper INSTANCE = Mappers.getMapper(DayRoomMapper.class);

    @Mapping(target = "roomDates", expression = "java(ldList2RdList(dayRoomReq.getDates()))")
    Room req2Entity(DayRoomReq dayRoomReq);

    @Mapping(target = "dates", expression = "java(rdList2LdList(room.getRoomDates()))")
    @Mapping(target = "participants", expression = "java(getAllParticipants(room))")
    DayRoomRes entity2Res(Room room);

    /**
     * localDate List to RoomDate List
     * @param ldList
     * @return
     */
    default List<RoomDate> ldList2RdList(List<LocalDate> ldList) {
        return ldList.stream().map(RoomDate::new)
                .collect(Collectors.toList());
    }

    /**
     * RoomDate List to LocalDate List
     * @param rdList
     * @return
     */
    default List<LocalDate> rdList2LdList(List<RoomDate> rdList) {
        return rdList.stream().map(RoomDate::getScheduledDate)
                .collect(Collectors.toList());
    }

    default List<String> getAllParticipants(Room room) {
        return room.getParticipantList().stream().map(Participant::getParticipantName)
                .collect(Collectors.toList());
    }
}
