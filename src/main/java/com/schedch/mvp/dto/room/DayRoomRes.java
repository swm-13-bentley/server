package com.schedch.mvp.dto.room;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor @AllArgsConstructor
public class DayRoomRes {
    private int count;
    private String title;
    private List<LocalDate> dates = new ArrayList<>();
    private List<String> participants = new ArrayList<>();
}
