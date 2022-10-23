package com.schedch.mvp.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    
    private String alarmEmail;

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

    public void setParticipantName(String participantName) {this.participantName = participantName;}

    public void setAlarmEmail(String alarmEmail) {
        this.alarmEmail = alarmEmail;
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
    }

    public Participant(User user) {
        this.participantName = user.getUsername();
        this.password = user.getPassword();
        this.isSignedIn = true;
        this.alarmEmail = user.getEmail();
    }

    public boolean checkPassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    public boolean findInTimeRangeSchedule(LocalDate confirmedDate, LocalTime startTime, LocalTime endTime) {
        LocalTime setEndTime = endTime.minusMinutes(1); //schedule endTime -> 일분씩 앞당겨서 저장되어 있음. 따라서 endTime도 일분 앞당겨서 비교

        List<Schedule> collect = scheduleList.stream()
                .filter(schedule -> schedule.getAvailableDate().isEqual(confirmedDate))
                .filter(schedule -> (
                        (schedule.getStartTime().compareTo(startTime) <= 0) && (schedule.getEndTime().compareTo(setEndTime) >= 0)))
                .collect(Collectors.toList());

        return collect.size() > 0;
    }

    public boolean findInDayRangeSchedule(LocalDate confirmedDate) {
        List<Schedule> collect = scheduleList.stream()
                .filter(schedule -> {
                    return schedule.getAvailableDate().isEqual(confirmedDate);
                })
                .collect(Collectors.toList());

        return collect.size() > 0;
    }
}
