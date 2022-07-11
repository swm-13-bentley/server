package com.schedch.mvp.dto;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.schedch.mvp.adapter.LocalDateAdapter;
import com.schedch.mvp.adapter.LocalTimeAdapter;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class RoomRequestDto {
    String title;
    List<LocalDate> dates = new ArrayList<>();
    LocalTime startTime;
    LocalTime endTime;

    @Builder
    public RoomRequestDto(String title, List<LocalDate> dates, LocalTime startTime, LocalTime endTime) {
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
