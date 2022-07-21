package com.schedch.mvp.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CalendarResponse {

    private String summary;
    private String calendarId;
    private String colorCode;
    private List<CalendarScheduleDto> events = new ArrayList<>();

    public CalendarResponse(String summary, String calendarId, String colorCode) {
        this.summary = summary;
        this.calendarId = calendarId;
        this.colorCode = colorCode;
    }
}
