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
}
