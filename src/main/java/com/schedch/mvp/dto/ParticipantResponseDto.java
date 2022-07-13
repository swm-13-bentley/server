package com.schedch.mvp.dto;

import com.schedch.mvp.model.Participant;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ParticipantResponseDto {

    private String participantName;
    private List<TimeBlockDto> timeBlockDtoList = new ArrayList<>();

    public ParticipantResponseDto(Participant participant) {
        this.participantName = participant.getParticipantName();
        this.timeBlockDtoList = participant.getScheduleList().stream()
                .map(schedule -> schedule.toTimeBlockDto(30))
                .collect(Collectors.toList());
    }
}
