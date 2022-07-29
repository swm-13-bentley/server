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
public class Schedule {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id")
    private Participant participant;

    @NotNull(message = "Schedule scheduledDate cannot be empty")
    private LocalDate availableDate;

    @NotNull(message = "Schedule startTime cannot be empty")
    private LocalTime startTime;

    @NotNull(message = "Schedule endTime cannot be empty")
    private LocalTime endTime;


    public Schedule(LocalDate availableDate, LocalTime startTime, LocalTime endTime) {
        this.availableDate = availableDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Schedule(LocalDate availableDate, int startTimeInt, int endTimeInt) {
        this.availableDate = availableDate;
        this.startTime = toLocalTime(startTimeInt, 30);
        this.endTime = toLocalTime(endTimeInt, 30).plusMinutes(30);
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

        return IntStream.range(start, end+1).boxed().collect(Collectors.toList());
    }

    public LocalTime toLocalTime(int timeInteger, int unit) {
        return LocalTime.of(timeInteger / (60/unit), unit * (timeInteger % (60/unit)), 0);
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

}
