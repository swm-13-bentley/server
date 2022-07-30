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
public class Room {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String uuid;

    @NotNull(message = "Room title is empty")
    private String title;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "room")
    @OrderBy(value = "scheduledDate ASC")
    private List<RoomDate> roomDates = new ArrayList<>();

    @NotNull(message = "Room startTime is empty")
    private LocalTime startTime;

    @NotNull(message = "Room endTime is empty")
    private LocalTime endTime;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "room")
    private List<Participant> participantList = new ArrayList<>();

    //연관관계 편의 메서드
    public void addDate(LocalDate localDate) {
        RoomDate roomDate = new RoomDate(localDate);
        roomDates.add(roomDate);
        roomDate.setRoom(this);
    }

    public void addParticipant(Participant participant) {
        participantList.add(participant);
        participant.setRoom(this);
    }

    public List<Participant> findUnSignedParticipant(String participantName) {
        return getParticipantList().stream()
                .filter(p -> p.isSignedIn() == false && p.getParticipantName().equals(participantName))
                .collect(Collectors.toList());
    }

    @Builder
    public Room(String title, List<RoomDate> roomDates, LocalTime startTime, LocalTime endTime) {
        this.uuid = UUID.randomUUID().toString();
        this.title = title;
        this.roomDates = roomDates;
        this.startTime = startTime;
        this.endTime = endTime;
    }

}
