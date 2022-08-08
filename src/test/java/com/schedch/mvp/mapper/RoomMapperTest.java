package com.schedch.mvp.mapper;

import com.schedch.mvp.dto.room.RoomRequest;
import com.schedch.mvp.model.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RoomMapperTest {

    private RoomMapper roomMapper = RoomMapper.INSTANCE;
    private RoomRequest roomReq;

    @BeforeEach
    public void createRoomEntity() {
        String title = "방 제목입니다";
        List<LocalDate> dateList = List.of(LocalDate.of(2022, 7, 15),
                LocalDate.of(2022, 7, 16));
        String startTime = "12:00:00";
        String endTime = "23:00:00";

        roomReq = new RoomRequest(title, dateList, startTime, endTime);

    }

    @Test
    public void req2Entity_매핑_테스트() throws Exception {
        //given

        //when
        Room room = roomMapper.req2Entity(roomReq);

        //then
        assertThat(room.getUuid()).isNotNull();
        assertThat(room.getRoomDates().size()).isEqualTo(2);
        assertThat(room.getStartTime()).isEqualTo(LocalTime.of(12, 00));
        assertThat(room.getEndTime()).isEqualTo(LocalTime.of(22, 59));

    }

    @Test
    public void req2Entity_매핑_24시_테스트() throws Exception {
        //given
        roomReq.setEndTime("24:00:00");

        //when
        Room room = roomMapper.req2Entity(roomReq);

        //then
        assertThat(room.getEndTime()).isEqualTo(LocalTime.of(23, 59));

    }

    @Test
    public void entity2Res_매핑_테스트() throws Exception {
        //given


        //when

        //then

    }
}
