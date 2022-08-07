package com.schedch.mvp.dto.room;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor @AllArgsConstructor
public class RoomRequest {

    @NotNull(message = "Room title cannot be empty")
    String title;

    List<LocalDate> dates = new ArrayList<>();

    @NotNull(message = "room startTime cannot be empty")
    String startTime;

    @NotNull(message = "room endTime cannot be empty")
    String endTime;
}
