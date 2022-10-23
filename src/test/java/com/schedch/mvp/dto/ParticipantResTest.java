package com.schedch.mvp.dto;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.schedch.mvp.adapter.LocalDateAdapter;
import com.schedch.mvp.adapter.LocalTimeAdapter;
import com.schedch.mvp.dto.participant.ParticipantRes;
import com.schedch.mvp.model.Participant;
import com.schedch.mvp.model.Schedule;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.*;

class ParticipantResTest {
    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
            .create();
    @Test
    public void timeBlock_integration_test() throws Exception {
        //given
        Participant participant = getParticipant();

        //when
        ParticipantRes dto = new ParticipantRes(participant);

        //then
        assertThat(dto.getAvailable().size()).isEqualTo(2);
        assertThat(dto.getAvailable().get(0).getAvailableDate())
                .isBefore(dto.getAvailable().get(1).getAvailableDate());

    }

//    @Test
//    public void json_test() {
//        //given
//        Participant participant = getParticipant();
//        ParticipantResponseDto dto = new ParticipantResponseDto(participant);
//
//        //when
//        String jsonString = gson.toJson(dto.getTimeBlockDtoList().toString());
//
//        System.out.println("jsonString = " + jsonString);
//        Type type = new TypeToken<List<TimeBlockDto>>(){}.getType();
//        List<TimeBlockDto> mappedDto = gson.fromJson(jsonString, type);
//        System.out.println("mappedDto = " + mappedDto);
//
//        //then
//        assertThat(mappedDto.size()).isEqualTo(2);
//        assertThat(mappedDto.get(0).getAvailableDate())
//                .isBefore(mappedDto.get(1).getAvailableDate());
//    }

    private Participant getParticipant() {
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

        return participant;
    }


}