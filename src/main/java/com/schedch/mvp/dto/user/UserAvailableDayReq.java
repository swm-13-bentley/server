package com.schedch.mvp.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class UserAvailableDayReq {

    private String participantName;
    private List<LocalDate> availableDates;

}
