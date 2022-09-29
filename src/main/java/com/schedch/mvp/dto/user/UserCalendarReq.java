package com.schedch.mvp.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UserCalendarReq {

    private Long id;
    private String calendarEmail;
    private List<SubCalendarReq> subCalendarList;

}
