package com.schedch.mvp.dto.room;

import com.schedch.mvp.dto.participant.DayParticipantRes;
import com.schedch.mvp.model.Participant;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class DayGroupSeperateRes {

    private DayParticipantRes myself;
    private List<DayParticipantRes> others = new ArrayList<>();

    public DayGroupSeperateRes(List<Participant> participantList, String participantName) {
        for (Participant participant : participantList) {
            DayParticipantRes dto = new DayParticipantRes(participant);
            if (isRequester(participantName, participant)) {
                myself = dto;
                continue;
            }

            others.add(dto);
        }
    }

    public DayGroupSeperateRes(List<Participant> participantList, Long participantId) {
        for (Participant participant : participantList) {
            DayParticipantRes dto = new DayParticipantRes(participant);
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
