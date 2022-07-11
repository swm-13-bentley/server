package com.schedch.mvp.service;

import com.schedch.mvp.dto.RoomRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RoomServiceTest {

    @Autowired RoomService roomService;

    @Test
    public void roomService_createRoom() throws Exception {
        //given
        String title = "test title";
        LocalDate date1 = LocalDate.of(2022, 04, 01);
        LocalDate date2 = LocalDate.of(2022, 04, 02);
        List<LocalDate> dates = Arrays.asList(date1, date2);
        LocalTime startTime = LocalTime.of(04, 30, 00);
        LocalTime endTime = LocalTime.of(20, 00, 00);

        RoomRequestDto roomRequestDto = RoomRequestDto.builder()
                .title(title)
                .dates(dates)
                .startTime(startTime)
                .endTime(endTime)
                .build();

        //when
        String roomUuid = roomService.save(roomRequestDto);

        //then
        assertThat(roomUuid).isNotNull();

    }
}