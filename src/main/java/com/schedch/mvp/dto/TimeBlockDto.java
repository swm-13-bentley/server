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

    private LocalDate availableDate;
    private List<Integer> availableTimeList = new ArrayList<>();

    @Builder
    public TimeBlockDto(LocalDate availableDate, List<Integer> availableTimeList) {
        this.availableDate = availableDate;
        this.availableTimeList = availableTimeList;
    }
}
