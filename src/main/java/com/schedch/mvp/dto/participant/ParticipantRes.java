package com.schedch.mvp.dto.participant;

import com.schedch.mvp.dto.TimeBlockDto;
import com.schedch.mvp.model.Participant;
import com.schedch.mvp.model.Schedule;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class ParticipantRes {

    private String participantName;
    private List<TimeBlockDto> available = new ArrayList<>();

    public ParticipantRes(Participant participant) {
        this.participantName = participant.getParticipantName();

        participant.getScheduleList().stream()
                .collect(Collectors.groupingBy(Schedule::getAvailableDate))
                .entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .forEach(entry -> {
                    LocalDate availableDate = entry.getKey();
                    List<Schedule> scheduleList = entry.getValue();
                    HashSet<Integer> availableTimeSet = new HashSet<>();
                    for (Schedule schedule : scheduleList) {
                        List<Integer> cutTimeList = schedule.cutTime(30);
                        cutTimeList.stream().forEach(i -> availableTimeSet.add(i));
                    }

                    this.available.add(TimeBlockDto.builder()
                            .availableDate(availableDate)
                            .availableTimeList(availableTimeSet.stream().sorted(Comparator.comparing(Integer::intValue)).collect(Collectors.toList()))
                            .build());
                });
    }


}
