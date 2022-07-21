package com.schedch.mvp.dto;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.schedch.mvp.adapter.LocalDateAdapter;
import com.schedch.mvp.adapter.LocalTimeAdapter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class RoomRequestDto {

    @NotNull(message = "Room title cannot be empty")
    String title;

    List<LocalDate> dates = new ArrayList<>();

    @NotNull(message = "room startTime cannot be empty")
    String startTime;

    @NotNull(message = "room endTime cannot be empty")
    String endTime;

    @Builder
    public RoomRequestDto(String title, List<LocalDate> dates, String startTime, String endTime) {
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
