package com.schedch.mvp.dto;

import com.schedch.mvp.model.Participant;
import com.schedch.mvp.model.Schedule;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.*;

class ParticipantResponseDtoTest {

    @Test
    public void timeBlock_integration_test() throws Exception {
        //given
        Participant participant = new Participant("testName", "testPwd", false);
        Schedule schedule1 = new Schedule(LocalDate.of(2022, 4, 1),
                LocalTime.of(4, 30, 0),
                LocalTime.of(6, 30, 0));

        Schedule schedule2 = new Schedule(LocalDate.of(2022, 4, 1),
                LocalTime.of(20, 30, 0),
                LocalTime.of(22, 30, 0));

        Schedule schedule3 = new Schedule(LocalDate.of(2022, 4, 2),
                LocalTime.of(20, 30, 0),
                LocalTime.of(22, 30, 0));

        participant.addSchedule(schedule1);
        participant.addSchedule(schedule2);
        participant.addSchedule(schedule3);

        //when
        ParticipantResponseDto dto = new ParticipantResponseDto(participant);

        //then
        assertThat(dto.getTimeBlockDtoList().size()).isEqualTo(2);
        assertThat(dto.getTimeBlockDtoList().get(0).getAvailableDate())
                .isBefore(dto.getTimeBlockDtoList().get(1).getAvailableDate());

    }
}