package com.schedch.mvp.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class UserCalendarListRes {

    private String mainCalendarEmail;
    private String mainCalendarChannel;
    private List<UserCalendarRes> calendarList = new ArrayList<>();
}
