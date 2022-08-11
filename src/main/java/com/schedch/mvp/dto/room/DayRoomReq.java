package com.schedch.mvp.dto.room;

import com.schedch.mvp.model.Room;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor @AllArgsConstructor
public class DayRoomReq {

    @NotNull(message = "Room title cannot be empty")
    String title;

    List<LocalDate> dates = new ArrayList<>();

}
