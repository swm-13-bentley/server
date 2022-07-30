package com.schedch.mvp.adapter;

import com.google.api.client.util.DateTime;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Component
public class TimeAdapter {

    public LocalDate dateTime2LocalDate(DateTime dateTime) {
        LocalDate localDate = LocalDate.parse(dateTime.toString().substring(0, 10), DateTimeFormatter.ISO_LOCAL_DATE);
        return localDate;
    }

    public LocalTime dateTime2LocalTime(DateTime dateTime) {
        LocalTime localTime = LocalTime.parse(dateTime.toString().substring(11, 19), DateTimeFormatter.ISO_LOCAL_TIME);
        return localTime;
    }

    public int localTime2TimeBlockInt(LocalTime localTime) {
        return (int) (localTime.getHour() * (60 / 30)
                + Math.floor(localTime.getMinute() / 30));
    }

    public int startLocalTime2TimeBlock(LocalTime localTime) {
        return (int) (localTime.getHour() * (60 / 30)
                + Math.floor(localTime.getMinute() / 30));
    }

    public int endLocalTime2TimeBlock(LocalTime localTime) {
        int block = (int) (localTime.getHour() * (60 / 30)
                + Math.floor(localTime.getMinute() / 30)) - 1;

        if(localTime.equals(LocalTime.of(23, 59, 0))) {
            block++;
        }

        return block;
    }

    public LocalTime timeBlock2LocalTime(int block) {
        int hour = block/2;
        int min = block%2 * 30;

        return LocalTime.of(hour, min, 0);
    }

    public String startBlock2Str(int block) {
        int hour = block/2;
        int min = block%2 * 30;

        return String.format("%02d:%02d:00", hour, min);
    }

    public String endBlock2Str(int block) {
        if(block == 47) {
            return "24:00:00";
        } else {
            block++;
            int hour = block/2;
            int min = block%2 * 30;
            return String.format("%02d:%02d:00", hour, min);
        }
    }
}
