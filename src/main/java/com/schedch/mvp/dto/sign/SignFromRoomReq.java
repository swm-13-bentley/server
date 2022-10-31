package com.schedch.mvp.dto.sign;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SignFromRoomReq {

    private String participantName;
    private String roomUuid;
}
