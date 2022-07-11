package com.schedch.mvp.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RoomTest {

    @Test
    public void 방_생성() throws Exception {
        //given
        String title = "testTitle";
        List<RoomDate> roomDates = new ArrayList<>();
        LocalDate testDate1 = LocalDate.of(2022, 06, 01);
        LocalDate testDate2 = LocalDate.of(2022, 06, 01);
        LocalDate testDate3 = LocalDate.of(2022, 06, 01);
        RoomDate roomDate1 = new RoomDate(testDate1);
        RoomDate roomDate2 = new RoomDate(testDate2);
        RoomDate roomDate3 = new RoomDate(testDate3);
        roomDates.add(roomDate1);
        roomDates.add(roomDate2);
        roomDates.add(roomDate3);

        LocalTime startTime = LocalTime.of(16, 00);
        LocalTime endTime   = LocalTime.of(23, 00);

        //when
        Room room = new Room(title, roomDates, startTime, endTime);

        //then
        assertThat(room.getUuid()).isNotNull();
    }

}