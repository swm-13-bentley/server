package com.schedch.mvp.mapper;

import com.schedch.mvp.dto.participant.DayParticipantRes;
import com.schedch.mvp.model.Participant;
import com.schedch.mvp.model.Schedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface DayParticipantMapper {
    DayParticipantMapper INSTANCE = Mappers.getMapper(DayParticipantMapper.class);

    @Mapping(target = "participantName", expression = "java(participant.getParticipantName())")
    @Mapping(target = "availableDates", expression = "java(getLocalDateList(participant.getScheduleList()))")
    DayParticipantRes entity2Res(Participant participant);

    default List<LocalDate> getLocalDateList(List<Schedule> scheduleList) {
        return scheduleList.stream().map(Schedule::getAvailableDate)
                .collect(Collectors.toList());
    }
}
