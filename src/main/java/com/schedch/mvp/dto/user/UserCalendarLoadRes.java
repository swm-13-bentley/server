package com.schedch.mvp.dto.user;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.schedch.mvp.adapter.TimeAdapter;
import com.schedch.mvp.dto.TimeBlockDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
@NoArgsConstructor
public class UserCalendarLoadRes {

    private String summary;
    private List<TimeBlockDto> timeBlockDtoList = new ArrayList<>();

    public UserCalendarLoadRes(Event eventItem) {
        this.summary = eventItem.getSummary();

        DateTime start = eventItem.getStart().getDateTime();
        DateTime end = eventItem.getEnd().getDateTime();

        LocalDate startDate = TimeAdapter.dateTime2LocalDate(start);
        LocalTime startTime = TimeAdapter.dateTime2LocalTime(start);
        int startBlock = TimeAdapter.localTime2TimeBlockInt(startTime);
        LocalDate endDate = TimeAdapter.dateTime2LocalDate(end);
        LocalTime endTime = TimeAdapter.dateTime2LocalTime(end);
        int endBlock = TimeAdapter.localTime2TimeBlockInt(endTime); //끝 시간까지 포함 (12시 30분인 경우, 25)
        if(endTime.getMinute() != 0 && endTime.getMinute() != 30) {
            //0분, 30분이 아닐 경우 endBlock++ 해줘야 (10시 15분 끝나는 경우 -> block = 20
            //IntStream.range(0, 20)인 경우 19까지만 포함되기 때문에 ++ 해줘서 20까지 포함되게 해줘야 함
            endBlock++;
        }

        if (startDate.isEqual(endDate)) { //시작일 == 종료일
            TimeBlockDto timeBlockDto = new TimeBlockDto();
            timeBlockDto.setAvailableDate(startDate);
            timeBlockDto.setAvailableTimeList(
                    IntStream.range(startBlock, endBlock).boxed().collect(Collectors.toList()) //range는 end를 포함하지 않음
            );

            timeBlockDtoList.add(timeBlockDto);
            return;
        }

        //start date blocks
        TimeBlockDto startTimeBlockDto = new TimeBlockDto();
        startTimeBlockDto.setAvailableDate(startDate);
        startTimeBlockDto.setAvailableTimeList(
                IntStream.range(startBlock, 49).boxed().collect(Collectors.toList()) //range는 end를 포함하지 않음 (48 + 1)
        );
        timeBlockDtoList.add(startTimeBlockDto);

        //middle
        int plus = 1;
        while(startDate.plusDays(plus).isBefore(endDate)) {
            TimeBlockDto midTimeBlockDto = new TimeBlockDto();
            midTimeBlockDto.setAvailableDate(startDate.plusDays(1));
            midTimeBlockDto.setAvailableTimeList(
                    IntStream.range(0, 49).boxed().collect(Collectors.toList())
            );
            timeBlockDtoList.add(midTimeBlockDto);
            plus++;
        }

        //end date blocks
        TimeBlockDto endTimeBlockDto = new TimeBlockDto();
        endTimeBlockDto.setAvailableDate(endDate);
        endTimeBlockDto.setAvailableTimeList(
                IntStream.range(0, endBlock).boxed().collect(Collectors.toList()) //range는 end를 포함하지 않음
        );
        timeBlockDtoList.add(endTimeBlockDto);
    }
}
