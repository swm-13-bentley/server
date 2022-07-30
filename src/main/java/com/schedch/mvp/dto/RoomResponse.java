package com.schedch.mvp.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class RoomResponse {

    private String title;
    private List<LocalDate> dates = new ArrayList<>();
    private int startTime;
    private int endTime;
}
