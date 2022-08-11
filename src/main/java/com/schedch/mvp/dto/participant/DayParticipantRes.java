package com.schedch.mvp.dto.participant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor @AllArgsConstructor
public class DayParticipantRes {
    private String participantName;
    private List<LocalDate> availableDates;
}
