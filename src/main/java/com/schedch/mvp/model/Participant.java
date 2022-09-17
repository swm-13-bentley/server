package com.schedch.mvp.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Participant extends BaseEntity{

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private boolean isSignedIn;

    @NotNull
    private String participantName;

    private String password;

    private String roomTitle;

    private boolean isConfirmed;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "participant")
    @OrderBy(value = "availableDate ASC")
    private List<Schedule> scheduleList = new ArrayList<>();

    public void setUser(User user) {
        this.user = user;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public void setRoomTitle(String roomTitle) {
        this.roomTitle = roomTitle;
    }

    //연관관계 편의 메서드
    public void addSchedule(Schedule schedule) {
        scheduleList.add(schedule);
        schedule.setParticipant(this);
    }

    public void emptySchedules() {
        this.scheduleList.clear();
    }

    public Participant(String participantName, String password, boolean isSignedIn) {
        this.participantName = participantName;
        this.password = password;
        this.isSignedIn = isSignedIn;
        this.isConfirmed = false;
    }

    public Participant(User user) {
        this.participantName = user.getUsername();
        this.password = "";
        this.isSignedIn = true;
        this.isConfirmed = false;
    }

    public boolean checkPassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }
}
