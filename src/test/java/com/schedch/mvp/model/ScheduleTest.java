package com.schedch.mvp.model;

import com.schedch.mvp.dto.TimeBlockDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ScheduleTest {

    @Test
    public void create_schedule_success_test() throws Exception {
        //given
        LocalDate scheduledDate = LocalDate.of(2022, 4, 2);
        LocalTime startTime = LocalTime.of(4, 30, 0);
        LocalTime endTime = LocalTime.of(20, 0, 0);

        //when
        Schedule schedule = new Schedule(scheduledDate, startTime, endTime);

        //then
        assertThat(schedule.getStartTime()).isEqualTo(startTime);

    }

    @Test
    public void cutTimeTest() {
        //given
        LocalDate availableDate = LocalDate.of(2022, 4, 20);
        LocalTime startTime = LocalTime.of(6, 30, 0);
        LocalTime endTime = LocalTime.of(9, 0, 0);
        Schedule schedule = new Schedule(availableDate, startTime, endTime);

        //when
        List<Integer> list = schedule.cutTime(30);

        //then
        assertThat(list.size()).isEqualTo(6);
        assertThat(list.get(0)).isEqualTo(13);
        assertThat(list.get(list.size() - 1)).isEqualTo(18);

    }

    @Test
    void create_timeBlockDto_test() {
        //given
        LocalDate availableDate = LocalDate.of(2022, 4, 20);
        LocalTime startTime = LocalTime.of(6, 0, 0);
        LocalTime endTime = LocalTime.of(11, 30, 0);
        Schedule schedule = new Schedule(availableDate, startTime, endTime);

        //when
        TimeBlockDto timeBlockDto = schedule.toTimeBlockDto(30);

        //then
        int listSize = timeBlockDto.getAvailableTimeList().size();
        assertThat(timeBlockDto.getAvailableDate()).isEqualTo(availableDate);
        assertThat(listSize).isEqualTo(12);
        assertThat(timeBlockDto.getAvailableTimeList().get(0)).isEqualTo(12);
        assertThat(timeBlockDto.getAvailableTimeList().get(listSize-1)).isEqualTo(23);


    }

}