package com.schedch.mvp.dto.participant;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class ParticipantAlarmEmailReq {

    @NotNull(message = "participantName cannot be empty")
    private String participantName;

    @NotNull(message = "participantName cannot be empty")
    @Email
    private String alarmEmail;

}
