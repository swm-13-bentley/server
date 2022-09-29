package com.schedch.mvp.mapper;

import com.schedch.mvp.dto.user.SubCalendarRes;
import com.schedch.mvp.model.SubCalendar;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface SubCalendarMapper {
    SubCalendarMapper INSTANCE = Mappers.getMapper(SubCalendarMapper.class);

    SubCalendarRes subCalendar2Res(SubCalendar subCalendar);

}
