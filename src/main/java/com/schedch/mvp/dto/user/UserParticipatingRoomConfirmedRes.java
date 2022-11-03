package com.schedch.mvp.dto.user;

import com.schedch.mvp.model.Room;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserParticipatingRoomConfirmedRes {

    private String roomTitle;
    private String roomUuid;
    private boolean isDayOnly;
    private List<String> participants;
    private LocalDate confirmedDate;
    private LocalTime confirmedStartTime;
    private LocalTime confirmedEndTime;

    public UserParticipatingRoomConfirmedRes(String title, Room room, List<String> participants) {
        this.roomTitle = title;
        this.roomUuid = room.getUuid();
        this.isDayOnly = room.getStartTime() == null;
        this.participants = participants;
        this.confirmedDate = room.getConfirmedDate();
        this.confirmedStartTime = room.getConfirmedStartTime();
        this.confirmedEndTime = room.getConfirmedEndTime();
    }
}
