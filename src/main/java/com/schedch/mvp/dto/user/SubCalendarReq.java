package com.schedch.mvp.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubCalendarReq {

    private Long subCalendarId;
    private boolean selected;

}
