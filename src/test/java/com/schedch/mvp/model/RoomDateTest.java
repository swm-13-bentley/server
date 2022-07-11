package com.schedch.mvp.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RoomDateTest {
    @Test
    public void 방_날짜_생성_테스트() throws Exception {
        //given
        LocalDate testDate = LocalDate.of(2022, 06, 01);

        //when
        RoomDate roomDate = new RoomDate(testDate);

        //then
        assertThat(roomDate.getScheduledDate()).isEqualTo(testDate);
    }
}