package com.schedch.mvp.dto.room;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.PriorityQueue;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DayRoomTopRes {
    private int count;
    private LocalDate availableDate;
    private PriorityQueue<String> participants;
}
