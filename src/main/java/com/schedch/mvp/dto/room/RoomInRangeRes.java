package com.schedch.mvp.dto.room;

import com.schedch.mvp.model.Participant;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class RoomInRangeRes {

    private List<ParticipantWithIdRes> inRange = new ArrayList<>();
    private List<ParticipantWithIdRes> notInRange = new ArrayList<>();

    public RoomInRangeRes(List<Participant> inRange, List<Participant> notInRange) {
        this.inRange = inRange.stream().map(participant -> new ParticipantWithIdRes(participant.getId(), participant.getParticipantName())).collect(Collectors.toList());
        this.notInRange = notInRange.stream().map(participant -> new ParticipantWithIdRes(participant.getId(), participant.getParticipantName())).collect(Collectors.toList());
    }

    @Data
    public static class ParticipantWithIdRes {

        private Long id;
        private String participantName;

        public ParticipantWithIdRes(Long id, String participantName) {
            this.id = id;
            this.participantName = participantName;
        }
    }
}
