package com.schedch.mvp.model;

import com.schedch.mvp.dto.RoomRequestDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RoomTest {

    @Test
    public void 방_생성() throws Exception {
        //given
        String title = "testTitle";
        List<RoomDate> roomDates = new ArrayList<>();
        roomDates.add(new RoomDate(LocalDate.of(2022, 06, 01)));
        roomDates.add(new RoomDate(LocalDate.of(2022, 06, 01)));
        roomDates.add(new RoomDate(LocalDate.of(2022, 06, 01)));

        LocalTime startTime = LocalTime.of(16, 00);
        LocalTime endTime   = LocalTime.of(23, 00);

        //when
        Room room = new Room(title, roomDates, startTime, endTime);

        //then
        assertThat(room.getUuid()).isNotNull();
    }

    @Test
    public void create_room_from_dto() {
        //given
        String title = "testTitle";
        List<LocalDate> roomDates = new ArrayList<>();
        roomDates.add(LocalDate.of(2022, 06, 01));
        roomDates.add(LocalDate.of(2022, 06, 01));
        roomDates.add(LocalDate.of(2022, 06, 01));

        LocalTime startTime = LocalTime.of(16, 00);
        LocalTime endTime   = LocalTime.of(23, 00);

        //when
        RoomRequestDto dto = RoomRequestDto.builder()
                .title(title)
                .dates(roomDates)
                .startTime(startTime)
                .endTime(endTime)
                .build();


        //then
        try {
            Room room = new Room(dto);
        } catch (Exception e) {
            Assertions.fail();
        }

    }

}