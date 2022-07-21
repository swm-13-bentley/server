package com.schedch.mvp.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 현재는 각각의 일정을 반환하는 것이 아니라, 한 날짜에 있는 모든 일정을 합쳐서 주기 때문에 summary가 들어갈 수 없음
 */
@Data
public class CalendarScheduleDto {

//    private String summary;
    private LocalDate scheduledDate;
    private List<Integer> scheduledTimeList = new ArrayList<>();

    public CalendarScheduleDto(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }
}
