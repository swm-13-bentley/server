package com.schedch.mvp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomDate {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    @JsonIgnore
    private Room room;

    @NotNull(message = "날짜는 비어있을 수 없습니다")
    private LocalDate scheduledDate;

    public RoomDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}
