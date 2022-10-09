package com.schedch.mvp.dto.room;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
public class RoomConfirmReq {

    @NotNull(message = "confirmedDate cannot be empty")
    private LocalDate confirmedDate;

    private LocalTime startTime;
    private LocalTime endTime;

}
