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
@Schema(description = "방 생성을 요청할 때 사용하는 DTO")
public class RoomRequestDto {

    @NotNull(message = "Room title cannot be empty")
    @Schema(description = "방 제목", example = "sample title")
    String title;

    @Schema(description = "방의 날짜들", type = "List", example = "[\"2022-07-15\", \"2022-07-16\", \"2022-08-01\"]")
    List<LocalDate> dates = new ArrayList<>();

    @NotNull(message = "room startTime cannot be empty")
    @Schema(description = "방의 시작 시간(HH:mm:ss)", type = "string", example = "11:30:00")
    LocalTime startTime;

    @NotNull(message = "room endTime cannot be empty")
    @Schema(description = "방의 끝 시간(HH:mm:ss)", type = "string", example = "23:00:00")
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
