package com.schedch.mvp.dto.room;

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
    private List<String> participants = new ArrayList<>();
}
