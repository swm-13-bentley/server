package com.schedch.mvp.dto.room;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class RoomConfirmReq {

    @NotNull(message = "confirmedDate cannot be empty")
    private LocalDate confirmedDate;

    private String startTime;

    private String endTime;

//    @NotNull(message = "participantIdList cannot be empty")
//    private List<Long> participantIdList;

}
