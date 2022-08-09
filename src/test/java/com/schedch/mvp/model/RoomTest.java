package com.schedch.mvp.model;

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
        roomDates.add(new RoomDate(LocalDate.of(2022, 06, 02)));
        roomDates.add(new RoomDate(LocalDate.of(2022, 06, 03)));

        LocalTime startTime = LocalTime.of(16, 00);
        LocalTime endTime   = LocalTime.of(23, 00);

        //when
        Room room = new Room(title, roomDates, startTime, endTime);

        //then
        assertThat(room.getUuid()).isNotNull();
    }
}