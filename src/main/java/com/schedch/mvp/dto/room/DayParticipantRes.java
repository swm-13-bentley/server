package com.schedch.mvp.dto.room;

import com.schedch.mvp.model.Participant;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class DayParticipantRes {

    private String participantName;
    private List<LocalDate> availableDates = new ArrayList<>();

    public DayParticipantRes(Participant participant) {
        this.participantName = participant.getParticipantName();

        participant.getScheduleList().stream()
                .forEach(schedule -> {
                    availableDates.add(schedule.getAvailableDate());
                });
    }


}
