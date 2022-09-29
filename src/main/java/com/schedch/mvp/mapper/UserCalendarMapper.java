package com.schedch.mvp.mapper;

import com.schedch.mvp.dto.user.SubCalendarRes;
import com.schedch.mvp.dto.user.UserCalendarRes;
import com.schedch.mvp.model.UserCalendar;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = SubCalendarMapper.class)
public interface UserCalendarMapper {
    UserCalendarMapper INSTANCE = Mappers.getMapper(UserCalendarMapper.class);

    @Mapping(target = "subCalendarList", expression = "java(getSubCalendarList(userCalendar))")
    UserCalendarRes userCalendar2Res(UserCalendar userCalendar);

    default List<SubCalendarRes> getSubCalendarList(UserCalendar userCalendar) {
        SubCalendarMapper subCalendarMapper = SubCalendarMapper.INSTANCE;
        List<SubCalendarRes> subCalendarResList = userCalendar.getSubCalendarList().stream()
                .map(subCalendar -> subCalendarMapper.subCalendar2Res(subCalendar))
                .collect(Collectors.toList());

        return subCalendarResList;
    }

}
