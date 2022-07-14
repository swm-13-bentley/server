package com.schedch.mvp.model;

import com.schedch.mvp.dto.TimeBlockDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ScheduleTest {
    LocalDate availableDate = LocalDate.of(2022, 4, 2);
    LocalTime startTime = LocalTime.of(4, 30, 0);
    LocalTime endTime = LocalTime.of(20, 0, 0);

    @Test
    public void create_schedule_success_test() throws Exception {
        //given

        //when
        Schedule schedule = new Schedule(availableDate, startTime, endTime);

        //then
        assertThat(schedule.getStartTime()).isEqualTo(startTime);

    }

    @Test
    public void cutTimeTest() {
        //given
        Schedule schedule = new Schedule(availableDate, startTime, endTime);

        //when
        List<Integer> list = schedule.cutTime(30);

        //then
        assertThat(list.size()).isEqualTo(32);
        assertThat(list.get(0)).isEqualTo(9);
        assertThat(list.get(list.size() - 1)).isEqualTo(40);

    }

    @Test
    void create_timeBlockDto_test() {
        //given
        Schedule schedule = new Schedule(availableDate, startTime, endTime);

        //when
        TimeBlockDto timeBlockDto = schedule.toTimeBlockDto(30);

        //then
        int listSize = timeBlockDto.getAvailableTimeList().size();
        assertThat(timeBlockDto.getAvailableDate()).isEqualTo(availableDate);
        assertThat(listSize).isEqualTo(32);
        assertThat(timeBlockDto.getAvailableTimeList().get(0)).isEqualTo(9);
        assertThat(timeBlockDto.getAvailableTimeList().get(listSize-1)).isEqualTo(40);


    }

    @Test
    void from_timeInt_to_localDate() {
        //given
        int startTimeInt = 1; //0시 30분
        int endTimeInt = 11; //5시 30분

        //when
        Schedule schedule = new Schedule(availableDate, startTimeInt, endTimeInt);

        //then
        assertThat(schedule.getStartTime()).isEqualTo(LocalTime.of(0, 30, 0));
        assertThat(schedule.getEndTime()).isEqualTo(LocalTime.of(5, 30, 0));
    }

}