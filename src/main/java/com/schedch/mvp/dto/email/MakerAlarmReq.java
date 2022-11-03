package com.schedch.mvp.dto.email;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MakerAlarmReq {

    private String mailTo;
    private String roomTitle;
    private String roomLink;
    private int alarmNumber;

    @Builder
    public MakerAlarmReq(String mailTo, String roomTitle, String roomLink, int alarmNumber) {
        this.mailTo = mailTo;
        this.roomTitle = roomTitle;
        this.roomLink = roomLink;
        this.alarmNumber = alarmNumber;
    }
}
