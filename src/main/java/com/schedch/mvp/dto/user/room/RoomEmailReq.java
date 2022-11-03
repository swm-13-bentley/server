package com.schedch.mvp.dto.user.room;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoomEmailReq {

    private String roomUuid;
    private int alarmNumber;

}
