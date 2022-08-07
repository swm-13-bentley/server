package com.schedch.mvp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeCountResponse {
    private int count;
    private LocalDate availableDate;
    private String startTime;
    private String endTime;
}
