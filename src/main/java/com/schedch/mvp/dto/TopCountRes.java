package com.schedch.mvp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopCountRes {
    private int count;
    private LocalDate availableDate;
    private String startTime;
    private String endTime;
    private List<String> participants;
}
