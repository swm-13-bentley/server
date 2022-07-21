package com.schedch.mvp.dto;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.schedch.mvp.adapter.LocalDateAdapter;
import com.schedch.mvp.adapter.LocalTimeAdapter;
import com.schedch.mvp.model.Room;
import com.schedch.mvp.model.RoomDate;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
public class RoomResponseDto {

    private String title;
    private List<LocalDate> dates = new ArrayList<>();

//    @Schema(description = "방의 시작 시간(HH:mm:ss)", type = "string", example = "11:30:00")
//    private LocalTime startTime;

//    @Schema(description = "방의 끝 시간(HH:mm:ss)", type = "string", example = "23:00:00")
//    private LocalTime endTime;

    private int startTime;
    private int endTime;

//    public RoomResponseDto(Room room) {
//        this.title = room.getTitle();
//        this.dates = room.getRoomDates().stream()
//                .map(RoomDate::getScheduledDate)
//                .collect(Collectors.toList());
//        this.startTime = room.getStartTime();
//        this.endTime = room.getEndTime();
//    }

    public RoomResponseDto(Room room) {
        this.title = room.getTitle();
        this.dates = room.getRoomDates().stream()
                .map(RoomDate::getScheduledDate)
                .collect(Collectors.toList());
        this.startTime = toTimeBlockInteger(room.getStartTime());
        this.endTime = toTimeBlockInteger(room.getEndTime()) - 1;
    }

    public int toTimeBlockInteger(LocalTime time) {
        int block = (int) (time.getHour() * (60/30)
                + Math.floor(time.getMinute() / 30));

        return block;
    }

//    @Builder
//    public RoomResponseDto(String title, List<LocalDate> dates, LocalTime startTime, LocalTime endTime) {
//        this.title = title;
//        this.dates = dates;
//        this.startTime = startTime;
//        this.endTime = endTime;
//    }

    @Builder
    public RoomResponseDto(String title, List<LocalDate> dates, int startTime, int endTime) {
        this.title = title;
        this.dates = dates;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String toString() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                .create();

        return gson.toJson(this);
    }
}
