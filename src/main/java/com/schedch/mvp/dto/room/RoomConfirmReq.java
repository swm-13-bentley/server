package com.schedch.mvp.dto.room;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
public class RoomConfirmReq {

    @NotNull(message = "confirmedDate cannot be empty")
    private LocalDate confirmedDate;

    private LocalTime startTime;

    private LocalTime endTime;

    @NotNull(message = "participantIdList cannot be empty")
    private List<Long> participantIdList;

}
