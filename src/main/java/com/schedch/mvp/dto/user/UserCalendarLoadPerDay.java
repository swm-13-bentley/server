package com.schedch.mvp.dto.user;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.schedch.mvp.adapter.TimeAdapter;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@Slf4j
public class UserCalendarLoadPerDay {

    private List<SchedulePerDay> scheduleList = new ArrayList();

    public UserCalendarLoadPerDay(List<Event> eventList, int roomStartBlock, int roomEndBlock) {
        HashMap<LocalDate, SchedulePerDay> map = new HashMap<>();
        for (Event event : eventList) { //각 일정 별로
            String summary = event.getSummary() == null ? "이름 없음" : event.getSummary();

            DateTime start = event.getStart().getDateTime();
            DateTime end = event.getEnd().getDateTime();

            LocalDate startDate = TimeAdapter.dateTime2LocalDate(start);
            LocalTime startTime = TimeAdapter.dateTime2LocalTime(start);
            int startBlock = TimeAdapter.localTime2TimeBlockInt(startTime);
            LocalDate endDate = TimeAdapter.dateTime2LocalDate(end);
            LocalTime endTime = TimeAdapter.dateTime2LocalTime(end);
            int endBlock = TimeAdapter.localTime2TimeBlockInt(endTime); //끝 시간까지 포함 (12시 15분인 경우, 24)
            if(endTime.getMinute() == 0 || endTime.getMinute() == 30) {
                //12시 30분인 경우 endBlock이 25로 계산되어 있음 -> 24로 변경해주는 것이 옳음
                endBlock--;
            }

            if (startDate.isEqual(endDate)) { //시작일 == 종료일
                if (endBlock < roomStartBlock) {
                    continue;
                }
                startBlock = Math.max(startBlock, roomStartBlock);
                endBlock = Math.min(endBlock, roomEndBlock);
                ScheduleInfo scheduleInfo = new ScheduleInfo(summary, startBlock, endBlock);
                if(!map.containsKey(startDate)) {
                    map.put(startDate, new SchedulePerDay(startDate));
                }
                map.get(startDate).scheduleInfoList.add(scheduleInfo);
                continue;
            }

            //start date blocks
            int minStartBlock;
            int maxEndBlock;
            if(startBlock < roomEndBlock) {
                minStartBlock = Math.max(startBlock, roomStartBlock);
                 maxEndBlock = roomEndBlock;
                ScheduleInfo scheduleInfo = new ScheduleInfo(summary, minStartBlock, maxEndBlock);
                if (!map.containsKey(startDate)) {
                    map.put(startDate, new SchedulePerDay(startDate));
                }
                map.get(startDate).scheduleInfoList.add(scheduleInfo);
            }

            //middle
            int plus = 1;
            minStartBlock = roomStartBlock;
            maxEndBlock = roomEndBlock;
            while(startDate.plusDays(plus).isBefore(endDate)) {
                LocalDate nowDate = startDate.plusDays(plus);
                ScheduleInfo midScheduleInfo = new ScheduleInfo(summary, minStartBlock, maxEndBlock);
                if(!map.containsKey(nowDate)) {
                    map.put(nowDate, new SchedulePerDay(nowDate));
                }
                map.get(nowDate).scheduleInfoList.add(midScheduleInfo);
                plus++;
            }

            //end date blocks
            if(endBlock >= roomStartBlock) {//일정이 끝나는 시간이 방 시작 시간보다 늦을 때만
                minStartBlock = roomStartBlock;
                maxEndBlock = Math.min(endBlock, roomEndBlock);
                ScheduleInfo endScheduleInfo = new ScheduleInfo(summary, minStartBlock, maxEndBlock);
                if (!map.containsKey(endDate)) {
                    map.put(endDate, new SchedulePerDay(endDate));
                }
                map.get(endDate).scheduleInfoList.add(endScheduleInfo);
            }

        }
        map.entrySet().stream().sorted(Map.Entry.comparingByKey())
                        .forEach(entry -> scheduleList.add(entry.getValue()));
    }

    @Data
    @NoArgsConstructor
    public static class SchedulePerDay {

        private LocalDate scheduledDate;
        private List<ScheduleInfo> scheduleInfoList = new ArrayList<>();

        public SchedulePerDay(LocalDate scheduledDate) {
            this.scheduledDate = scheduledDate;
        }
    }

    @Data
    @NoArgsConstructor
    public static class ScheduleInfo {

        private String summary;
        private int startTime;
        private int endTime;

        public ScheduleInfo(String summary, int startTime, int endTime) {
            this.summary = summary;
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }

}
