package com.schedch.mvp.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UserCalendarRes {

    private Long id;
    private String calendarEmail;
    private boolean mainCalendar;
    private String calendarChannel;
    private List<SubCalendarRes> subCalendarList;

}
