package com.schedch.mvp.dto.user;

import com.schedch.mvp.adapter.TimeAdapter;
import com.schedch.mvp.dto.TopCountRes;
import com.schedch.mvp.dto.room.DayRoomTopRes;
import com.schedch.mvp.model.Participant;
import com.schedch.mvp.model.Room;
import com.schedch.mvp.model.TopTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserParticipatingRoomRes {

    private String roomTitle;
    private String roomUuid;
    private List<LocalDate> roomDates;
    private boolean isDayOnly;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<String> participants;
    private TopCountRes topOne;

    public UserParticipatingRoomRes(Participant participant) {
        Room room = participant.getRoom();
        this.roomTitle = participant.getRoomTitle();
        this.roomUuid = room.getUuid();
        this.isDayOnly = room.getStartTime() == null;
        if(room.getStartTime() != null) {
            this.startTime = room.getStartTime();
        }
        if (room.getEndTime() != null) {
            this.endTime = room.getEndTime().plusMinutes(1);
        }
    }

    public void setTopCountResByTopTime(List<TopTime> topTimes) {
        if(topTimes.isEmpty()) return;

        TopTime topTime = topTimes.get(0);
        Collections.sort(topTime.getParticipantNames());
        this.topOne = TopCountRes.builder()
                .count(topTime.getParticipantSize())
                .availableDate(topTime.getAvailableDate())
                .startTime(TimeAdapter.startBlock2Str(topTime.getStart()))
                .endTime(TimeAdapter.endBlock2Str(topTime.getStart() + topTime.getLen() - 1))
                .participants(topTime.getParticipantNames())
                .build();
    }

    public void setTopCountResByTopDate(List<DayRoomTopRes> topRes) {
        if(topRes.isEmpty()) return;

        DayRoomTopRes dayRoomTopRes = topRes.get(0);
        ArrayList<String> participants = new ArrayList<>(dayRoomTopRes.getParticipants());
        this.topOne = TopCountRes.builder()
                .count(dayRoomTopRes.getCount())
                .availableDate(dayRoomTopRes.getAvailableDate())
                .participants(participants)
                .build();
    }
}
