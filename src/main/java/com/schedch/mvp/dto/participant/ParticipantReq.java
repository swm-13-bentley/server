package com.schedch.mvp.dto.participant;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ParticipantReq {

    private String participantName;
    private String password;

    public ParticipantReq(String participantName, String password) {
        this.participantName = participantName;
        this.password = password;
    }
}
