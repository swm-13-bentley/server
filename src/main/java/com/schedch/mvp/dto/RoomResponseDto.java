package com.schedch.mvp.dto;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.schedch.mvp.adapter.LocalDateAdapter;
import com.schedch.mvp.adapter.LocalTimeAdapter;
import com.schedch.mvp.model.Room;
import com.schedch.mvp.model.RoomDate;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class RoomResponseDto {
    private String title;
    private List<LocalDate> dates = new ArrayList<>();
    private LocalTime startTime;
    private LocalTime endTime;

    public RoomResponseDto(Room room) {
        this.title = room.getTitle();
        this.dates = room.getRoomDates().stream()
                .map(RoomDate::getScheduledDate)
                .collect(Collectors.toList());
        this.startTime = room.getStartTime();
        this.endTime = room.getEndTime();
    }

    @Builder
    public RoomResponseDto(String title, List<LocalDate> dates, LocalTime startTime, LocalTime endTime) {
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
