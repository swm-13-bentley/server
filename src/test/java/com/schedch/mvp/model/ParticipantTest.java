package com.schedch.mvp.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class ParticipantTest {
    
    @Test
    public void create_participant_test() throws Exception {
        //given
        String participantName = "testName";
        String password = "testPwd";

        //when
        try {
            Participant participant = new Participant(participantName, password, false);
        }
        //then
        catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    @DisplayName("범위 내의 스케줄을 갖는 유저 테스트")
    public void inRangeTest() throws Exception {
        //given
        Participant participant1 = new Participant("username1", "", false);
        Schedule schedule1 = new Schedule(LocalDate.of(2022, 10, 12), LocalTime.of(8, 0, 0), LocalTime.of(13, 29, 0));
        Schedule schedule2 = new Schedule(LocalDate.of(2022, 10, 13), LocalTime.of(8, 0, 0), LocalTime.of(13, 29, 0));
        participant1.addSchedule(schedule1);
        participant1.addSchedule(schedule2);

        Participant participant2 = new Participant("username2", "", false);
        Schedule schedule4 = new Schedule(LocalDate.of(2022, 10, 12), LocalTime.of(8, 0, 0), LocalTime.of(13, 29, 0));
        Schedule schedule5 = new Schedule(LocalDate.of(2022, 10, 13), LocalTime.of(8, 0, 0), LocalTime.of(11, 29, 0));
        Schedule schedule6 = new Schedule(LocalDate.of(2022, 10, 13), LocalTime.of(12, 0, 0), LocalTime.of(13, 29, 0));
        participant2.addSchedule(schedule4);
        participant2.addSchedule(schedule5);
        participant2.addSchedule(schedule6);

        Participant participant3 = new Participant("username3", "", false);
        Schedule schedule7 = new Schedule(LocalDate.of(2022, 10, 12), LocalTime.of(9, 0, 0), LocalTime.of(13, 29, 0));
        Schedule schedule8 = new Schedule(LocalDate.of(2022, 10, 14), LocalTime.of(8, 0, 0), LocalTime.of(13, 29, 0));
        Schedule schedule9 = new Schedule(LocalDate.of(2022, 10, 14), LocalTime.of(8, 0, 0), LocalTime.of(11, 29, 0));
        participant3.addSchedule(schedule7);
        participant3.addSchedule(schedule8);
        participant3.addSchedule(schedule9);

        //when
        LocalDate confirmedDate = LocalDate.of(2022, 10, 13);
        LocalTime startTime = LocalTime.of(8, 0, 0);
        LocalTime endTime = LocalTime.of(13, 30, 0);
        boolean b1 = participant1.findInTimeRangeSchedule(confirmedDate, startTime, endTime);
        boolean b2 = participant2.findInTimeRangeSchedule(confirmedDate, startTime, endTime);
        boolean b3 = participant3.findInTimeRangeSchedule(confirmedDate, startTime, endTime);

        //then
        assertThat(b1).isTrue();
        assertThat(b3).isFalse();
        assertThat(b2).isFalse();

    }

}