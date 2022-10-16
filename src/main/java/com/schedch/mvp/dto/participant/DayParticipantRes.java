package com.schedch.mvp.dto.participant;

import com.schedch.mvp.model.Participant;
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

    public DayParticipantRes(Participant participant) {
        this.participantName = participant.getParticipantName();

        participant.getScheduleList().stream()
                .forEach(schedule -> {
                    availableDates.add(schedule.getAvailableDate());
                });
    }
}
