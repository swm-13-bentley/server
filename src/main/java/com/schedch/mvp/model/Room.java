package com.schedch.mvp.model;

import com.schedch.mvp.dto.RoomRequestDto;
import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor
public class Room {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String uuid;

    @NotNull
    private String title;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "room")
    private List<RoomDate> roomDates = new ArrayList<>();

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    //연관관계 편의 메서드
    public void addRoomDate(RoomDate roomDate) {
        roomDates.add(roomDate);
        roomDate.setRoom(this);
    }

    @Builder
    public Room(String title, List<RoomDate> roomDates, LocalTime startTime, LocalTime endTime) {
        this.uuid = UUID.randomUUID().toString();
        this.title = title;
        this.roomDates = roomDates;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Room(RoomRequestDto roomRequestDto) {
        this.uuid = UUID.randomUUID().toString();
        this.title = roomRequestDto.getTitle();
        roomRequestDto.getDates().stream()
                .map(localDate -> new RoomDate(localDate))
                .forEach(roomDate -> addRoomDate(roomDate));
        this.startTime = roomRequestDto.getStartTime();
        this.endTime = roomRequestDto.getEndTime();

    }
}
