package com.schedch.mvp.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room extends BaseEntity{
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String uuid;

    @NotNull(message = "Room: title cannot be empty")
    private String title;

    @NotNull(message = "Room: participant_limit cannot be empty")
    private int participantLimit;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "room")
    @OrderBy(value = "scheduledDate ASC")
    private List<RoomDate> roomDates = new ArrayList<>();

    private LocalTime startTime;

    private LocalTime endTime;

    private boolean confirmed;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "room")
    @OrderBy(value = "participantName ASC")
    private List<Participant> participantList = new ArrayList<>();

    //연관관계 편의 메서드
    public void addParticipant(Participant participant) {
        participantList.add(participant);
        participant.setRoom(this);
        participant.setRoomTitle(this.title);
    }

    public List<Participant> findUnSignedParticipant(String participantName) {
        return getParticipantList().stream()
                .filter(p -> p.isSignedIn() == false && p.getParticipantName().equals(participantName))
                .collect(Collectors.toList());
    }

    public List<LocalDate> getLocalDateList() {
        return roomDates.stream().map(RoomDate::getScheduledDate)
                .collect(Collectors.toList());
    }

    @Builder
    public Room(String title, List<RoomDate> roomDates, LocalTime startTime, LocalTime endTime) {
        this.uuid = UUID.randomUUID().toString();
        this.title = title;
        this.roomDates = roomDates;
        this.startTime = startTime;
        this.endTime = endTime;
        this.confirmed = false;

        //연관관계 맺어주기
        roomDates.stream().forEach(roomDate -> roomDate.setRoom(this));
    }

    public void setParticipantLimit(int participantLimit) {
        this.participantLimit = participantLimit;
    }

    public boolean canAddMember() {
        if(participantList.size() >= participantLimit) {
            return false;
        } else {
            return true;
        }
    }

    public boolean contains(User user) {
        List<Participant> collect = participantList.stream().filter(participant -> participant.isSignedIn())
                .filter(participant -> participant.getUser() == user).collect(Collectors.toList());

        return !collect.isEmpty();
    }
}
