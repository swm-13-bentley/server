package com.schedch.mvp.mapper;

import com.schedch.mvp.dto.RoomRequest;
import com.schedch.mvp.dto.RoomResponse;
import com.schedch.mvp.model.Room;
import com.schedch.mvp.model.RoomDate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RoomMapper{
    RoomMapper INSTANCE = Mappers.getMapper(RoomMapper.class);

    @Mapping(target = "startTime", expression = "java(startStr2lt(roomReq.getStartTime()))")
    @Mapping(target = "endTime", expression = "java(endStr2lt(roomReq.getEndTime()))")
    @Mapping(target = "roomDates", expression = "java(ldList2RdList(roomReq.getDates()))")
    Room req2Entity(RoomRequest roomReq);

    @Mapping(target = "startTime", expression = "java(lt2int(room.getStartTime(), 30))")
    @Mapping(target = "endTime", expression = "java(lt2int(room.getEndTime(), 30))")
    @Mapping(target = "dates", expression = "java(rdList2LdList(room.getRoomDates()))")
    RoomResponse entity2Res(Room room);

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

    /**
     * String -> LocalTime
     * @param timeStr
     * @return LocalTime
     */
    default LocalTime startStr2lt(String timeStr) {
        return LocalTime.parse(timeStr, DateTimeFormatter.ISO_LOCAL_TIME);
    }

    /**
     * String -> LocalTime, with minute minus 1
     * @param timeStr
     * @return LocalTime
     */
    default LocalTime endStr2lt(String timeStr) {
        if(timeStr.equals("24:00:00")) {
            return LocalTime.of(23, 59, 0);
        } else {
            return LocalTime.parse(timeStr, DateTimeFormatter.ISO_LOCAL_TIME)
                    .minusMinutes(1);
        }
    }

    /**
     * LocalTime을 정해진 규격에 맞는 int로 변환
     * ex1) lt = 23:00:00, unit = 30분 -> 46
     * ex2) lt = 23:20:00, unit = 20분 -> 70
     * @param lt: LocalTime
     * @param unit: 자르는 단위
     * @return
     */
    default int lt2int(LocalTime lt, int unit) {
        return (int) (
                lt.getHour() * (60 / unit)
                        + Math.floor(lt.getMinute() / unit)
        );
    }
}
