package com.schedch.mvp.dto;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.schedch.mvp.adapter.LocalDateAdapter;
import com.schedch.mvp.adapter.LocalTimeAdapter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class TimeBlockDto {

    @Schema(description = "가능한 일정의 날짜", type = "String", example = "2022-07-15")
    private LocalDate availableDate;

    @Schema(description = "가능한 일정의 시간(30분 단위로 쪼갠 것)", type = "List", example = "[4, 5, 6, 17, 18, 19, 38, 39]")
    private List<Integer> availableTimeList = new ArrayList<>();

    @Builder
    public TimeBlockDto(LocalDate availableDate, List<Integer> availableTimeList) {
        this.availableDate = availableDate;
        this.availableTimeList = availableTimeList;
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
