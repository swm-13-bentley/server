package com.schedch.mvp.dto;

import com.schedch.mvp.model.Participant;
import com.schedch.mvp.model.Schedule;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class ParticipantResponseDto {

    private String participantName;
    private List<TimeBlockDto> timeBlockDtoList = new ArrayList<>();

    public ParticipantResponseDto(Participant participant) {
        this.participantName = participant.getParticipantName();
//        Map<LocalDate, List<Schedule>> map = participant.getScheduleList().stream()
//                .collect(Collectors.groupingBy(Schedule::getAvailableDate));

        participant.getScheduleList().stream()
                .collect(Collectors.groupingBy(Schedule::getAvailableDate))
                .entrySet().stream()
                .sorted((a, b) -> a.getKey().compareTo(b.getKey()))
                .forEach(entry -> {
                    LocalDate availableDate = entry.getKey();
                    List<Schedule> scheduleList = entry.getValue();
                    HashSet<Integer> availableTimeSet = new HashSet<>();
                    for (Schedule schedule : scheduleList) {
                        List<Integer> cutTimeList = schedule.cutTime(30);
                        cutTimeList.stream().forEach(i -> availableTimeSet.add(i));
                    }

                    this.timeBlockDtoList.add(TimeBlockDto.builder()
                            .availableDate(availableDate)
                            .availableTimeList(availableTimeSet.stream().sorted(Comparator.comparing(Integer::intValue)).collect(Collectors.toList()))
                            .build());
                });
    }


}