package com.schedch.mvp.adapter;

import com.google.api.client.util.DateTime;
import com.schedch.mvp.dto.TimeBlockDto;
import com.schedch.mvp.model.Schedule;
import org.springframework.stereotype.Component;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TimeAdapter {

    static Pattern datetimePattern = Pattern.compile("(\\d*)-(\\d*)-(\\d*)T(\\d*):(\\d*):(\\d*).(\\d\\d\\d)(.*)");
    public static LocalTime str2LocalTime(String str) {
        String hour = str.substring(0, 2);
        String time = str.substring(3, 5);
        return LocalTime.of(Integer.parseInt(hour), Integer.parseInt(time), 0);
    }

    public static LocalDate dateTime2LocalDate(DateTime dateTime) {
        LocalDateTime localDateTime = dateTime2LocalDateTime(dateTime);
        return localDateTime.toLocalDate();
    }

    public static LocalTime dateTime2LocalTime(DateTime dateTime) {
        LocalDateTime localDateTime = dateTime2LocalDateTime(dateTime);
        return localDateTime.toLocalTime();
    }

    public static LocalDateTime dateTime2LocalDateTime(DateTime dateTime) {
        Matcher matcher = datetimePattern.matcher(dateTime.toString());
        matcher.find();
        String offSet = matcher.group(8);

        if(offSet.equals("+09:00")) {
            LocalDateTime localDateTime = LocalDateTime.parse(dateTime.toString().substring(0, 19), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return localDateTime;
        }

        int year = Integer.parseInt(matcher.group(1));
        int month = Integer.parseInt(matcher.group(2));
        int dayOfMonth = Integer.parseInt(matcher.group(3));
        int hour = Integer.parseInt(matcher.group(4));
        int minute = Integer.parseInt(matcher.group(5));
        int second = Integer.parseInt(matcher.group(6));
        int nanoseconds = Integer.parseInt(matcher.group(7));
        ZonedDateTime zonedDateTime = ZonedDateTime.of(year, month, dayOfMonth, hour, minute, second, nanoseconds, ZoneOffset.of(offSet));

        ZonedDateTime seoulDateTime = zonedDateTime.withZoneSameInstant(ZoneOffset.of("+09:00"));
        return seoulDateTime.toLocalDateTime();
    }



    public static int localTime2TimeBlockInt(LocalTime localTime) {
        return (int) (localTime.getHour() * (60 / 30)
                + Math.floor(localTime.getMinute() / 30));
    }

    public static String startBlock2Str(int block) {
        if(block >= 48) block -= 48;
        int hour = block/2;
        int min = block%2 * 30;

        return String.format("%02d:%02d:00", hour, min);
    }

    //유저에게 보이는 값을 반환할 때는 29, 59분이 아닌 정각으로 반환한다
    public static String endBlock2Str(int block) {
        if(block >= 48) block -= 48;
        if(block == 47) {
            return "24:00:00";
        } else {
            block++;
            int hour = block/2;
            int min = block%2 * 30;
            return String.format("%02d:%02d:00", hour, min);
        }
    }

    public static LocalTime startBlock2lt(int block) {
        if(checkBlockOverDefault(block))
            block = reduceBlockDefault(block);

        int hour = block/2;
        int min = block%2 * 30;

        return LocalTime.of(hour, min);
    }

    //lt로 변환할 때는 db에 저장하기 위한 목적임으로 29, 59분 형태로 저장한다
    public static LocalTime endBlock2lt(int block) {
        if(checkBlockOverDefault(block))
            block = reduceBlockDefault(block);

        int hour = block/2;
        int min = block%2 * 30;
        return LocalTime.of(hour, min).plusMinutes(29);
    }

    //블록의 크기가 24시를 넘어서는 새벽인지를 확인. default: 24시를 30분으로 분할 -> 48
    public static boolean checkBlockOverDefault(int block) {
        return block >= 48;
    }

    public static int reduceBlockDefault(int block) {
        return block - 48;
    }

    public static List<Schedule> changeTimeBlockDtoToSchedule(TimeBlockDto timeBlockDto, LocalTime roomStartTime) {
        List<Schedule> scheduleList = new ArrayList<>();
        LocalDate availableDate = timeBlockDto.getAvailableDate();
        List<Integer> availableTimeList = timeBlockDto.getAvailableTimeList();
        if(!availableTimeList.isEmpty()) {
            int start = availableTimeList.get(0);
            int end = start;

            for (int i = 1; i <= availableTimeList.size(); i++) {
                if(i == availableTimeList.size()) {
                    LocalTime startTime = TimeAdapter.startBlock2lt(start);
                    scheduleList.add(new Schedule(availableDate, startTime, TimeAdapter.endBlock2lt(end), roomStartTime));
                    return scheduleList;
                }
                if (availableTimeList.get(i) != end + 1) {//불연속 or 마지막
                    scheduleList.add(new Schedule(availableDate, TimeAdapter.startBlock2lt(start), TimeAdapter.endBlock2lt(end), roomStartTime));
                    start = availableTimeList.get(i);
                }
                end = availableTimeList.get(i);
            }
        }
        return scheduleList;
    }

    public static DateTime localDateAndTime2DateTime(LocalDate localDate, LocalTime localTime, String offsetId) {
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        DateTime dateTime = new DateTime(1000 * localDateTime.toEpochSecond(ZoneOffset.of(offsetId)));
        return dateTime;
    }
}
