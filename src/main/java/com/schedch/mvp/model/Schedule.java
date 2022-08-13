package com.schedch.mvp.model;

import com.schedch.mvp.dto.TimeBlockDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule extends BaseEntity{

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id")
    private Participant participant;

    @NotNull(message = "Schedule scheduledDate cannot be empty")
    private LocalDate availableDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private LocalTime roomStartTime;

    public Schedule(LocalDate availableDate) {
        this.availableDate = availableDate;
    }
    public Schedule(LocalDate availableDate, LocalTime startTime, LocalTime endTime) {
        this.availableDate = availableDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Schedule(LocalDate availableDate, LocalTime startTime, LocalTime endTime, LocalTime roomStartTime) {
        this.availableDate = availableDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.roomStartTime = roomStartTime;
    }

    public TimeBlockDto toTimeBlockDto(int unit) {
        return TimeBlockDto.builder()
                .availableDate(this.availableDate)
                .availableTimeList(cutTime(unit))
                .build();
    }

    public List<Integer> cutTime(int unit) {
        int start = (int) (startTime.getHour() * (60/unit)
                        + Math.floor(startTime.getMinute() / unit));

        int end = (int) (endTime.getHour() * (60/unit)
                + Math.floor(endTime.getMinute() / unit));

        if(roomStartTime != null) {
            if(startTime.isBefore(roomStartTime)) start += 48;
            if(endTime.isBefore(roomStartTime)) end += 48;
        }

        return IntStream.range(start, end+1).boxed().collect(Collectors.toList());
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

}
