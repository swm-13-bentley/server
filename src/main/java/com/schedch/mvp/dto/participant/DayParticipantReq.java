package com.schedch.mvp.dto.participant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor @AllArgsConstructor
public class DayParticipantReq {

    @NotNull(message = "participantName cannot be empty")
    private String participantName;

    @NotNull(message = "password cannot be empty")
    private String password;

    private List<LocalDate> availableDates;
}
