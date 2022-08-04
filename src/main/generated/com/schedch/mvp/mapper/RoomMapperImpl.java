package com.schedch.mvp.mapper;

import com.schedch.mvp.dto.RoomRequest;
import com.schedch.mvp.dto.RoomResponse;
import com.schedch.mvp.model.Room;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-08-01T12:58:37+0900",
    comments = "version: 1.5.1.Final, compiler: javac, environment: Java 11.0.14 (Oracle Corporation)"
)
@Component
public class RoomMapperImpl implements RoomMapper {

    @Override
    public Room req2Entity(RoomRequest roomReq) {
        if ( roomReq == null ) {
            return null;
        }

        Room.RoomBuilder room = Room.builder();

        room.title( roomReq.getTitle() );

        room.startTime( startStr2lt(roomReq.getStartTime()) );
        room.endTime( endStr2lt(roomReq.getEndTime()) );
        room.roomDates( ldList2RdList(roomReq.getDates()) );

        return room.build();
    }

    @Override
    public RoomResponse entity2Res(Room room) {
        if ( room == null ) {
            return null;
        }

        RoomResponse roomResponse = new RoomResponse();

        roomResponse.setTitle( room.getTitle() );

        roomResponse.setStartTime( lt2int(room.getStartTime(), 30) );
        roomResponse.setEndTime( lt2int(room.getEndTime(), 30) );
        roomResponse.setDates( rdList2LdList(room.getRoomDates()) );

        return roomResponse;
    }
}
