package com.schedch.mvp.dto.room;

import com.schedch.mvp.dto.ParticipantResponseDto;
import com.schedch.mvp.model.Participant;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class GroupSeperateRes {

    private ParticipantResponseDto myself;
    private List<ParticipantResponseDto> others = new ArrayList<>();

    public GroupSeperateRes(List<Participant> participantList, String participantName) {
        for (Participant participant : participantList) {
            ParticipantResponseDto dto = new ParticipantResponseDto(participant);
            if (isRequester(participantName, participant)) {
                myself = dto;
                continue;
            }

            others.add(dto);
        }
    }

    public GroupSeperateRes(List<Participant> participantList, Long participantId) {
        for (Participant participant : participantList) {
            ParticipantResponseDto dto = new ParticipantResponseDto(participant);
            if (isRequester(participantId, participant)) {
                myself = dto;
                continue;
            }

            others.add(dto);
        }
    }

    private boolean isRequester(String participantName, Participant participant) {
        return participant.getParticipantName().equals(participantName) && participant.getUser() == null;
    }

    private boolean isRequester(Long participantId, Participant participant) {
        return participantId.equals(participant.getId());
    }
}
