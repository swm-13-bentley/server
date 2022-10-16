package com.schedch.mvp.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubCalendarRes {

    private Long subCalendarRes;
    private String subCalendarName;
    private boolean selected;

}
