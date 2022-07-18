package com.schedch.mvp.dto;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.schedch.mvp.adapter.LocalDateAdapter;
import com.schedch.mvp.adapter.LocalTimeAdapter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class AvailableRequestDto {

    @Schema(description = "참가자 이름", type = "string", example = "participant1")
    private String participantName;

    @Schema(description = "비밀번호", type = "string", example = "asdf123!")
    private String password;

    private List<TimeBlockDto> available = new ArrayList<>();

    public String toString() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                .create();

        return gson.toJson(this);
    }
}
