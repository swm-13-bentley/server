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

    public String startBlock2Str(int block) {
        int hour = block/2;
        int min = block%2 * 30;

        return String.format("%02d:%02d:00", hour, min);
    }

    //유저에게 보이는 값을 반환할 때는 29, 59분이 아닌 정각으로 반환한다
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

    public LocalTime startBlock2lt(int block) {
        if(checkBlockOverDefault(block))
            block = reduceBlockDefault(block);

        int hour = block/2;
        int min = block%2 * 30;

        return LocalTime.of(hour, min);
    }

    //lt로 변환할 때는 db에 저장하기 위한 목적임으로 29, 59분 형태로 저장한다
    public LocalTime endBlock2lt(int block) {
        if(checkBlockOverDefault(block))
            block = reduceBlockDefault(block);

        int hour = block/2;
        int min = block%2 * 30;
        return LocalTime.of(hour, min).plusMinutes(29);
    }

    //블록의 크기가 24시를 넘어서는 새벽인지를 확인. default: 24시를 30분으로 분할 -> 47
    public boolean checkBlockOverDefault(int block) {
        return block > 47;
    }

    public int reduceBlockDefault(int block) {
        return block - 47;
    }
}
