package com.schedch.mvp.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubCalendarRes {

    private Long id;
    private String subCalendarName;
    private boolean selected;

}
